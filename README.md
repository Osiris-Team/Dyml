# Dream-Yaml [![](https://jitpack.io/v/Osiris-Team/Dream-Yaml.svg)](https://jitpack.io/#Osiris-Team/Dream-Yaml)
The Java-API for processing YAML you've always dreamed of.
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
## More examples
These examples build on top of each other, so make sure to follow the order.
<details>
  <summary>Open/Close core features example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/advanced-example.yml");
// yaml.load() is not needed, because autoLoad is true by default and loads your file automatically in the constructor above

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
<pre lang="yaml">
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

<details>
  <summary>Open/Close saving example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/saving-example.yml");

// SCENARIO 1:
// Lets imagine this file contains tons of information but we only want to modify/update that one section and keep the rest.
// For that we simply add that section into memory and edit it: 
yaml.get("work").setValues("Developer");
// And save the file:
yaml.save(); // Note that stuff that isn't supported by DreamYaml wont be parsed and thus removed from the file after you save it!
// Just as simple as that!

// SCENARIO 2:
// Lets imagine another scenario where this file contains a lot of unnecessary stuff we want to get rid of
// and add other data instead.
// For that we (again) add the modules first:
DYModule firstName = yaml.add("name").setDefValues("John");
DYModule lastName = yaml.add("last-name").setDefValues("Goldman");
DYModule age = yaml.add("age").setDefValues("29");
// Then save it with 'overwrite' true:
yaml.save(true);
// That's it!
</pre>
</details>

<details>
  <summary>Open/Close comments example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/comments-example.yml");

DYModule firstName = yaml.put("name").setDefValues("John").setComments("You can insert your", "multiline comments like this.");
DYModule lastName = yaml.put("last-name").setDefValues("Goldman").setComments(
        "This is a multiline comment \n" +
                "separated by javas \n" +
                "next line character!");
DYModule age = yaml.put("age").setDefValues(new DYValue(29).setComment("This is a side-comment/value-comment"))
        .setComments("This is a single line comment.");
DYModule work = yaml.put("work").setDefValues("Reporter");
DYModule parent = yaml.put("p1", "c2", "c3").setComments("Comments in", "a hierarchy.");

yaml.saveAndLoad();

// How to get comments?
firstName.getComments(); // Returns this modules key/top-comments
age.getValue().getComment(); // Returns this modules, values/side-comment
</pre>
<pre lang="yaml">
# You can insert your
# multiline comments like this.
name: John
# This is a multiline comment
# separated by javas
# next line character!
last-name: Goldman
# This is a single line comment.
age: 29 # This is a side-comment/value-comment
work: Reporter
p1:
  c2:
      # Comments in
      # a hierarchy.
      c3:
</pre>
</details>

<details>
  <summary>Open/Close getting values example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/getting-values-example.yml");

DYModule firstName = yaml.put("name").setDefValues("John").setComments("Everything about getting values.");
DYModule lastName = yaml.put("last-name").setDefValues("Goldman");
DYModule age = yaml.put("age").setDefValues("29");
DYModule work = yaml.put("work").setDefValues("Reporter");
DYModule pendingTasks = yaml.put("pending-tasks").setDefValues("research", "1234", "start working");

yaml.saveAndLoad(); // Since the file got reset, we need to reload it after saving it

// Getting module details
String key = firstName.getFirstKey(); // name // Returns the first key.
String keyI = firstName.getKeyByIndex(0); // name // Returns the key by given index. More on this in later examples.
Object value = firstName.getValue(); // John // Returns the 'real' value from the yaml file at the time when load() was called.
Object valueI = firstName.getValueByIndex(0); // John // Returns the value by given index.
Object defaultValue = firstName.getDefValue(); // John // Returns the default value
Object defaultValueI = firstName.getDefValueByIndex(0); // John // Returns the default value
String comment = firstName.getComment(); // Everything about... // Returns the first comment.
String commentI = firstName.getCommentByIndex(0); // Everything about... // Returns the comment by given index.

