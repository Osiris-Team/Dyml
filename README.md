# Dyml [![](https://jitpack.io/v/Osiris-Team/Dyml.svg)](https://jitpack.io/#Osiris-Team/Dyml)
The YAML and DYML processor of your dreams, written in pure Java! <br>
- Java 8 or higher required.
[Maven/gradle/sbt/leinigen instructions](https://jitpack.io/#Osiris-Team/Dyml/LATEST).
- Support and chat over at [Discord](https://discord.com/invite/GGNmtCC).
- Fund this project via [PayPal](https://www.paypal.com/donate?hosted_button_id=JNXQCWF2TF9W4).

## Features
- Amazing support for side, multiline, regular comments and line breaks.
- Written from scratch with performance and usability in mind.
- Only key-features of YAML implemented, thus very small, blazing fast and easy to use.
- API design, core ideas: 
  - Rely on default values
  - Either null or nothing (no empty values/strings)
  - Keep it simple but powerful (few objects that can do a lot) 

`example.yml` [code](src/test/java/examples/yaml/FeaturesExample.java)
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
or:even:colons: puh!

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

`example.dyml` [code](src/test/java/examples/dyml/FeaturesExample.java)
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
Read more about the `.dyml` file type [here](DYML-SPEC.md). Note
that the API for `.dyml` files is in beta and not all features of the `.yaml` API 
were ported over yet.

Some extras:
 - **DirWatcher |** Directory watcher with recursive directory watching support.
 - **YamlDatabase |** (BETA) Yaml based, lightning fast database.

## Examples
All examples can be found as tests [here](src/test/java/examples).
<details>
<summary>Show frequent YAML mistakes</summary>

It's fine to have colons in keys, as long as there is no space after it.
Here is a small quiz, determine the key and value for the following yaml section:
```yaml
hello:there: my : friend: !
```
Answer: The key is `hello:there` and the value `my : friend: !`.
</details>

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
