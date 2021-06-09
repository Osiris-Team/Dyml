# Design
Dream-Yaml is designed to allow an easy, but also powerful way of editing a yaml file.

The DreamYaml object, represents a single yaml file. It will automatically
`load()` the file (can be disabled) and parse its contents into DYModules, at initialisation .
Those modules are then inside of the DreamYamls 'loadedModules' list.

A DYModule is nothing more than the in-memory representation of a single yaml section like this:
```yaml
# Key-Comment
key: value # Value-Comment
```
It has methods for retrieving and altering all the stuff shown above.

A DYValue is, like the name almost says, the value of a DYModule.
A DYModule can contain multiple values.
For the example above the value returned by `asString()` would be 'value'.
You could retrieve it `asCharArray()`, or as another data type if you'd like.

To sum it up, a DreamYaml object consists of multiple DYModules and a DYModule consists of multiple DYValues.
You can edit DYModules through various methods like:
 - `get()`: Returns module with matching keys or null.
 - `replace()`: Replaces module with matching keys, with the provided module.
 - `add()`: Adds new module or throws exception if it already exists.
 - `put()`: Returns existing, or adds new module.
 - `remove()`: Removes a module.

Note that when you use `get(), add() or put()` the module gets added to a special 'inEditModules' list.
This has multiple advantages (mainly performance).
You can for example keep editing the same DYModule, no matter how often you call `load()`,
since the DYModules objects in the 'inEditModules' list stay the same, and only their values (and parent-/child-modules) get updated on `load()`.

The idea of 'defaults' is strongly represented in this project. 
Defaults are used, when the yaml-file does not contain the default.
Example:
```yaml
key: 
```
The module above has no values and no comments. If you want to have defaults though you can do something like this:
```java
yaml.get("key")
        .setDefValues(new DYValue("value", "value-comment"))
        .setDefComments("key-comment");
yaml.save();
```
And you would get something like this:
```yaml
# key-comment
key: value # value-comment
```
If the value exists however, the default is not used.
