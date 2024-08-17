package com.thoughtworks.asyncapi.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.asyncapi.codegen.Generator;
import com.thoughtworks.asyncapi.jsonschema.JsonGenerationConfig;
import com.thoughtworks.asyncapi.jsonschema.SchemaObjectExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings({"SwitchStatementWithTooFewBranches", "unchecked"})
public class Engine {

  private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
  private final ObjectMapper jsonMapper = new ObjectMapper();

  public static File createTempDirectory()
      throws IOException {
    final File temp;

    temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

    if (!(temp.delete())) {
      throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
    }

    if (!(temp.mkdir())) {
      throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
    }

    return (temp);
  }

  public void run(File source, File output, String packageName, JsonGenerationConfig jsonGenerationConfig) throws Exception {

    if (jsonGenerationConfig.getTargetPackage() == null || jsonGenerationConfig.getTargetPackage().isEmpty()) {
      jsonGenerationConfig.setTargetPackage(packageName);
    }
    jsonGenerationConfig.setTargetDirectory(output);
    var config = yamlMapper.readValue(source, Map.class);

    var version = (String) config.get("asyncapi");
    var mimeType = (String) config.get("defaultContentType");

    List<Generator> generators = new ArrayList<>();

    var servers = (Map<String, Map<String, ?>>) config.get("servers");
    if (servers.isEmpty()) {
      throw new IllegalArgumentException("Servers must be defined with at least one protocol");
    }
    var protocols = servers.values().stream().map(m -> (Map<String, Object>) m).map(m -> m.get("protocol")).map(Objects::toString).toList();

    switch (version) {
      case "2.6.0" -> {
        var schema = yamlMapper.readValue(source, com.thoughtworks.asyncapi.spec.v2_6_0.AsyncAPI.class);
        protocols.forEach(proto -> {
          switch (proto) {
            case "mqtt" ->
                generators.add(new com.thoughtworks.asyncapi.codegen.v2_6_0.MqttClientGenerator(source.toURI(), schema, packageName, output));
            default -> throw new IllegalArgumentException("Unsupported protocol: " + proto);
          }
        });
        switch (mimeType) {
          case "application/json" -> {
            var extractor = new SchemaObjectExtractor(jsonMapper, createTempDirectory());
            extractor.extract(schema.getComponents().getSchemas().getAdditionalProperties());
            extractor.render(jsonGenerationConfig);
          }
          default -> throw new IllegalArgumentException("Unsupported mime type: " + mimeType);
        }
      }
      default -> throw new IllegalArgumentException("Unsupported AsyncAPI version: " + version);
    }

    for (Generator generator : generators) {
      generator.generate();
    }


  }
}
