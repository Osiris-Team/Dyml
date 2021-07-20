# Design
Dream-Yaml is designed to allow an easy, but also powerful way of editing a yaml file.

The DreamYaml object, represents a single yaml file. The first thing you should do is to 
`load()` the file and parse its contents into DYModules.
Those DYModules are then inside of the'loadedModules' list.
If you edit/get or add anything, those DYModules get added to the 'inEditModules' list.

A DYModule is nothing more than the in-memory representation of a single yaml section like this:
```yaml
# Key-Comment
key: value # Value-Comment
```
All aspects of the above section can be altered with the methods of a DYModule. Like the 'Key-Comment' or even the 'Value-Comment'. 
The 'value' gets put into a DYValueContainer, and then that gets added to the DYModule.
This is done, so you can retrieve the value as different data-types.
For the example above the value returned by `asString()` would be 'value'.
You could retrieve it `asCharArray()`, or as another data type if you'd like.

To sum it up, a DreamYaml object consists of multiple DYModules and a DYModule can consist of multiple DYValueContainers.

DreamYaml provides methods to edit the order, retrieve or even delete sections/DYModules:
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
