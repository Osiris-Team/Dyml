# Dream-Yaml
The best yaml file reader and writer for java.
## Release
Currently still in early development stages. Watch this repository to get notified when its released.
## Requirements
Java 8+
## Motivation
It started by not beeing able to find a library which fullfilled my needs in simplicity and performance.
So I developed Dream-Yaml with the goal of having a simple, performant and reliable library for processing yaml files.
## Features
```yaml
# Comments and
# multiline comments support.
the-show-off-list:
  - completly written from scratch without any extra dependency
  - fastest yaml reader and writer currently available (see benchmarks below)
  - not a single static method and very memory efficient
supports-hyphen-separation: awesome!
or speration by spaces: great!
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
  - apostrophes ("" and '') encapsultating values wont be removed, just don't use them
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

yaml.save(); // Saves the default values to the file. Already existing modules won't be overwritten. Missing modules will be created.

/*
 name: John
 last name: Goldman
 age: 29
 work: Reporter
 */
```
## Examples
The examples build on top of each other, so make sure to follow the order.
* [`SimpleExample`](https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/SimpleExample.java)
* https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples/CommentsExample.java

