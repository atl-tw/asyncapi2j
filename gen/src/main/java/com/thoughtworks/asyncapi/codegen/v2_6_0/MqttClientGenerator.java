package com.thoughtworks.asyncapi.codegen.v2_6_0;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.thoughtworks.asyncapi.codegen.Generator;
import com.thoughtworks.asyncapi.mqtt.BaseMqttApi;
import com.thoughtworks.asyncapi.serde.SerializerFactory;
import com.thoughtworks.asyncapi.spec.v2_6_0.AsyncAPI;
import com.thoughtworks.asyncapi.spec.v2_6_0.Operation;
import com.thoughtworks.asyncapi.spec.v2_6_0.OperationTrait;
import com.thoughtworks.asyncapi.spec.v2_6_0.OperationTraits;
import com.thoughtworks.asyncapi.spec.v2_6_0.Parameters;
import com.thoughtworks.asyncapi.util.ReferenceResolver;
import org.eclipse.paho.client.mqttv3.IMqttClient;

import javax.annotation.processing.Generated;
import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static java.util.Optional.ofNullable;

public class MqttClientGenerator implements Generator {
  public static final String NOT_FOUND = " not found";
  private static final String COMPONENTS_SCHEMAS = "#/components/schemas/";
  private static final Logger LOGGER = Logger.getLogger(MqttClientGenerator.class.getName());
  private static final String FORMAT = "format";
  private static final String TYPE = "type";
  private final AsyncAPI specification;
  private final String packageName;
  private final File outputDirectory;
  private final LinkedHashMap<String, Class<?>> baseTypesByProtocol = new LinkedHashMap<>();
  private final URI baseUri;
  private Map<String, Object> unreferencedTypes = new HashMap<>();

  public MqttClientGenerator(URI baseUri, AsyncAPI specification, String packageName, File outputDirectory) {
    this.baseUri = baseUri;
    this.specification = specification;
    this.outputDirectory = outputDirectory;
    this.packageName = packageName;
    specification.getServers()
        .getAdditionalProperties()
        .values()
        .forEach(
            server -> {
              var serverMap = asMap(server);
              serverMap.ifPresent(
                  map -> {
                    var protocol = ofNullable(map.get("protocol")).map(Object::toString).orElseThrow(() -> new IllegalArgumentException("Protocol not found on server"));
                    var baseType = switch (protocol) {
                      case "mqtt" -> BaseMqttApi.class;
                      default -> throw new IllegalArgumentException("Unsupported protocol " + protocol);
                    };
                    baseTypesByProtocol.put(protocol, baseType);
                  }
              );
            }
        );
  }

