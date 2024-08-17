package com.thoughtworks.asyncapi;

import com.thoughtworks.asyncapi.engine.Engine;
import com.thoughtworks.asyncapi.jsonschema.JsonGenerationConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;


@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class GenerateMojo
    extends AbstractMojo {
  /**
   * Location of the file.
   */

  @Parameter(defaultValue="${project}")
  private MavenProject project;

  @Parameter(property = "source", required = true)
  private File source;
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/asyncapi2j", property = "outputDir", required = true)
  private File outputDirectory;

  @Parameter(property = "packageName", required = true)
  private String packageName;

  @Parameter
  private JsonGenerationConfig json = new JsonGenerationConfig();

  public void execute()
      throws MojoExecutionException {
    project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    Engine engine = new Engine();
    try {
      if(!outputDirectory.exists()){
        outputDirectory.mkdirs();
        outputDirectory.mkdir();
      }
      engine.run(source, outputDirectory, packageName, json);
    } catch (Exception e) {
      throw new MojoExecutionException("Failed to generate code", e);
    }
  }
}
