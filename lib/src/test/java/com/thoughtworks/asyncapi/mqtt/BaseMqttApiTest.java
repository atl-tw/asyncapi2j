package com.thoughtworks.asyncapi.mqtt;

import com.google.common.util.concurrent.Uninterruptibles;
import com.thoughtworks.asyncapi.serde.SerializerFactory;
import com.thoughtworks.asyncapi.serde.impl.JacksonSerDe;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BaseMqttApiTest {
  public static final String CONTENT_TYPE = "application/json";
  private IMqttClient client;
  private MqttBindings bindings;
  private final JacksonSerDe serDe = new JacksonSerDe();
  private final SerializerFactory serializerFactory = new SerializerFactory(Map.of(CONTENT_TYPE, serDe));
  @BeforeEach
  void setUp() {
    Logger rootLogger = LogManager.getLogManager().getLogger("");
    rootLogger.setLevel(Level.FINER);
    for (Handler h : rootLogger.getHandlers()) {
      h.setLevel(Level.FINER);
    }
    client = mock(IMqttClient.class);
    bindings = new MqttBindings(null, null);

  }

  @Test
  void testSimpleWeak() throws Exception {
    var atomicString = new AtomicReference<String>();

    var instance = new BaseMqttApi(client, serializerFactory, CONTENT_TYPE);
    ArgumentCaptor<IMqttMessageListener> subscriber = ArgumentCaptor.forClass(IMqttMessageListener.class);
    Consumer<String> consumer = atomicString::set;
    instance.subscribeWeakly("topic", String.class, null, consumer);
    verify(client).subscribe(eq("topic"), subscriber.capture());
    subscriber.getValue().messageArrived("topic", new org.eclipse.paho.client.mqttv3.MqttMessage("\"Hello\"".getBytes()));
    assertThat(atomicString.get()).isEqualTo("Hello");

  }

  @Test
  void testSimpleStrong() throws Exception {
    var atomicString = new AtomicReference<String>();

    var instance = new BaseMqttApi(client, serializerFactory, CONTENT_TYPE);
    ArgumentCaptor<IMqttMessageListener> subscriber = ArgumentCaptor.forClass(IMqttMessageListener.class);
    Consumer<String> consumer = atomicString::set;
    instance.subscribe("strong", String.class, null, consumer);
    verify(client).subscribe(eq("strong"), subscriber.capture());
    subscriber.getValue().messageArrived("strong", new org.eclipse.paho.client.mqttv3.MqttMessage("\"Hello\"".getBytes()));
    assertThat(atomicString.get()).isEqualTo("Hello");
    instance.unsubscribe("strong", consumer);
    subscriber.getValue().messageArrived("strong", new org.eclipse.paho.client.mqttv3.MqttMessage("\"World\"".getBytes()));
    assertThat(atomicString.get()).isEqualTo("Hello");

  }

  @Test
  void testDereffed() throws Exception {
    var atomicString = new AtomicReference<String>();

    var instance = new BaseMqttApi(client, serializerFactory, CONTENT_TYPE);
    ArgumentCaptor<IMqttMessageListener> subscriber = ArgumentCaptor.forClass(IMqttMessageListener.class);
    deref(atomicString, instance);

    verify(client).subscribe(eq("deref"), subscriber.capture());
    Uninterruptibles.sleepUninterruptibly(500, TimeUnit.MILLISECONDS);
    System.gc();
    subscriber.getValue().messageArrived("deref", new org.eclipse.paho.client.mqttv3.MqttMessage("\"Hello\"".getBytes()));
    assertThat(atomicString.get()).isNull();
  }

  void deref(AtomicReference<String> atomicString, BaseMqttApi instance){
    Consumer<String> consumer = atomicString::set;
    instance.subscribeWeakly("deref", String.class, null, consumer);
  }

  @Test
  void testErrorWeak() throws Exception {
    var atomicString = new AtomicReference<String>();
    var atomicError = new AtomicReference<BaseMqttApi.SubscriptionException>();
    var instance = new BaseMqttApi(client, serializerFactory, CONTENT_TYPE);
    instance.setErrorHandler(atomicError::set);
    ArgumentCaptor<IMqttMessageListener> subscriber = ArgumentCaptor.forClass(IMqttMessageListener.class);
    Consumer<String> consumer = atomicString::set;
    instance.subscribeWeakly("error", String.class, null, consumer);
    verify(client).subscribe(eq("error"), subscriber.capture());
    subscriber.getValue().messageArrived("error", new org.eclipse.paho.client.mqttv3.MqttMessage("Hello\"".getBytes()));
    assertThat(atomicString.get()).isNull();
    assertThat(atomicError.get().getTopic()).isEqualTo("error");
    instance.setErrorHandler(null);
    subscriber.getValue().messageArrived("error", new org.eclipse.paho.client.mqttv3.MqttMessage("Hello\"".getBytes()));
  }

}