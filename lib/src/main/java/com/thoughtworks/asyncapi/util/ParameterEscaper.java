package com.thoughtworks.asyncapi.util;

import java.util.Map;

import static java.util.Optional.ofNullable;

public interface ParameterEscaper {

  static String escape(String template, Map<String, ?> values) {
    if (values == null) {
      return template;
    }
    for (Map.Entry<String, ?> entry : values.entrySet()) {
      template = template.replace("{" + entry.getKey() + "}", ofNullable(entry.getValue()).map(Object::toString).orElse(""));
    }
    return template;
  }
}
