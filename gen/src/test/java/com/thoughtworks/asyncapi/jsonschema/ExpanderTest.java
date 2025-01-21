package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

class ExpanderTest {
  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void testAllOf() throws IOException {

    Expander instance = new Expander();
    File target = new File("target/expanderAllOf");

    File source = new File("src/test/resources/postProcess/Child.json");
    target.mkdir();
    instance.expandAllOf(source, target);

    var read = mapper.readValue(new File(target, source.getName()), LinkedHashMap.class);
    var props = (Map) read.get("properties");
    assertThat(props).containsKey("fromParent");
    assertThat(props).containsKey("fromGrandparent");
  }

}