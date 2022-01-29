# Dyml [![](https://jitpack.io/v/Osiris-Team/Dream-Yaml.svg)](https://jitpack.io/#Osiris-Team/Dyml)
The YAML and DYML processor of your dreams, written in pure Java! <br>
- Java 8 or higher required.
[Maven/gradle/sbt/leinigen instructions](https://jitpack.io/#Osiris-Team/Dream-Yaml/LATEST).
- Support and chat over at [Discord](https://discord.com/invite/GGNmtCC).
- Fund this project via [PayPal](https://www.paypal.com/donate?hosted_button_id=JNXQCWF2TF9W4).

## Features
- Amazing support for side, multiline and regular comments.
- Written from scratch with performance, comments and usability in mind.
- Very small, blazing fast and easy to use.
```YAML
important: Everything else that is not explicitly mentioned in this file is not supported

# Comments and
# multiline comments support.
supports-lists: 
  - Hello World!
  - 2nd value
supports-hyphen-separation: awesome! 
or separation by spaces: great! # side-comments supported!
and.dots.like.this: wow!

# Complex hierarchies supported.
g0:
  g1a:
    g2a: wow!
    g2b: <3
  g1b:
    - v1 # side-comments in lists
    # This is also a side-comment, for the value below
    - v2
```
The file above was created in the [FeaturesExample.class](src/test/java/examples/yaml/FeaturesExample.java).
```DYML
important Everything else that is not explicitly mentioned in this file is not supported
 Comments and
 multiline comments support.
key value
 Complex hierarchies supported.
g0
  g1a
    g2a wow!
    g2b <3
  g1b great!
```
The file above was created in the [FeaturesExample.class](src/test/java/examples/dyml/FeaturesExample.java). Read more about this awesome, new
file type [here](DYML-SPEC.md).

Some extras:
 - **.dyml processor** | Faster than the fastest .json Java lib (gson). Smaller file sizes and faster read/write speeds are no longer a dream.
 - **DirWatcher |** Directory watcher with recursive directory watching support.
 - **YamlDatabase |** (BETA) Yaml based, lightning fast database.

## Examples
All examples can be found as tests [here](src/test/java/examples).

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
  <summary>Difference between 'loaded' and 'added' modules?</summary>
The only difference, is that loaded modules cannot have default values set.
They are basically the raw output from your yaml file. In-Edit modules get created when you call the add() method. Their initial value is taken from the  
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
