# .dyml
Smaller file sizes and faster read/write speeds are no longer a dream. See [benchmarks](https://github.com/Osiris-Team/Dyml/issues/17).

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
All sections can have values. Values get defined by adding a space after the key (thus a key cannot contain spaces):
```dyml
parent value
  child1 value
  child2 value
```
Comments must be above a section, and get defined by having one more leading space than the section they belong:
```dyml
 I am a comment of parent
parent
   I am a comment of child
  child
```
Multiple values are generally not supported and implementation specific. 
One idea would be by splitting the value into multiple
values via spaces inbetween each value:
```dyml
parent value
  child value1 value2 value3
```
Empty values do not exist. Only null values exist. Which means that if the value is empty or only contains spaces return null:
```
 Returned value is null
key
 Returned value is "null"
key null
 Returned value is "''"
key ''
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