  public void generate() throws IOException {

    this.baseTypesByProtocol.forEach((key, value) -> {
      var client = TypeSpec.classBuilder(ClassName.get(packageName, key.toUpperCase(Locale.ENGLISH) + "Client"))
          .superclass(value)
          .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "\"com.thoughtworks.asyncapi\"").build())
          .addModifiers(Modifier.PUBLIC)
          .addMethod(
              MethodSpec.constructorBuilder()
                  .addModifiers(Modifier.PUBLIC)
                  .addParameter(IMqttClient.class, "client")
                  .addParameter(SerializerFactory.class, "serializerFactory")
                  .addStatement("super(client, serializerFactory, $S)", specification.getDefaultContentType())
                  .build()
          );

      specification.getChannels()
          .getAdditionalProperties()
          .forEach((k, v) -> {
            var subscribeModifier = new AtomicReference<>(Modifier.PUBLIC);
            var publishModifier = new AtomicReference<>(Modifier.PUBLIC);
            var subscribeOp = ofNullable(v.getSubscribe()).orElseGet(() -> {
              subscribeModifier.set(Modifier.PROTECTED);
              return v.getPublish();
            });
            var publishOp = ofNullable(v.getPublish()).orElseGet(() -> {
              publishModifier.set(Modifier.PROTECTED);
              return v.getSubscribe();
            });
            List<MethodSpec> subs = this.createOperations(k, subscribeOp,
                ofNullable(v.getParameters()).map(Parameters::getAdditionalProperties).orElse(Collections.emptyMap()),
                subscribeModifier.get(), publishModifier.get());
            subs.forEach(client::addMethod);
          });

      var file = JavaFile.builder(packageName, client.build())
          .build();
      GenerationException.call(() -> {
        file.writeTo(outputDirectory);
        return null;
      });
      LOGGER.info("Wrote " + packageName + ".AsyncApiClient to " + outputDirectory.getAbsolutePath());
    });

  }

  private Optional<Map<String, Object>> asMap(Object o) {
    //noinspection unchecked
    return ofNullable(o)
        .filter(m -> m instanceof Map)
        .map(m -> (Map<String, Object>) m);
  }


  @SuppressWarnings({"unchecked"})
  private List<MethodSpec> createOperations(String key, Operation operation, Map<String, Object> parameters, Modifier subscriberModifier, Modifier publisherModifier) {
    var payloadTypeName = new AtomicReference<String>();
    var payloadArgumentName = new AtomicReference<String>();
    var contentType = new AtomicReference<String>();
    asMap(operation.getMessage())
        .map(messageMap -> {
          var message = asMap(ReferenceResolver.resolveReference(baseUri, specification, messageMap));
          message.ifPresent(m -> contentType.set(ofNullable(m.get("contentType")).map(Objects::toString).orElse(null)));
          return message.orElse(null);
        })
        .map(m -> (Map) m)
        .ifPresent(refMap ->
            asMap(ReferenceResolver.resolveReference(baseUri, specification, (Map<String, ?>) refMap.get("payload")))
                .ifPresent(payloadMap -> {
                  if (payloadMap.containsKey("@asyncapi_name")) {
                    payloadTypeName.set(payloadMap.get("@asyncapi_name").toString());
                  } else {
                    payloadTypeName.set(operation.getOperationId() + "Payload");
                    unreferencedTypes.put(operation.getOperationId() + "Payload", payloadMap);
                  }
                  payloadArgumentName.set(ofNullable(refMap.get("name")).map(Object::toString).orElse("payload"));
                }));
    var subscribe = createSubscribeMethod(key, operation, contentType.get(), parameters, payloadTypeName, false, subscriberModifier);
    var subscribeWeak = createSubscribeMethod(key, operation, contentType.get(), parameters, payloadTypeName, true, subscriberModifier);
    var publish = createPublishMethod(key, operation, contentType.get(), parameters, payloadTypeName, publisherModifier);
    return List.of(subscribe.build(), subscribeWeak.build(), publish.build());
  }

  private MethodSpec.Builder createPublishMethod(String key, Operation operation, String messageContentType, Map<String, Object> parameters, AtomicReference<String> payloadTypeName, Modifier modifier) {
    var method = MethodSpec.methodBuilder("publish" + Character.toUpperCase(operation.getOperationId().charAt(0)) + operation.getOperationId().substring(1))
        .addModifiers(modifier);

    parameters.forEach((k, v) -> GenerationException.call(() -> {
      var type = resolveType(asMap(v).orElseThrow(() -> new IllegalArgumentException("Parameter " + k + NOT_FOUND)));
      method.addParameter(Class.forName(type), k);
      return null;
    }));

    var payloadType = ClassName.get(packageName + ".model", payloadTypeName.get());
    method.addParameter(payloadType, "payload");

    method
        .addStatement("var topic = $S", key);

    var bindings = new HashMap<String, Object>();
    //noinspection unchecked
    ofNullable(operation.getTraits()).orElse(Collections.emptyList())
        .stream()
        .map(trait -> (Map) trait)
        .map(trait -> (OperationTrait) ReferenceResolver.resolveReference(baseUri, specification, trait))
        .map(trait -> (Map) trait.getBindings().getMqtt())
        .forEach(m -> bindings.putAll(asMap(m).orElseThrow(() -> new IllegalArgumentException("Bindings not found"))));

    method.addStatement("var bindings = new com.thoughtworks.asyncapi.mqtt.MqttBindings($L, $L)", bindings.get("retained"), bindings.get("qos"));

    if (!parameters.isEmpty()) {
      var block = CodeBlock.builder()
          .add("var params = java.util.Map.of(");
      var first = new AtomicBoolean(true);
      parameters.forEach((k, v) -> GenerationException.call(() -> {
        if (!first.get())
          block.add(",");
        first.set(false);
        block.add("$S, $L", k, k);
        return null;
      }));
      block.add(")");
      method.addStatement(block.build());
      method
          .addStatement("topic = com.thoughtworks.asyncapi.util.ParameterEscaper.escape(topic, params)");
    }
    method
        .addStatement("this.publish(topic, bindings, $L.class, $S, payload)", payloadType, messageContentType);
    return method;
  }

  private MethodSpec.Builder createSubscribeMethod(String key, Operation operation, String messageContentType, Map<String, Object> parameters, AtomicReference<String> payloadTypeName, boolean weak, Modifier modifier) {
    var method = MethodSpec.methodBuilder("subscribe" + (weak ? "Weakly" : "") + Character.toUpperCase(operation.getOperationId().charAt(0)) + operation.getOperationId().substring(1))
        .addModifiers(modifier);

    parameters.forEach((k, v) -> GenerationException.call(() -> {
      var type = resolveType(asMap(v).orElseThrow(() -> new IllegalArgumentException("Parameter " + k + NOT_FOUND)));
      method.addParameter(Class.forName(type), k);
      return null;
    }));

    var payloadType = ClassName.get(packageName + ".model", payloadTypeName.get());
    method.addParameter(
        ParameterizedTypeName.get(ClassName.get(Consumer.class), payloadType)
        , "consumer");


    method
        .addStatement("var topic = $S", key);

    if (!parameters.isEmpty()) {
      var block = CodeBlock.builder()
          .add("var params = java.util.Map.of(");
      var first = new AtomicBoolean(true);
      parameters.forEach((k, v) -> GenerationException.call(() -> {
        if (!first.get())
          block.add(",");
        first.set(false);
        block.add("$S, $L", k, k);
        return null;
      }));
      block.add(")");
      method.addStatement(block.build());
      method
          .addStatement("topic = com.thoughtworks.asyncapi.util.ParameterEscaper.escape(topic, params)");
    }
    if (weak) {
      method
          .addStatement("this.subscribeWeakly(topic, $L.class, $S, consumer)", payloadType, messageContentType);
    } else {
      method
          .addStatement("this.subscribe(topic, $L.class, $S, consumer)", payloadType, messageContentType);
    }
    return method;
  }


  @SuppressWarnings({"ReassignedVariable", "unchecked"})
  private String resolveType(Map<String, Object> map) {
    if (map.containsKey("schema")) {
      map = (Map<String, Object>) map.get("schema");
    }
    if (map.containsKey("$ref")) {
      var ref = map.get("$ref").toString();
      var finalRef = ref;
      if (ref.startsWith(COMPONENTS_SCHEMAS)) {
        ref = ref.substring(COMPONENTS_SCHEMAS.length());
        return resolveType(asMap(specification.getComponents().getSchemas().getAdditionalProperties().get(ref)).orElseThrow(() -> new IllegalArgumentException("Schema " + finalRef + NOT_FOUND)));
      }
      if (ref.startsWith("#/components/parameters")) {
        ref = ref.substring("#/components/parameters/".length());
        return resolveType(asMap(specification.getComponents().getParameters().getAdditionalProperties().get(ref)).orElseThrow(() -> new IllegalArgumentException("Parameter " + finalRef + NOT_FOUND)));
      }
      throw new IllegalArgumentException("Unsupported ref " + ref);
    }
    if (map.containsKey(TYPE)) {
      var type = map.get(TYPE).toString();
      return switch (type) {
        case "string" -> {
          if (map.containsKey(FORMAT)) {
            var format = map.get(FORMAT).toString();
            yield switch (format) {
              case "date-time" -> "java.time.LocalDateTime";
              case "date" -> "java.time.LocalDate";
              case "time" -> "java.time.LocalTime";
              default -> "java.lang.String";
            };
          }
          yield "java.lang.String";
        }
        case "number" -> {
          if (map.containsKey(FORMAT)) {
            var format = map.get(FORMAT).toString();
            yield switch (format) {
              case "float" -> "java.lang.Float";
              case "double" -> "java.lang.Double";
              default -> "java.lang.BigDecimal";
            };
          }
          yield "java.lang.Double";
        }
        case "integer" -> "java.lang.Integer";
        case "boolean" -> "java.lang.Boolean";
        default -> "java.lang.Object";
      };
    }
    return "java.lang.Object";
  }


  static class GenerationException extends RuntimeException {
    public GenerationException(String message, Throwable cause) {
      super(message, cause);
    }

    static <T> T call(Callable<T> callable) {
      try {
        return callable.call();
      } catch (Exception e) {
        throw new GenerationException("Exception in generation", e);
      }
    }
  }
}




