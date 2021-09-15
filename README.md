![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/akvel/spring-xml-to-java-converter?include_prereleases)
[![Build Status](https://travis-ci.com/Akvel/spring-xml-to-java-converter.svg?branch=master)](https://travis-ci.com/Akvel/spring-xml-to-java-converter)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Akvel_spring-xml-to-java-converter&metric=alert_status)](https://sonarcloud.io/dashboard?id=Akvel_spring-xml-to-java-converter)
![GitHub](https://img.shields.io/github/license/akvel/spring-xml-to-java-converter)

# spring-xml-to-java-converter
Tool for convert Spring xml configurations to Spring java based configurations.

Currently tested only on *java 11*

Converter does not has real bean classes, so it has some limitation:
* Not convert bean configs with named construct-Args (because converter doesn't know constructor params order)
* If constructor-arg does not has type attribute - value will be String type by default. Supported only String, Integer and Long
* Property tag only support: ref and value 
* <context:component-scan /> ignored
* Not implemented factories convert
* Not implemented convertation beans that uses factories and Mergeable types (Map, List, Set...)
* New XML files do not include any comments (<!-- -->)  

# Usage
Simple run:
```
java -jar spring-xml-to-java-converter.jar -xp <XMLs base path> -op <Java classes base path> -p <Base java package name>
```

Full params list:
```
java -jar spring-xml-to-java-converter.jar -h
```

# Contributing

If I've made any errors anywhere in the implementation, please do let me know by raising an issue. If there's any cool addition you want to introduce, all PRs appreciated!

# License

Beer and pizza license
