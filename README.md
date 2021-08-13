[![Build Status](https://travis-ci.com/Akvel/spring-xml-to-java-converter.svg?branch=master)](https://travis-ci.com/Akvel/spring-xml-to-java-converter)

# spring-xml-to-java-converter
Tool for convert all Spring xml configurations to Spring java based configurations.


Convertor does not has real bean classes, so it has some limitation:
* Not convert bean configs with named construct-Args (because converter not used converted project source)
* If constructor-arg does not has type attribute - value will be String type by default. Supported only String, Integer and Long
* Property tag only support: ref and value 
* <context:component-scan /> ignored
* Not implemented factories convert 


FIXME проверить что тесты BeanData имеют все тесты генерации кода - в идеале их склеить
FIXME сделать поддержку генерации с бинами по которые приезжают из Java Conf
FIXME <import r
