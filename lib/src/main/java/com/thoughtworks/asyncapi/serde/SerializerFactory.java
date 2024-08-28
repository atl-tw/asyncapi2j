package com.thoughtworks.asyncapi.serde;

import java.util.Map;

public class SerializerFactory {

  private final Map<String, SerDe> serializerDeserializers;

  public SerializerFactory(Map<String, SerDe> serializerDeserializers) {
    this.serializerDeserializers = serializerDeserializers;
  }

  public SerDe forMimeType(String mimeType){
    return serializerDeserializers.get(mimeType);
  }

  public static class SerializationException extends RuntimeException {
    public SerializationException(String message, Throwable cause) {
      super(message, cause);
    }
  }

}
