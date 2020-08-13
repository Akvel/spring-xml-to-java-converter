# spring-xml-to-java-converter
Tool for convert all Spring xml configurations to Spring java based configurations.


Convertor does not has real bean classes, so it has some limitation:
* Not convert bean configs with named construct-Args
* If constructor-arg does not has type attribute - value will be String type by default. Supported only String, Integer and Long
* Property tag only support: ref and value 
