# Design
Dream-Yaml is designed to allow an easy, but also powerful way of editing a yaml file.

The Dream-Yaml object, represents a single yaml file. It will automatically
`load()` the file and parse its contents into DYModules, at initialisation.

A DYModule is nothing more than the in-memory representation of a single yaml section like this:
```yaml
# Key-Comment
key: value # Value-Comment
```

A Dream-Yaml object has two lists: inEditModules and loadedModules.

  - The inEditModules list, gets filled with new DYModules via `add(), get(), put() and replace()`.
  - The loadedModules list, gets cleared and refilled with DYModules when you call `load()`.

The inEditModules values, as well as its parent and child modules get updated on `load()`.

There are methods for all your editing needs:
 - `get()`:Returns module with matching keys or null.
 - `replace()`: Replaces module with matching keys, with the provided module.
 - `add()`: Adds new module or throws exception if it already exists.
 - `put()`: Returns existing, or adds new module.
 - `remove()`: Removes a module.