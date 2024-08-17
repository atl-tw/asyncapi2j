package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.codemodel.JCodeModel;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class SchemaObjectExtractor {

  private final ObjectMapper mapper;
  private final File tmpDir;
  public SchemaObjectExtractor(ObjectMapper mapper, File tmpDir) {
    this.mapper = mapper;
    this.tmpDir = tmpDir;
    //noinspection ResultOfMethodCallIgnored
    this.tmpDir.mkdirs();
  }

  public void extract(Map<String, Object> parsedSchemaObjects) {
    parsedSchemaObjects.forEach((key, value) -> {
      try {
        File file = new File(tmpDir, key);
        var string = mapper.writeValueAsString(value);
        string = string.replace("\"#/components/schemas/", "\"./");
        Files.write(file.toPath(), string.getBytes());
      } catch (Exception e) {
        throw new SchemaExtractorException("Failed to write schema object to file", e);
      }
    });
  }

  public void render(GenerationConfig config) {
    try {
      File[] source = tmpDir.listFiles();
      Arrays.stream(Objects.requireNonNull(source))
          .filter(f-> !f.isDirectory())
          .forEach(file -> {
        JCodeModel codeModel = new JCodeModel();
        SchemaMapper schemaMapper = new SchemaMapper(new RuleFactory(config, new Jackson2Annotator(config), new SchemaStore()), new SchemaGenerator());
        try {
          schemaMapper.generate(codeModel, file.getName(), config.getTargetPackage()+".model", file.toURI().toURL());
          codeModel.build(config.getTargetDirectory());
        } catch (IOException e) {
          throw new SchemaExtractorException("Failed to process file: " + file.getName(), e);
        }
      });

    } catch (Exception e) {
      throw new SchemaExtractorException("Failed to render schema object", e);
    }

  }

  static class SchemaExtractorException extends RuntimeException {
    public SchemaExtractorException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}