// All the methods below return the 'real' values at the time when load() was called.
DYValue firstNameValue = firstName.getValue(); // This is never null, and acts as a container for the actual string value
String firstNameAsString = firstName.asString(); // Can be null if there is no actual string value
int ageAsInt = age.asInt();
List<DYValue> pendingTasksValues = pendingTasks.getValues();
List<String> pendingTasksStrings = pendingTasks.asStringList();
// You can also get each value from the list as an independent object
String listIndex0 = pendingTasks.asString(0);
int listIndex1 = pendingTasks.asInt(1);
char[] listIndex2 = pendingTasks.asCharArray(2);

// Finding and getting a module by its keys
DYModule firstNameModuleByKeys = yaml.get("name"); // Returns the module from the permanent added modules list
DYModule firstNameLoadedModuleByKeys = yaml.get("name"); // Returns the module from the temporary loaded modules list, at the time load() was called
</pre>
<pre lang="yaml">
# Everything about getting values.
name: John
last-name: Goldman
age: 29
work: Reporter
pending-tasks:
  - research
  - 1234
  - start working
</pre>
</details>

<details>
  <summary>Open/Close value validation example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/value-validation-example.yml");
DYModule module = yaml.put("is-valid").setDefValues("false");
yaml.saveAndLoad(); // It could be that the file is empty and the default value doesn't exist yet.

if (!module.asBoolean())
    System.err.println("Invalid value '" + module.getValue().asBoolean() + "' at " + module.getKeys() + " Corrected to -> '" + module.setValues("true").getValue().asBoolean() + "'");

yaml.save(true); // Remember to save and update the file, after doing the correction.
</pre>
<pre lang="yaml">
# BEFORE CORRECTION:
is-valid: false
# AFTER CORRECTION:
is-valid: true
</pre>
</details>

<details>
  <summary>Open/Close parent- and child-modules example</summary>
<pre lang="java">
DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/parent-example.yml");

DYModule p1 = yaml.put("p1").setComments("Create a more complex yaml file", "with multiple parents and children.");
DYModule p1C1 = yaml.put("p1", "c1").setDefValues("You can arrange your");
DYModule p1C2 = yaml.put("p1", "c2").setDefValues("keys and values");
DYModule p1C3 = yaml.put("p1", "c3").setDefValues("as you like!");
DYModule p2C3 = yaml.put("p2", "c1", "c2", "c3").setDefValues("awesome!"); // Always order objects from parent to child, otherwise you will get errors!
// DYModule notAllowed = yaml.addDef("p2", "c1", "c2"); <- ERROR because you try to access c2 even though c3 already was added
// If you want to access the child's values you need to pass them one after another like this:
DYModule p3C1 = yaml.put("p3", "c1").setDefValues("v1");
DYModule p3C2 = yaml.put("p3", "c1", "c2").setDefValues("v2");
DYModule p3C3 = yaml.put("p3", "c1", "c2", "c3").setDefValues("v3");

yaml.saveAndLoad();
// You can get a parents child modules easily through:
p1.getChildModules();
// Return a childs parent is also easy:
p1C1.getParentModule();
</pre>
<pre lang="yaml">
# Create a more complex yaml file
# with multiple parents and children.
p1:
  c1: You can arrange your
  c2: keys and values
  c3: as you like!
p2:
  c1:
    c2:
      c3: awesome!
p3:
  c1: v1
    c2: v2
      c3: v3
</pre>
</details>
 
 <details>
  <summary>Open/Close coding style recommendation example</summary>
<pre lang="java">
/**
 * Recommendation how to use the DreamYaml api.
 */
public class CodingStyleExample {

    // If you only got a few modules and you want quick access across your code, you can add them as static fields and load your yaml file once at startup
    // If you also want to keep this values up to date you can add a DYWatcher with an DYAction which does that.
    public static DYModule FIRST_NAME;
    public static DYModule LAST_NAME;
    public static DYModule AGE;
    public static DYModule PROFESSION;

