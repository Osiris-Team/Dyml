# Y - The (very lazy) shortcuts - Coming soon (maybe)
If you don't like to write a lot of code and want to make your code hard to read for others,
then Dream-Yaml shortcuts should be perfect for you.

```java
Y y = new Y("my-file.yml");
y.load();

y.k("name").dv("John");
y.k("last-name").dv("Goldman");
y.k("age").dv("29");
y.k("work").dv("Reporter");
y.k("pending-tasks").dv("do research", "buy food", "start working");

y.save();
```

| Shortcut | Equivalent |
| :-----: | :-----: |
|`new Y();`|`new DreamYaml();`|
|`k();`|`put()`|
|`dv();`|`setDefaultValues()`|
|`v();`|`setValues()`|