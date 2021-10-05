![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/akvel/spring-xml-to-java-converter?include_prereleases)
[![Build](https://github.com/Akvel/spring-xml-to-java-converter/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/Akvel/spring-xml-to-java-converter/actions/workflows/build.yml)
[![Build Status](https://travis-ci.com/Akvel/spring-xml-to-java-converter.svg?branch=master)](https://travis-ci.com/Akvel/spring-xml-to-java-converter)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Akvel_spring-xml-to-java-converter&metric=alert_status)](https://sonarcloud.io/dashboard?id=Akvel_spring-xml-to-java-converter)
![GitHub](https://img.shields.io/github/license/akvel/spring-xml-to-java-converter)

# Spring-xml-to-java-converter
The tool for converting Spring XML configuration to Spring Java-Based configuration.

Just set parameters input path (-xp), output path(-op) and package name(-p) and the tool converts all supported types to Java class and
remove them from XML.

# Limitations

Converter does not have access to your classes, so it has some limitation.

* If your XML configuration misses any constructor params - the beans will be generated without them (You will see compilation error and you will need to fix it manually)
* Skips bean elements named construct-Args. Because converter doesn't know real constructor params order
* If constructor-arg does not has type attribute - value will be set to String type. (Supported only String, Integer and Long)
* Property tags only support: ref and value
* No implemented factories convert (XML factory, FactoryBean)
* No implemented convertation beans that use factories and only Mergeable (except types List and Set)
* New XML files do not include any comments (&lt;!-- --&gt;)
* Tag &lt;qualifier/&gt; must have attribute type="org.springframework.beans.factory.annotation.Qualifier"
* Not supported attrs xmlns:c="http://www.springframework.org/schema/c" and xmlns:p="http://www.springframework.org/schema/p"
* Not supported EL expressions #{} and classpath:

# Usage

You need *java 11* to run this tool.

Simple run:
```
java -jar spring-xml-to-java-converter.jar -xp <XMLs base path> -op <Java classes base path> -p <Base java package name>
```

Full params list:
```
java -jar spring-xml-to-java-converter.jar -h
```

# Contributing

If I've made any errors anywhere in the implementation, please do let me know by raising an issue. If there's any cool addition you want to introduce, all PRs are appreciated!

# License

Beer and pizza license