    // If you prefer encapsulating the modules you can do so, but remember that you will have to load your yaml file every time you create this class
    // This will ensure you always are using the latest values.
    private DYModule firstName;
    private DYModule lastName;
    private DYModule age;
    private DYModule work;

    public CodingStyleExample() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/coding-style-example.yml");
        //yaml.load(); // We don't need to call this, since autoLoad is enabled by default.
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        FIRST_NAME = yaml.put("name").setDefValues("John");
        LAST_NAME = yaml.put("last-name").setDefValues("Goldman");
        AGE = yaml.put("age").setDefValues("29");
        PROFESSION = yaml.put("work").setDefValues("Reporter");

        firstName = yaml.put("encapsulated", "name").setDefValues("John");
        lastName = yaml.put("encapsulated", "last-name").setDefValues("Goldman");
        age = yaml.put("encapsulated", "age").setDefValues("29");
        work = yaml.put("encapsulated", "work").setDefValues("Reporter");

        yaml.save(true);
    }

    // Getters for encapsulated modules:

    public DYModule getFirstName() {
        return firstName;
    }

    public DYModule getLastName() {
        return lastName;
    }

    public DYModule getAge() {
        return age;
    }

    public DYModule getWork() {
        return work;
    }
}
</pre>
<pre lang="yaml">
name: John
last-name: Goldman
age: 29
work: Reporter
encapsulated:
  name: John
  last-name: Goldman
  age: 29
  work: Reporter
</pre>
</details>

 <details>
  <summary>Open/Close watching yaml files example</summary>
<pre lang="java">
// First we create two yaml files with some data
DreamYaml yaml1 = new DreamYaml(System.getProperty("user.dir") + "/src/test/watcher-1-example.yml");
yaml1.load();
DYModule firstName1 = yaml1.put("name").setDefValues("John");
yaml1.save(true);

DreamYaml yaml2 = new DreamYaml(System.getProperty("user.dir") + "/src/test/watcher-2-example.yml");
yaml2.load();
DYModule firstName2 = yaml2.put("name").setDefValues("John");
yaml2.save(true);


// Create a watcher. Note that by default it will watch the complete, user directory in which this jar is located and its subdirectories.
DYWatcher watcher = new DYWatcher();  // You can specify a custom directory to watch by: DYWatcher watcher = new DYWatcher("C:your/custom/directory/path/here");
watcher.start(); // Starts the watcher in its own new thread.

// Add the files we want to watch
watcher.addYaml(yaml1);
watcher.addYaml(yaml2);

// Create the action we want to perform when these files change
DYAction action1 = new DYAction();
action1.setRunnable(() -> {
// This action will reload every config watched by the watcher when its changed
try {
        action1.getYaml().load();
        System.out.println("The " + action1.getYaml().getFile().getName() + " file was modified! Event kind: " + action1.getEventKind());
    } catch (Exception e) {
        e.printStackTrace();
    }
   });

// Besides, you can create an action for a specific yaml file, by simply adding that file to the actions constructor
DYAction action2 = new DYAction(yaml2);
action2.setRunnable(() -> {
   // Displays a message when the file gets modified. For more events see StandardWatchEventKinds.
    if (action2.getEventKind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
        System.out.println("This is a specific message for the file yaml2(" + action2.getYaml().getFile().getName() + "), that it was modified!");
});

// Add the actions to the watcher
watcher.addAction(action1);
watcher.addAction(action2);

// That's it. Now we run some test to see if it works:
System.out.println("\nUser modifies yaml1:");
firstName1.setValues("Pete"); // Imagine that this change is done by a person
yaml1.save(); // In this moment the file gets modified

System.out.println("\nUser modifies yaml2:");
firstName2.setValues("Pete"); // Imagine that this change is done by a person
yaml2.save(); // In this moment the file gets modified
</pre>
</details>
All the above examples can be found as tests here: https://github.com/Osiris-Team/Dream-Yaml/blob/main/src/test/java/com/osiris/dyml/examples

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
