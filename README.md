AsyncAPI2J
==========

Purpose
-------

This is a tool (command line or Maven plugin) that will generate Java client code from an 
[AsyncAPI](https://www.asyncapi.com/) specification. This exists because we found
the Node-based client generation tooling wanting. It didn't support JSON Schema very well, and the code it generated
was not particularly flexible. Also, having Java-based tooling is easier to incorporate into Java-based build systems.

Status
------

*WARNING:* This is very much a work in progress. Currently, the only version of AsyncAPI supported is 2.6.0,
the only protocol supported is MQTT, and the only contentType is `application/json`.

Usage
-----

The best way to get started is with the Maven plugin. First, you need to set up the 
[Maven repository for GitHub](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry),
as this project is not in Maven Central yet. Then you can included the plugin in your project thusly:

```xml
<plugin>
    <groupId>com.thoughtworks.asyncapi</groupId>
    <artifactId>asyncapi2j-maven-plugin</artifactId>
    <version>${asyncapi2j.version}</version>
    <executions>
        <execution>
            <id>generate</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- path to your AsyncAPI descriptor -->
        <source>src/asyncapi/my-api.yaml</source>
        <!-- Output package -->
        <packageName>my.api.package</packageName>
        <!-- this is the default output directory -->
        <outputDirectory>${project.build.directory}/generated-sources/asyncapi2j</outputDirectory>
        <json>
            <generateBuilders>true</generateBuilders>
        </json>
    </configuration>
</plugin>
```

JSON schema generation is done with [jsonschema2pojo](https://github.com/joelittlejohn/jsonschema2pojo) and the 
options available under `<json>` will match the [GenerationConfig](https://joelittlejohn.github.io/jsonschema2pojo/javadocs/1.2.1/)
class from that library.

This will generate the schema objects into `[packageName].model` and a class called `MQTTClient` at the top of the package.

Generated Client
----------------

The generated client will have `public` methods reflecting the AsyncAPI specification. Each `subscribe*` operation will
also have a `subscribeWeakly*` version that will keep a weak reference to the listener. This is useful if you need
transient listeners and want to not have to worry about cleaning them up with `unsubscribe*`.

Also, no matter whether the operation is a subscribe or publish operation, a `protected` method for the inverse operation
will be added to the client. This is useful if you want to write unit/integration tests and wish to dispatch messages
for you application code to consume.

You will also need the `asyncapi2j-lib` dependency in your project. This contains classes needed by the generated code:

```xml
 <dependency>
  <groupId>com.thoughtworks.asyncapi</groupId>
  <artifactId>asyncapi2j-lib</artifactId>
  <version>@project.version@</version>
</dependency>
```


Code
----

The code is structured into three different projects. `lib` contains the client library for consumer projects. `gen` 
contains code to generate the clients, and the CLI implementation. `maven` contains the Maven plugin that delegates
to the `gen` code.
