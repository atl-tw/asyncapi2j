package com.thoughtworks.asyncapi.cli;

import com.thoughtworks.asyncapi.jsonschema.JsonGenerationConfig;
import picocli.CommandLine;

public class Async2J {
  public static void main(String[] args) {
    JsonGenerationConfig jsonConfig = new JsonGenerationConfig();
    new CommandLine(jsonConfig).parseArgs(args);
    int exitCode = new CommandLine(new Generate(jsonConfig))
        .addMixin("json", jsonConfig)
        .execute(args);
    System.exit(exitCode);
  }
}
