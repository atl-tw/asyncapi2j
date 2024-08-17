package com.thoughtworks.asyncapi.mqtt;

import java.util.function.Function;

public interface SerDe {

  <T> Function<byte[], T> deserializer(Class<T> clazz);
  <T> Function<T, byte[]> serializer(Class<T> clazz);


}
