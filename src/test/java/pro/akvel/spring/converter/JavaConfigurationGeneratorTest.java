package pro.akvel.spring.converter;

import com.sun.codemodel.JClassAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class JavaConfigurationGeneratorTest {

    private static final String OUTPUT_PATH = "build/tmp";

    @Test
    public void generateBeanWithRefs() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithRefs";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .id("beanId")
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                ConstructorBeanParam.builder()
                                        .className("pro.akvel.spring.converter.generator.TestBean1")
                                        .ref("value1")
                                        .build(),
                                ConstructorBeanParam.builder()
                                        .className("pro.akvel.spring.converter.generator.TestBean2")
                                        .ref("value2")
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithoutId() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithoutId";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of())
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithIndexParams() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithIndexParams";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                ConstructorConstantParam.builder()
                                        .value("param1")
                                        .index(1)
                                        .build(),
                                ConstructorConstantParam.builder()
                                        .value("param0")
                                        .index(0)
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }


    @Test
    public void generateBeanInitDestroyMethod() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanInitDestroyMethod";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of())
                        .id("beanId")
                        .initMethodName("initMethod")
                        .destroyMethodName("destroyMethod")
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithNull() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithNull";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                ConstructorNullParam.builder()
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanProperties() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanProperties";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                PropertyValueParam.builder()
                                        .name("property1")
                                        .value("value1")
                                        .build(),
                                PropertyBeanParam.builder()
                                        .name("property2")
                                        .className("pro.akvel.spring.converter.generator.PropertyBean")
                                        .ref("refToBean")
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateSubBeanProperties() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateSubBeanProperties";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(
                                List.of(
                                        ConstructorSubBeanParam.builder()
                                                .beanData(BeanData.builder()
                                                        .id("subBean")
                                                        .clazzName("pro.akvel.spring.converter.generator.SubBean")
                                                        .constructorParams(
                                                                List.of(
                                                                        PropertyValueParam.builder()
                                                                                .name("property1")
                                                                                .value("value1")
                                                                                .build(),
                                                                        PropertyBeanParam.builder()
                                                                                .name("property2")
                                                                                .className("pro.akvel.spring.converter.generator.PropertyBean")
                                                                                .ref("ref")
                                                                                .build()))
                                                        .build())
                                                .build()))
                        .build())
                , OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithConstants() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithConstants";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                ConstructorConstantParam.builder()
                                        .type("java.lang.Integer")
                                        .value("1")
                                        .build(),
                                ConstructorConstantParam.builder()
                                        .type("java.lang.Long")
                                        .value("2")
                                        .build(),
                                ConstructorConstantParam.builder()
                                        .type("java.lang.String")
                                        .value("3")
                                        .build(),
                                ConstructorConstantParam.builder()
                                        .type("java.lang.String")
                                        .value("4")
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithSubBeanWithSubBean() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithSubBeanWithSubBean";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of(
                                ConstructorSubBeanParam.builder()
                                        .beanData(BeanData.builder()
                                                .clazzName("pro.akvel.spring.converter.generator.SubBean")
                                                .id("subBean")
                                                .constructorParams(List.of(ConstructorSubBeanParam.builder()
                                                        .beanData(BeanData.builder()
                                                                .clazzName("pro.akvel.spring.converter.generator.SubSubBean")
                                                                .id("subSubBean")
                                                                .constructorParams(List.of(
                                                                        ConstructorNullParam.builder()
                                                                                .build(),
                                                                        ConstructorBeanParam.builder()
                                                                                .className("pro.akvel.spring.converter.generator.TestBean1")
                                                                                .ref("value1")
                                                                                .build()))
                                                                .build())
                                                        .build()))
                                                .build())
                                        .build()
                        ))
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void generateBeanWithoutParams() throws IOException, JClassAlreadyExistsException {
        JavaConfigurationGenerator config = new JavaConfigurationGenerator();

        String fileName = "GenerateBeanWithoutParams";
        config.generateClass("pro.akvel.spring.converter.test", fileName,
                Collections.singletonList(BeanData.builder()
                        .id("beanId")
                        .clazzName("pro.akvel.spring.converter.generator.TestBean")
                        .constructorParams(List.of())
                        .build()
                ),
                OUTPUT_PATH);


        Path generatedFile = Paths.get(OUTPUT_PATH + "/pro/akvel/spring/converter/test/" + fileName + ".java");
        Path expectedFile = Paths.get("src/test/resources/pro/akvel/spring/converter/generated/" + fileName + ".java");


        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    private List<String> getLines(Path expectedFile) throws IOException {
        return Files.readAllLines(expectedFile, StandardCharsets.UTF_8)
                .stream()
                .map(it -> it.replaceAll("[^;*@.()\\[\\]a-zA-Z0-9]", ""))
                .collect(Collectors.toList());
    }
}