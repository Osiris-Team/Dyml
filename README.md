# Dream-Yaml [![](https://jitpack.io/v/Osiris-Team/Dream-Yaml.svg)](https://jitpack.io/#Osiris-Team/Dream-Yaml)
The Java-API for processing YAML files you've always dreamed of.
## Links
 - [API-Design](DESIGN.md)
 - Support and chat over at [Discord](https://discord.com/invite/GGNmtCC)
## Installation
[Click here for maven/gradle/sbt/leinigen instructions.](https://jitpack.io/#Osiris-Team/Dream-Yaml/LATEST) <br>
Java 8+ required. <br>
Make sure to watch this repository to get notified of future updates. <br>
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
or separation by spaces: great! # side-comments supported!
and.dots.like.this: wow!

# Complex hierarchies supported.
p1:
  c1-1:
    c2-1: wow!
    c2-2: <3
  c1-2:
    - v1 # side-comments in lists
    # This is also a side-comment, for the value below
    - v2

not supported:
  - everything else that is not explicitly mentioned in this file
```
Some extras:
 - **DYWatcher |** Yaml files watcher with recursive directory watching support.
## Simple example
```java
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/simple-example.yml");

yaml.put("name")         .setDefValues("John");
yaml.put("last-name")    .setDefValues("Goldman");
yaml.put("age")          .setDefValues("29");
yaml.put("work")         .setDefValues("Reporter");
yaml.put("pending-tasks").setDefValues("do research", "buy food", "start working");

yaml.save();
```
The code above generates the following YAML:
```yaml
name: John
last-name: Goldman
age: 29
work: Reporter
pending-tasks: 
  - do research
  - buy food
  - start working
```
## Advanced example
(demonstrating some core features)

<details>
  <summary>For what does DY stand for?</summary>
<pre class="highlight highlight-source-java position-relative">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/advanced-example.yml");

yaml.put("name")         .setDefValues(new DYValue("John", "Value-Comment")).setDefComments("Key-Comment");
yaml.put("last-name")    .setDefValues("Goldman");
yaml.put("age")          .setDefValues("29");
yaml.put("work")         .setDefValues("Reporter");
yaml.put("pending-tasks").setDefValues("do research", "buy food", "start working");

yaml.saveAndLoad();

DYModule getNameModule = yaml.get("name"); // Method for retrieving modules by their keys
yaml.add("new-module"); // Adds a new module, with null value. Throws exception if the key already exists
yaml.remove("new-module"); // Removes the module. Note that this also will remove it from the file.
yaml.replace(getNameModule, new DYModule("first-name").setDefValues("JOHNY")) // First parameter should be the module to replace. Second the new module.
</pre>
The code above generates the following YAML:
<pre class="highlight highlight-source-java position-relative">
  # Key-Comment
first-name: JOHNY # Value-Comment
last-name: Goldman
age: 29
work: Reporter
pending-tasks: 
  - do research
  - buy food
  - start working
</pre>
</details>

## More examples
These examples build on top of each other, so make sure to follow the order.
* [`SimpleExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/SimpleExample.java)
* [`SavingExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/SavingExample.java)
* [`CommentsExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/CommentsExample.java)
* [`GettingValuesExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/GettingValuesExample.java)
* [`ValueValidationExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/ValueValidationExample.java)
* [`ParentExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/ParentExample.java)
* [`CodingStyleExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/CodingStyleExample.java)
* [`WatcherExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/DYWatcherExample.java)
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
These are named 'loaded modules' by the way.
</details>
<details>
  <summary>Difference between 'loaded' and 'added' modules?</summary>
The only difference, is that loaded modules cannot have default values set.
They are basically the raw output from your yaml file. Added modules get created when you call the add() method. Their initial value is taken from the  
loaded module with the same keys.
</details>
<details>
  <summary>How are null/empty values handled?</summary>
<pre>
parent:
  key1:               # this value is null
  key2: ~             # not null, but a string
  key3: null          # not null, but a string
  key5: "null"        # not null, but a string
  key5: ""            # this value is null (note that if you disable the remove quotes post-processing option, this is a string("") and not empty, otherwise this gets turned into a null value)
</pre>
To sum it up: <b>Empty values do NOT exist. Null values exist. </b>
Note that null values are removed from the modules values list, in the post-processing part while parsing the yaml file.
You can disable it though, if you want.
</details>
<details>
  <summary>Fallback to default values?</summary>
When the 'real value' is null, return the default value.
This feature is enabled by default. You can change it for each individual module.
</details>
</div>

## Tags
<div>
<details>
  <summary>Open/Close tags</summary>
Tags are used to make this repository easier to find for others. <br>
yaml javascript
yaml java library
yaml java map
yaml java map example
yaml java list
yaml java api
yaml javascript example
yaml java code generation
yaml java spring
java yaml array
yaml and java
java yaml arraylist
yaml annotations java
java yaml anchors
java yaml auslesen
yaml string array java
load a yaml file java
read a yaml file java
yaml java boolean
java yaml binding
java yaml build
java yaml bean
yaml java spring boot
best yaml java library
java best yaml parser
yaml bigdecimal java
yaml java class
yaml java configuration
yaml java create
java yaml configuration library
java yaml config example
yaml checker java
java yaml cannot create property
yaml java doc
yaml java dictionary
yaml deserialize java
yaml deserialization java
java yaml dump to file
yaml diff java
java yaml default value
java yaml dump options
yaml java example
yaml java enums
yaml en java
yaml escape java
java yaml einlesen
yaml parser java example
yaml java jackson example
java yaml file reader
yaml for java
yaml files java
yaml for java configuration
java yaml file writer
java yaml framework
yaml flatten java
yaml factory java
yaml java github
yaml java generator
yaml generate java class
java yaml get value
yaml generate java
swagger yaml generate java
openapi yaml generate java
yaml java hashmap
yaml to html java
heroku yml java
yaml java.io.ioexception stream closed
yaml in java
java yaml inheritance
java yaml import
yaml interface java
java yaml implementation
yaml iterator java
read yaml in java
yaml java jackson
yaml java json
yaml parser java jackson
yaml to json java library
java yaml vs json
java yaml to json jackson
yaml file to json java
kubernetes yaml java_opts
java kubernetes yaml
java yaml key value
yaml java lib
yaml java list of objects
java.lang.illegalargumentexception could not resolve placeholder yml
yaml.load java
java yaml list example
java yaml libraries
yaml java maven
yaml java mapping
java yaml map of objects
java yaml merge
java yaml mapper
java yaml maven dependency
yaml java.nio.charset.malformedinputexception input length = 1
java yaml ignore null
java yaml nested objects
yaml nested java
yaml caused by java.nio.charset.malformedinputexception input length = 1
java yaml dot notation
java yaml dump ignore null
yaml java object
yaml java_opts
java yaml output
java yaml override
yaml or java
yaml to java online
yaml parser java online
yaml java parser
yaml java properties
yaml java parser example
yaml java parsing
yaml java pojo
java yaml parser jackson
java yaml properties example
java yaml parser library
yaml query java
yaml java reader
yaml java rest
java yaml read list
java yaml reader library
yaml file reader java
yaml config reader java
java read yaml properties
java read yaml configuration file
yaml java set
yaml java snakeyaml
java yaml schema validator
yaml schema java
java yaml snakeyaml example
yaml java tutorial
java yaml to json
yaml to java
yaml to java object
java yaml to map
yaml to java class
java yaml to properties
yaml class java.util.linkedhashmap cannot be cast to class
java use yaml
yaml java.lang.classcastexception java.util.linkedhashmap cannot be cast to
yaml to java.util.properties
java update yaml file
read yaml using java
generate yaml using java
java update yaml
yaml validator java
java yaml vs properties
java yaml variables
java yaml @value
java yaml writer
yaml with java
java yaml parser with comments
yaml file writer java
read yaml with java
use yaml with java
read yaml in java with jackson
write yaml in java with jackson
yaml to xml java
yaml xpath java
java yaml vs xml
yaml java 11
java yaml 1.2 parser
java yaml 1.2
yaml 2 java
openapi 3 yaml to java
java 8 yaml parser
java 8 yaml
</details>
</div>
