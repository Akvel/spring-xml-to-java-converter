[![Build Status](https://travis-ci.com/Akvel/spring-xml-to-java-converter.svg?branch=master)](https://travis-ci.com/Akvel/spring-xml-to-java-converter)

# spring-xml-to-java-converter
Tool for convert all Spring xml configurations to Spring java based configurations.

Currently tested only on *java 11*


Convertor does not has real bean classes, so it has some limitation:
* Not convert bean configs with named construct-Args (because converter not used converted project source)
* If constructor-arg does not has type attribute - value will be String type by default. Supported only String, Integer and Long
* Property tag only support: ref and value 
* <context:component-scan /> ignored
* Not implemented factories convert 

FIXME оформить в виде плагина gradle + maven

TODO генерация XML без сконвертированных бинов
TODO плагин maven
TODO плагин gradle
TODO плагин IDEA
TODO Найти проекты с разными XML и погонять
TODO Sax wrapper not support comments

TODO Поправить структуру пакетов 