package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.thoughtworks.asyncapi.spec.v2_6_0.AsyncAPI;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class SchemaObjectExtractorTest {

  @Test
  void testExtractAndRender() throws IOException {
    var output = new File("target/SchemaObjectExtractorTest");
    var yamlMapper  = new ObjectMapper(new YAMLFactory());
    var jsonMapper = new ObjectMapper();
    var schema = yamlMapper.readValue(SchemaObjectExtractorTest.class.getResource("/schema.yaml"), AsyncAPI.class);
    var instance = new SchemaObjectExtractor(jsonMapper, output);
    instance.extract(schema.getComponents().getSchemas().getAdditionalProperties());
    var config = new DefaultGenerationConfig() {
      @Override
      public String getTargetPackage() {
        return "com.tw.test";
      }

      @Override
      public File getTargetDirectory() {
        return output;
      }


    };
    instance.render(config);

  }

}