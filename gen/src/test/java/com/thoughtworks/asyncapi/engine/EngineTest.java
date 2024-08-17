package com.thoughtworks.asyncapi.engine;

import com.thoughtworks.asyncapi.jsonschema.JsonGenerationConfig;
import io.moquette.broker.Server;
import io.moquette.broker.config.ClasspathResourceLoader;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.IResourceLoader;
import io.moquette.broker.config.ResourceLoaderConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

class EngineTest {

  static Server mqttServer;

  @BeforeAll
  public static void setup() throws IOException {
    IResourceLoader classpathLoader = new ClasspathResourceLoader();
    final IConfig classPathConfig = new ResourceLoaderConfig(classpathLoader, "moquette.conf");
    mqttServer = new Server();
    mqttServer.startServer(classPathConfig, Collections.emptyList());
  }

  @AfterAll
  public static void teardown() {
    mqttServer.stopServer();
  }

  @Test
  void testGenerateAndRun() throws Exception {
    Engine engine = new Engine();
    var target = new File("target/EngineTest");
    target.mkdir();
    
    engine.run(new File("src/test/resources/schema.yaml"), target, "enginetest", new JsonGenerationConfig());

    Files.copy(Path.of("src/test/resources/Test.java"),Path.of("target/EngineTest/enginetest/Test.java"), REPLACE_EXISTING);
    var sources = Files.walk(Paths.get("target/EngineTest")).filter(Files::isRegularFile)
        .map(Path::toFile)
        .filter(f -> f.getName().endsWith(".java"))
        .toList();

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

    Iterable<? extends JavaFileObject> compilationUnits1 =
        fileManager.getJavaFileObjectsFromFiles(sources);
    compiler.getTask(null, fileManager, null, null, null, compilationUnits1).call();

    fileManager.close();



    var gcl = URLClassLoader.newInstance(new URL[] {target.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
    Class<?> clazz = gcl.loadClass("enginetest.Test");
    Runnable r = (Runnable) clazz.getConstructor().newInstance();
    r.run();

  }
}