package com.thoughtworks.asyncapi.cli;

import com.thoughtworks.asyncapi.jsonschema.JsonGenerationConfig;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

public class Generate implements Callable<Integer> {

  @CommandLine.Parameters(index = "0", description = "The path to the AsyncAPI schema document.")
  private String schemaPath;
  @CommandLine.Parameters(index = "1", description = "The path to the output directory.")
  private File outputDir;
  @CommandLine.Option(names={"-p", "--package"}, description = "The package name for the generated classes.")
  private String packageName;
  private final JsonGenerationConfig jsonConfig;

  public Generate(JsonGenerationConfig jsonConfig) {
    this.jsonConfig = jsonConfig;
  }


  @Override
  public Integer call() throws Exception {
    return 0;
  }
}
