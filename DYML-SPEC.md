# .dyml
Smaller file sizes and faster read/write speeds are no longer a dream.

[Benchmarks](https://github.com/Osiris-Team/Dyml/issues/17);

# Features

All sections must have a parent section, except the root/first section. A child section gets defined by adding 2 spaces at the beginning:
```dyml
parent
  child
```
Keys must be unique in each section:
```dyml
parent
  child1
  child2
```
All sections can have values. Values get defined by adding a space after the key:
```dyml
parent value
  child1 value
  child2 value
```
Comments must be above a section, and get defined by having one more leading space than the section they belong:
```dyml
 I am a comment
parent
   I am a comment
  child
```
Multiple values can be assigned to one key, by adding spaces between values:
```dyml
parent value
  child value1 value2 value3
```

example.dyml
```dyml
 First comment
key1 value
  child1 value
  child2 value
 Second comment
key2 value
```
