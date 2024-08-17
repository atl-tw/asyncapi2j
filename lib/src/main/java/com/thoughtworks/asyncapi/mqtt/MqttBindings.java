package com.thoughtworks.asyncapi.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.util.Optional.ofNullable;

public class MqttBindings {

  private final Boolean retained;
  private final Integer qos;

  public MqttBindings(Boolean retained, Integer qos) {
    this.retained = retained;
    this.qos = qos;
  }

  public void apply(MqttMessage message){
    ofNullable(qos).ifPresent(message::setQos);
    ofNullable(retained).ifPresent(message::setRetained);
  }

}
