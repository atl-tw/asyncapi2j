package com.thoughtworks.asyncapi.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParameterEscaperTest {

  @Test
  void testEscape() {
    assertEquals("Hello, World!", ParameterEscaper.escape("Hello, {name}!", Map.of("name", "World")));
  }

}