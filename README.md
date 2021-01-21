# Dream-Yaml [![](https://jitpack.io/v/Osiris-Team/Dream-Yaml.svg)](https://jitpack.io/#Osiris-Team/Dream-Yaml)
The Java-API for processing YAML files you've always dreamed of.
## Installation
[Click here for maven/gradle/sbt/leinigen instructions.](https://jitpack.io/#Osiris-Team/Dream-Yaml/1.9)
Java 8+ required.
## Motivation
It started by not being able to find an API that fulfilled my needs in simplicity and performance.
So I developed Dream-Yaml with the aim of having a simple, performant, and reliable API for processing YAML files.
Currently, only the most essential parts of YAML are implemented.
## Features
```YAML
# Comments and
# multiline comments support.
the-show-off-list:
  - completely written from scratch without any extra dependency
  - fastest YAML reader and writer currently available (see benchmarks below)
  - not a single static method and very memory efficient
supports-hyphen-separation: awesome!
or separation by spaces: great!
and.dots.like.this: wow!

# Complex hierarchies supported.
p1:
  c1-1:
    c2-1: wow!
    c2-2: <3
  c1-2:
    - v1
    - v2

not supported:
  - everything else that is not explicitly mentioned in this file
  - side comments like -> # will not work :( use multiline comments instead
  - apostrophes ("" and '') encapsulating values won’t be removed, just don't use them
```
## Getting started
```java
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/simple-example.yml");
yaml.load();

DYModule firstName = yaml.add("name")         .setDefValue("John");
DYModule lastName  = yaml.add("last-name")    .setDefValue("Goldman");
DYModule age       = yaml.add("age")          .setDefValue("29");
DYModule work      = yaml.add("work")         .setDefValue("Reporter");
DYModule pending   = yaml.add("pending-tasks").setDefValues("do research", "buy food", "start working");

yaml.save();
```
## Examples
These examples build on top of each other, so make sure to follow the order.
* [`SimpleExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/SimpleExample.java)
* [`CommentsExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/CommentsExample.java)
* [`GettingValuesExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/GettingValuesExample.java)
* [`ValuesValidationExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/ValuesValidationExample.java)
* [`ParentExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/ParentExample.java)
* [`CodingStyleExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/CodingStyleExample.java)
## Benchmarks
Dream-Yaml seems to be about 9x faster than [SnakeYAML](https://bitbucket.org/asomov/snakeyaml/src/master/)
, 8x faster than [YamlBeans](https://github.com/EsotericSoftware/yamlbeans)
, 4x faster than [eo-yaml](https://github.com/decorators-squad/eo-yaml)
 and 3x faster than [Simple-Yaml](https://github.com/Carleslc/Simple-YAML).
<div align="center">
  <img src="https://i.imgur.com/rupU0Ea.png">
<details>
  <summary>Open/close details</summary>
<img src="https://i.imgur.com/Dvob5Ly.png">
</details>
</div>

## FAQ
<div>
<details>
  <summary>For what does DY stand for?</summary>
DreamYaml.
</details>
<details>
  <summary>What is a DYModule?</summary>
It is the in-memory representation of a yaml section. For example 'name: John' is one module. It has the key 'name' and the value 'John'.
</details>
<details>
  <summary>What is a DreamYaml object?</summary>
It is the in-memory representation of the full yaml file and contains all of the modules, which can be accessed by their keys.
</details>
<details>
  <summary>What is a DYReader?</summary>
It is responsible for reading the yaml file and parsing its objects into modules, which then get added to the DreamYaml object.
</details>
<details>
  <summary>Difference between 'loaded' and 'added' modules?</summary>
The only difference, is that loaded modules cannot have default values set.
They are basically the raw output from your yaml file. Added modules get created when you call the add() method. Their initial value is taken from the  
loaded module with the same keys.
</details>
<details>
  <summary>What does save() do?</summary>
It overwrites the yaml file with your added modules. This means that anything, that was not added by the add() method won't be saved to the file.
</details>
</div>
