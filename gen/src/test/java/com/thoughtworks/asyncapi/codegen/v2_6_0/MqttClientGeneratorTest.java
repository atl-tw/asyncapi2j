package com.thoughtworks.asyncapi.codegen.v2_6_0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.asyncapi.spec.v2_6_0.AsyncAPI;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

class MqttClientGeneratorTest {

  @Test
  void testGenerate() throws IOException, URISyntaxException {
    var output = new File("target/SchemaObjectExtractorTest");
    var yamlMapper  = new ObjectMapper(new YAMLFactory());
    var jsonMapper = new ObjectMapper();
    var schema = yamlMapper.readValue(MqttClientGeneratorTest.class.getResource("/schema.yaml"), AsyncAPI.class);
    var instance = new MqttClientGenerator(Objects.requireNonNull(MqttClientGeneratorTest.class.getResource("/schema.yaml")).toURI(), schema, "com.tw.test", output);
    instance.generate();
  }

  @Test
  void testGeneratePayments() throws IOException, URISyntaxException {
    var output = new File("target/payment");
    var yamlMapper  = new ObjectMapper(new YAMLFactory());
    var jsonMapper = new ObjectMapper();
    var schema = yamlMapper.readValue(MqttClientGeneratorTest.class.getResource("/payment.yaml"), AsyncAPI.class);
    var instance = new MqttClientGenerator(Objects.requireNonNull(MqttClientGeneratorTest.class.getResource("/payment.yaml")).toURI(), schema, "com.tw.test", output);
    instance.generate();
  }

}