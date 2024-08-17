package com.thoughtworks.asyncapi.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.CharStreams;
import org.apache.commons.beanutils.BeanUtilsBean;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ReferenceResolver {
  private static final BeanUtilsBean bub = new BeanUtilsBean();
  private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  private static final ObjectMapper jsonMapper = new ObjectMapper();

  @SuppressWarnings("unchecked")
  public static <T> T resolveReference(URI baseUri, Object definition, Map<String, ?> target) {
    if (target.containsKey("$ref")) {
      String ref = (String) target.get("$ref");

      if (ref.startsWith("#")) {
        var context = new AtomicReference<>(definition);
        Arrays.stream(ref.substring(1).split("/"))
            .forEach(property -> {
              try {
                var description = bub.describe(context.get());
                if (description.containsKey(property)) {
                  var value = bub.getPropertyUtils().getProperty(context.get(), property);
                  if (value instanceof Map map) {
                    map.put("@asyncapi_name", property);
                  }
                  context.set(value);
                } else if (description.containsKey("additionalProperties")) {
                  Map<String, Object> additionalProperties = (Map<String, Object>) bub.getPropertyUtils().getProperty(context.get(), "additionalProperties");
                  var value = additionalProperties.get(property);
                  if (additionalProperties.containsKey(property)) {
                    if(value instanceof Map map) {
                      map.put("@asyncapi_name", property);
                    }
                    context.set(value);
                  } else {
                    throw new ReferenceException("Property not found: " + property + " resolving " + ref);
                  }
                }
              } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new ReferenceException("Unexpected error resolving property: "+property+" in reference "+ref, e);
              }
            });
        return (T) context.get();
      }
      URI refUri = baseUri.resolve(ref);
      try(var stream = refUri.toURL().openStream(); var reader = new InputStreamReader(stream)){
        String content = CharStreams.toString(reader).trim();
        if(content.startsWith("{")){
          return jsonMapper.readValue(content, new TypeReference<>() {
          });
        } else {
          return yamlMapper.readValue(content, new TypeReference<>() {
          });
        }
      } catch (IOException e) {
        throw new ReferenceException("Failed to read reference URI "+ refUri, e);
      }
    }
    return (T) target;
  }

  public static class ReferenceException extends RuntimeException {
    public ReferenceException(String message) {
      super(message);
    }

    public ReferenceException(String message, Throwable cause) {
      super(message, cause);
    }
  }

}
