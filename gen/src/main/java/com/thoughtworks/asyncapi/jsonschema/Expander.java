package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;

@SuppressWarnings("unchecked")
public class Expander {

  private final ObjectMapper mapper = new ObjectMapper();

  public void expandAllOf(File file, File destinationDirectory) throws IOException {
    var read  = expandAllOf(file);
    mapper.writeValue(new File(destinationDirectory.getAbsolutePath(), file.getName()), read);
  }


  public void expandArrays(File file, File destinationDirectory) throws IOException {
    var contents = mapper.readValue(file, LinkedHashMap.class);
    ofNullable(contents.get("properties"))
        .map(o-> (Map<String, Map<String, Object>>) o)
        .ifPresent( properties->{
          properties.entrySet()
              .stream()
              .filter(property-> "array".equals(property.getValue().get("type")))
              .forEach(
                  property->{
                    if(!(property.getValue().get("items") instanceof Map)){
                      System.err.println("Incorrect items spec "+file.getAbsolutePath());
                      return;
                    }
                    var items = (Map<String, Object>) property.getValue().get("items");
                    if(items != null && items.containsKey("$ref")){
                      File child = new File(file.getParentFile().getAbsolutePath(), items.get("$ref").toString());
                      try {
                        var ref = mapper.readValue(child, LinkedHashMap.class);
                        items.clear();
                        items.putAll(ref);
                      } catch (IOException e) {
                        throw new RuntimeException(e);
                      }
                    }

                  }
              );

        });
    mapper.writeValue(new File(destinationDirectory.getAbsolutePath(), file.getName()), contents);
  }

  public LinkedHashMap<String, Object> expandAllOf(File file) {
    try {
      var contents = mapper.readValue(file, LinkedHashMap.class);
      if (contents.containsKey("allOf")) {
        List<LinkedHashMap<String, Object>> inclusions = (List<LinkedHashMap<String, Object>>) contents.get("allOf");
        inclusions.forEach(inclusion -> {
          String ref = (String) inclusion.get("$ref");
          if (ref != null) {
            File child = new File(file.getParentFile().getAbsolutePath(), ref);
            var read = expandAllOf(child);
           LinkedHashMap properties  = ofNullable(contents.get("properties"))
                .map(o-> (LinkedHashMap) o)
                .orElseGet(LinkedHashMap::new);
            LinkedHashMap readProperties  = ofNullable(read.get("properties"))
                .map(o-> (LinkedHashMap) o)
                .orElseGet(LinkedHashMap::new);
            properties.putAll(readProperties);
            contents.put("properties", properties);
          }
        });
        contents.remove("allOf");
      }

      return contents;
    } catch (IOException ioe){
      throw new RuntimeException(ioe);
    }

  }
}
