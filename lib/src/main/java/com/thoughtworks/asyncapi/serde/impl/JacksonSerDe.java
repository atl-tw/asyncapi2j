package com.thoughtworks.asyncapi.serde.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.asyncapi.serde.SerDe;
import com.thoughtworks.asyncapi.serde.SerializerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class JacksonSerDe implements SerDe {

  private final ObjectMapper mapper;
  private ConcurrentHashMap<Class<?>, Function<byte[], ?>> serializers = new ConcurrentHashMap<>();
  private ConcurrentHashMap<Class<?>, Function<?, byte[]>> deserializers = new ConcurrentHashMap<>();

  public JacksonSerDe(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  public JacksonSerDe() {
    this(new ObjectMapper());
  }


  @Override
  public <T> Function<byte[], T> deserializer(Class<T> clazz) {
    if(serializers.containsKey(clazz)){
      //noinspection unchecked
      return (Function<byte[], T>) serializers.get(clazz);
    }
    Function<byte[], T> serializer = bytes -> {
      try {
        return mapper.readValue(bytes, clazz);
      } catch (Exception e) {
        throw new SerializerFactory.SerializationException("Failed to deserialize " + clazz.getCanonicalName(), e);
      }
    };
    serializers.put(clazz, serializer);
    return serializer;
  }

  @Override
  public <T> Function<T, byte[]> serializer(Class<T> clazz) {
    if(deserializers.containsKey(clazz)){
      //noinspection unchecked
      return (Function<T, byte[]>) deserializers.get(clazz);
    }
    Function<T, byte[]> deserializer = obj -> {
      try {
        return mapper.writeValueAsBytes(obj);
      } catch (Exception e) {
        throw new SerializerFactory.SerializationException("Failed to serialize " + clazz.getCanonicalName(), e);
      }
    };
    deserializers.put(clazz, deserializer);
    return deserializer;
  }
}
