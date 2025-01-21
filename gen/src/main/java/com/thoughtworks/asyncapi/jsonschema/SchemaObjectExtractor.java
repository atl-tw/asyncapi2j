package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.codemodel.JCodeModel;
import com.thoughtworks.asyncapi.util.ReferenceResolver;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jackson2Annotator;
import org.jsonschema2pojo.SchemaGenerator;
import org.jsonschema2pojo.SchemaMapper;
import org.jsonschema2pojo.SchemaStore;
import org.jsonschema2pojo.rules.RuleFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.List;

public class SchemaObjectExtractor {

  public static final String ALL_OF = "allOf";
  private final ObjectMapper mapper;
  private final File tmpDir;
  private final boolean expandJsonFiles;
  public SchemaObjectExtractor(ObjectMapper mapper, File tmpDir, boolean expandJsonFiles) {
    this.mapper = mapper;
    this.tmpDir = tmpDir;
    this.expandJsonFiles = expandJsonFiles;
    //noinspection ResultOfMethodCallIgnored
    this.tmpDir.mkdirs();
  }


  @SuppressWarnings("unchecked")
  public Map<String, Object> resolveAllOfInTheBaseTypes(URI baseUri, Object definition, Map<String, Object> target){
    target.values()
        .forEach(e->{
          if(e instanceof Map entryMap){
            if(entryMap.containsKey(ALL_OF)){
              var allOf = (List<?>) entryMap.get(ALL_OF);
              allOf.stream().map(o-> {
                        Map<String, Object> r =  ReferenceResolver.resolveReference(baseUri, definition, (Map<String, Object>) o);
                        return r;
                      }
                  )
                  .forEach(m->{
                    if(entryMap.containsKey("properties")&& m.containsKey("properties")){
                      var properties = (Map<String, Object>) entryMap.get("properties");
                      properties.putAll((Map<? extends String, ?>) m.get("properties"));
                    }
                  });
            }
          }
        });
    return target;
  }

  public void extract(Map<String, Object> parsedSchemaObjects) {
    parsedSchemaObjects.forEach((key, value) -> {
      try {
        File file = new File(tmpDir, key+".json");
        var string = mapper.writeValueAsString(value);
        string = string.replaceAll("\"#/components/schemas/(\\w*)\"", "\"./$1.json\"") ;
        Files.write(file.toPath(), string.getBytes());
      } catch (Exception e) {
        throw new SchemaExtractorException("Failed to write schema object to file", e);
      }
    });
    if(this.expandJsonFiles){
      var expander = new Expander();
      Arrays.asList(Objects.requireNonNull(tmpDir.listFiles()))
          .forEach(file-> {
            try {
              expander.expandFile(file, tmpDir);
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    }
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
              var type = schemaMapper.generate(codeModel, file.getName(), config.getTargetPackage()+".model", file.toURI().toURL());
              codeModel.build(config.getTargetDirectory());
            } catch (Exception e) {
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