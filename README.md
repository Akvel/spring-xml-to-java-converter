![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/akvel/spring-xml-to-java-converter?include_prereleases)
[![Build](https://github.com/Akvel/spring-xml-to-java-converter/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Akvel/spring-xml-to-java-converter/actions/workflows/build.yml)
[![Build Status](https://travis-ci.com/Akvel/spring-xml-to-java-converter.svg?branch=master)](https://travis-ci.com/Akvel/spring-xml-to-java-converter)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Akvel_spring-xml-to-java-converter&metric=alert_status)](https://sonarcloud.io/dashboard?id=Akvel_spring-xml-to-java-converter)
![GitHub](https://img.shields.io/github/license/akvel/spring-xml-to-java-converter)

# Spring-xml-to-java-converter
The tool for convert Spring XML configurations to Spring Java-Based configurations.

Just set parameters input path (-xp), output path(-op) and package name(-p) and the tool convert all supported types to Java class and
remove they from XML.

# Limitation

Converter does not has access to your classes, so it has some limitation.

* If your XML configuration miss any constructor params - will be generate beans without they (You will see compilations error and must fix it manually)
* Skips bean elements with named construct-Args. Because converter doesn't know real constructor params order
* If constructor-arg does not has type attribute - value will be set as String type. (Supported only String, Integer and Long)
* Property tags only support: ref and value
* Not implemented factories convert
* Not implemented convertation beans that uses factories and Mergeable types (Map, List, Set...)
* New XML files do not include any comments (<!-- -->)

# Usage

You need *java 11* for run this tool.

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
