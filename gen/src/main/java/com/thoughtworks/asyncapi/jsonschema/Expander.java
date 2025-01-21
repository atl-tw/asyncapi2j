package com.thoughtworks.asyncapi.jsonschema;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.Optional.ofNullable;

@SuppressWarnings("unchecked")
public class Expander {

  private final ObjectMapper mapper = new ObjectMapper();

  public void expandFile(File file, File destinationDirectory) throws IOException {

    var read  = readFile(file);
    mapper.writeValue(new File(destinationDirectory.getAbsolutePath(), file.getName()), read);

  }

  public LinkedHashMap<String, Object> readFile(File file) {
    try {
      var contents = mapper.readValue(file, LinkedHashMap.class);
      if (contents.containsKey("allOf")) {
        List<LinkedHashMap<String, Object>> inclusions = (List<LinkedHashMap<String, Object>>) contents.get("allOf");
        inclusions.forEach(inclusion -> {
          String ref = (String) inclusion.get("$ref");
          if (ref != null) {
            File child = new File(file.getParentFile().getAbsolutePath(), ref);
            var read = readFile(child);
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
