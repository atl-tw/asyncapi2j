package com.thoughtworks.asyncapi.amqp;

import com.rabbitmq.client.ConnectionFactory;
import com.thoughtworks.asyncapi.mqtt.BaseMqttApi;
import com.thoughtworks.asyncapi.serde.SerializerFactory;

import java.util.function.Consumer;

public class BaseAmqpApi {

  private final ConnectionFactory connectionFactory;
  private final String replyQueueName;
  private final SerializerFactory serializerFactory;
  private Consumer<BaseMqttApi.SubscriptionException> errorHandler;
  private final String defaultMimeType;

  public BaseAmqpApi(ConnectionFactory connectionFactory, String replyQueueName, SerializerFactory serializerFactory, String defaultMimeType) {
    this.connectionFactory = connectionFactory;
    this.replyQueueName = replyQueueName;
    this.serializerFactory = serializerFactory;
    this.defaultMimeType = defaultMimeType;
  }
}
