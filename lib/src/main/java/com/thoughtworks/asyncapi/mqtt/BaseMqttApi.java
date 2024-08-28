package com.thoughtworks.asyncapi.mqtt;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.thoughtworks.asyncapi.serde.SerializerFactory;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;

@SuppressWarnings("SameParameterValue")
public class BaseMqttApi {
  private static final String CONSUMER_CANNOT_BE_NULL = "Consumer cannot be null";
  private static final String TOPIC_CANNOT_BE_NULL = "Topic cannot be null";
  private static final String CLASS_CANNOT_BE_NULL = "Class cannot be null";
  private static final Logger LOGGER = Logger.getLogger(BaseMqttApi.class.getName());
  private static final String I_MQTT_CLIENT_CANNOT_BE_NULL = "IMqttClient cannot be null";
  private static final String MQTT_BINDINGS_CANNOT_BE_NULL = "MqttBindings cannot be null";
  private final Multimap<String, WeakReference<Consumer<?>>> weakSubscriptions =
      MultimapBuilder.hashKeys()
          .arrayListValues().build();
  private final Multimap<String, Consumer<?>> subscriptions =
      MultimapBuilder.hashKeys()
          .arrayListValues().build();
  private final IMqttClient client;
  private final SerializerFactory serializerFactory;
  private Consumer<SubscriptionException> errorHandler;
  private final String defaultMimeType;

  public BaseMqttApi(@Nonnull IMqttClient client, @Nonnull SerializerFactory serializerFactory, String defaultMimeType) {
    this.defaultMimeType = defaultMimeType;
    checkNotNull(client, I_MQTT_CLIENT_CANNOT_BE_NULL);
    checkNotNull(serializerFactory, "SerializerFactory cannot be null");
    this.client = client;
    this.serializerFactory = serializerFactory;
  }

  public void setErrorHandler(Consumer<SubscriptionException> errorHandler) {
    this.errorHandler = errorHandler;
  }

  protected <T> void publish(@Nonnull String topic, @Nonnull MqttBindings bindings, @Nonnull Class<T> clazz, String contentType, @Nullable T payload) {
    checkNotNull(topic, TOPIC_CANNOT_BE_NULL);
    checkNotNull(bindings, MQTT_BINDINGS_CANNOT_BE_NULL);
    checkNotNull(clazz, CLASS_CANNOT_BE_NULL);
    try {
      MqttMessage mqttMessage = new MqttMessage(serializerFactory.forMimeType(
          ofNullable(contentType).orElse(defaultMimeType)
      ).serializer(clazz).apply(payload));
      bindings.apply(mqttMessage);
      client.publish(topic, mqttMessage);
    } catch (Exception e) {
      throw new PublishingException("Failed to publish message", e, clazz, payload);
    }
  }

  protected <T> void subscribeWeakly(@Nonnull String topic, @Nonnull Class<T> clazz, String contentType, @Nonnull Consumer<T> consumer) {
    checkNotNull(topic, TOPIC_CANNOT_BE_NULL);
    checkNotNull(clazz, CLASS_CANNOT_BE_NULL);
    checkNotNull(consumer, CONSUMER_CANNOT_BE_NULL);
    var notPreviouslySubscribed = !weakSubscriptions.containsKey(topic) && !subscriptions.containsKey(topic);
    weakSubscriptions.put(topic, new WeakReference<>(consumer));
    if (notPreviouslySubscribed) {
      checkSubscribe(topic, serializerFactory.forMimeType(
          ofNullable(contentType).orElse(defaultMimeType)
      ).deserializer(clazz));
    }
  }


  private <T> Consumer<WeakReference<Consumer<?>>> notifyWeakConsumer(String t, T payload) {
    return ref -> {
      @SuppressWarnings("unchecked") Consumer<T> c = (Consumer<T>) ref.get();
      if (c != null) {
        c.accept(payload);
      } else {
        LOGGER.fine("Consumer for " + t + " is garbage collected, removing subscription");
        weakSubscriptions.remove(t, ref);
        checkUnsubscribe(t);
      }
    };
  }

  protected void unsubscribe(@Nonnull String topic, @Nonnull Consumer<?> consumer) {
    checkNotNull(topic, TOPIC_CANNOT_BE_NULL);
    checkNotNull(consumer, CONSUMER_CANNOT_BE_NULL);
    subscriptions.remove(topic, consumer);
    checkUnsubscribe(topic);
  }

  private void checkUnsubscribe(String t) {
    if (weakSubscriptions.get(t).isEmpty() && subscriptions.get(t).isEmpty()) {
      LOGGER.fine("No more subscriptions on topic " + t);
      try {
        client.unsubscribe(t);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE, "Failed to unsubscribe from topic " + t, e); //NOSONAR
      }
    }
  }

  protected <T> void subscribe(@Nonnull String topic, @Nonnull Class<T> clazz, String contentType, @Nonnull Consumer<T> consumer) {
    checkNotNull(topic, TOPIC_CANNOT_BE_NULL);
    checkNotNull(clazz, CLASS_CANNOT_BE_NULL);
    checkNotNull(consumer, CONSUMER_CANNOT_BE_NULL);
    var notPreviouslySubscribed = !weakSubscriptions.containsKey(topic) && !subscriptions.containsKey(topic);
    this.subscriptions.put(topic, consumer);
    if (notPreviouslySubscribed) {
      checkSubscribe(topic, serializerFactory.forMimeType(
          ofNullable(contentType).orElse(defaultMimeType)
      ).deserializer(clazz));
    }
  }

  private <T> void checkSubscribe(String topic, Function<byte[], T> deserializer) {
    try {
      client.subscribe(topic, (t, message) -> {
        try {
          T payload = deserializer.apply(message.getPayload());
          new ArrayList<>(weakSubscriptions.get(t)).forEach(notifyWeakConsumer(t, payload));
          //noinspection unchecked
          new ArrayList<>(subscriptions.get(t)).forEach(c -> ((Consumer<T>) c).accept(payload));
        } catch (Exception e) {
          var se = e instanceof SubscriptionException ? (SubscriptionException) e : //NOSONAR
              new SubscriptionException("Failed to process message", e, topic);
          if (errorHandler != null) {
            errorHandler.accept(se);
          } else {
            LOGGER.log(Level.WARNING, "Failed to process message on topic " + topic, se); //NOSONAR
          }
        }
      });

    } catch (Exception e) {
      throw new SubscriptionException("Failed to subscribe to topic", e, topic);
    }
  }

  public static class SubscriptionException extends RuntimeException {
    private final String topic;

    public SubscriptionException(String message, Throwable cause, String topic) {
      super(message, cause);
      this.topic = topic;
    }

    public String getTopic() {
      return topic;
    }
  }

  public static class PublishingException extends RuntimeException {
    private final Class<?> clazz;
    private final transient Object payload;

    public PublishingException(String message, Throwable cause, Class<?> clazz, Object payload) {
      super(message, cause);
      this.clazz = clazz;
      this.payload = payload;
    }

    public Class<?> getType() {
      return clazz;
    }

    public Object getPayload() {
      return payload;
    }
  }
}
