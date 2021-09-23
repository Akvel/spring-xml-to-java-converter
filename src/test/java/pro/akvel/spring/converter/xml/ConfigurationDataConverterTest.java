package pro.akvel.spring.converter.xml;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorMergeableParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.MergeableParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyMergeableParam;
import pro.akvel.spring.converter.generator.param.PropertySubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;
import pro.akvel.spring.converter.java.JavaConfigurationGenerator;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigurationDataConverterTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    private static final String OUTPUT_PATH = "build/tmp/";

    private static final XmlConfigurationReader reader = new XmlConfigurationReader(
            root
                    + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml"
    );
    private static final String PACKAGE = "pro.akvel.spring.converter.testbean.";
    private static final String CLASS_BEAN_1 = PACKAGE + "BeanWithIdOnly1";
    private static final String CLASS_BEAN_2 = PACKAGE + "BeanWithIdOnly2";
    public static final String PACKAGE_NAME = "pro.akvel.spring.converter.generator";
    public static final String EXPECTED_CLASS_PATH = "src/test/resources/pro/akvel/spring/converter/xml/expected/";

    private JavaConfigurationGenerator classGenerator = new JavaConfigurationGenerator();

    @Test
    public void BeanWithoutId() {
        String beanName = PACKAGE + "BeanWithoutId#0";

        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(beanName, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .className(PACKAGE + "BeanWithoutId")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithoutId");
    }


    @Test
    public void BeanWithIdOnly() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithIdOnly", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithIdOnly")
                .className(PACKAGE + "BeanWithIdOnly")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithIdOnly");
    }

    @Test
    public void BeanWithConstructorParams() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorParams", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParams")
                .className(PACKAGE + "BeanWithConstructorParams")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build(),
                        ConstructorBeanParam.builder()
                                .ref("bean2")
                                .className(CLASS_BEAN_2)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorParams");
    }

    /**
     * This case not supported
     */
    @Test
    public void BeanWithConstructorParamsWithNames() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorParamsWithNames", reader.getBeanFactory().get());

        Assertions.assertEquals(null, actualObject);
    }

    @Test
    public void BeanWithConstructorParamsWithNull() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorParamsWithNull", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParamsWithNull")
                .className(PACKAGE + "BeanWithConstructorParamsWithNull")
                .constructorParams(List.of(
                        ConstructorNullParam.builder().build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorParamsWithNull");
    }

    @Test
    public void BeanWithConstructorParamsWithIndex() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorParamsWithIndex", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParamsWithIndex")
                .className(PACKAGE + "BeanWithConstructorParamsWithIndex")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .index(0)
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build(),
                        ConstructorBeanParam.builder()
                                .index(1)
                                .ref("bean2")
                                .className(CLASS_BEAN_2)
                                .build(),
                        ConstructorNullParam.builder()
                                .index(2)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorParamsWithIndex");
    }

    @Test
    public void BeanWithProperty() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithProperty", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithProperty")
                .className(PACKAGE + "BeanWithProperty")
                .propertyParams(List.of(
                        PropertyValueParam.builder()
                                .name("property1")
                                .value("value1")
                                .build(),

                        PropertyBeanParam.builder()
                                .name("property2")
                                .className(CLASS_BEAN_1)
                                .ref("bean1")
                                .build(),

                        PropertyValueParam.builder()
                                .name("property3")
                                .value("value3")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithProperty");
    }


    @Test
    public void BeanWithPropertyList() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithPropertyList", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithPropertyList")
                .className(PACKAGE + "BeanWithPropertyList")
                .propertyParams(List.of(
                        PropertyMergeableParam.builder()
                                .name("prop1")
                                .type(MergeableParam.Type.LIST)
                                .values(List.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build(),
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean2")
                                                .className(CLASS_BEAN_2)
                                                .build()
                                ))
                                .build(),
                        PropertyMergeableParam.builder()
                                .name("prop2")
                                .type(MergeableParam.Type.LIST)
                                .values(List.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build()
                                ))
                                .build(),
                        PropertyMergeableParam.builder()
                                .name("prop3")
                                .type(MergeableParam.Type.SET)
                                .values(Set.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build()
                                ))
                                .build(),
                        PropertyMergeableParam.builder()
                                .name("prop4")
                                .type(MergeableParam.Type.SET)
                                .values(Set.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithPropertyList");
    }


    @Test
    public void BeanWithConstructorListArg() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorListArg", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorListArg")
                .className(PACKAGE + "BeanWithConstructorListArg")
                .constructorParams(List.of(
                        ConstructorMergeableParam.builder()
                                .type(MergeableParam.Type.LIST)
                                .values(List.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build(),
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean2")
                                                .className(CLASS_BEAN_2)
                                                .build(),
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean2")
                                                .className(CLASS_BEAN_2)
                                                .build()
                                ))
                                .build(),
                        ConstructorMergeableParam.builder()
                                .type(MergeableParam.Type.SET)
                                .values(Set.of(
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean1")
                                                .className(CLASS_BEAN_1)
                                                .build(),
                                        PropertyBeanParam.builder()
                                                .name("add")
                                                .ref("bean2")
                                                .className(CLASS_BEAN_2)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorListArg");
    }


    @Test
    public void BeanWithConstructorAndPropsParams() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorAndPropsParams", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorAndPropsParams")
                .className(PACKAGE + "BeanWithConstructorAndPropsParams")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build()
                ))
                .propertyParams(List.of(
                        PropertyBeanParam.builder()
                                .name("property2")
                                .className(CLASS_BEAN_2)
                                .ref("bean2")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorAndPropsParams");
    }


    @Test
    public void BeanWithConstructorConstArgs() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorConstArgs", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorConstArgs")
                .className(PACKAGE + "BeanWithConstructorConstArgs")
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .type("java.lang.String")
                                .value("stringValue")
                                .build(),

                        ConstructorNullParam.builder().build(),

                        ConstructorConstantParam.builder()
                                .type("java.lang.String")
                                .value("param3Value")
                                .build(),

                        ConstructorConstantParam.builder()
                                .type("java.lang.Integer")
                                .value("0")
                                .build(),

                        ConstructorConstantParam.builder()
                                .type("java.lang.Long")
                                .value("1")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorConstArgs");
    }

    @Test
    public void BeanWithConstructorWithCreateSubBean() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorWithCreateSubBean", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBean")
                .className(PACKAGE + "BeanWithConstructorWithCreateSubBean")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder().beanData(
                                BeanData.builder()
                                        .className("pro.akvel.spring.converter.testbean.SubBean")
                                        .constructorParams(List.of(
                                                        ConstructorBeanParam.builder()
                                                                .ref("bean1")
                                                                .className(CLASS_BEAN_1)
                                                                .build(),
                                                        ConstructorBeanParam.builder()
                                                                .ref("bean2")
                                                                .className(CLASS_BEAN_2)
                                                                .build()
                                                )
                                        ).build()
                        ).build()
                )).build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorWithCreateSubBean");
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithSubBean() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorWithCreateSubBeanWithSubBean", reader.getBeanFactory().get());


        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBeanWithSubBean")
                .className(PACKAGE + "BeanWithConstructorWithCreateSubBeanWithSubBean")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .className(PACKAGE + "SubBeanWithSubBean")
                                        .constructorParams(List.of(
                                                        ConstructorSubBeanParam.builder()
                                                                .beanData(BeanData.builder()
                                                                        .className(PACKAGE + "SubBean")
                                                                        .constructorParams(
                                                                                List.of(ConstructorBeanParam.builder()
                                                                                                .ref("bean1")
                                                                                                .className(CLASS_BEAN_1)
                                                                                                .build(),
                                                                                        ConstructorBeanParam.builder()
                                                                                                .ref("bean2")
                                                                                                .className(CLASS_BEAN_2)
                                                                                                .build()
                                                                                )
                                                                        )
                                                                        .build()
                                                                )
                                                                .build()
                                                )
                                        )
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorWithCreateSubBeanWithSubBean");
    }

    @Test
    public void BeanWithSubBeanWithProperty() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithSubBeanWithProperty", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithSubBeanWithProperty")
                .className(PACKAGE + "BeanWithSubBeanWithProperty")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .className(PACKAGE + "SubBeanWithSubBeanWithProperty")
                                        .propertyParams(List.of(
                                                PropertyValueParam.builder()
                                                        .name("property1")
                                                        .value("value1")
                                                        .build(),
                                                PropertyBeanParam.builder()
                                                        .name("property2")
                                                        .ref("BeanWithIdOnly")
                                                        .className(PACKAGE + "BeanWithIdOnly")
                                                        .build()
                                        ))
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithSubBeanWithProperty");
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithConstArg() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorWithCreateSubBeanWithConstArg", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBeanWithConstArg")
                .className(PACKAGE + "BeanWithConstructorWithCreateSubBeanWithConstArg")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build(),
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .className(PACKAGE + "BeanWithConstParam")
                                        .constructorParams(List.of(
                                                ConstructorConstantParam.builder()
                                                        .type("java.lang.Integer")
                                                        .value("123")
                                                        .build()
                                        ))
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorWithCreateSubBeanWithConstArg");
    }

    @Test
    public void BeanWithInitDestroyMethod() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithInitDestroyMethod", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithInitDestroyMethod")
                .className(PACKAGE + "BeanWithInitMethod")
                .initMethodName("initMethod")
                .destroyMethodName("destroyMethod")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithInitDestroyMethod");
    }

    @Test
    public void BeanWithDependsOn() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithDependsOn", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithDependsOn")
                .className(PACKAGE + "BeanWithDependsOn")
                .dependsOn(new String[]{"bean1"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDependsOn");
    }

    @Test
    public void BeanWithDependsOnMulti() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithDependsOnMulti", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithDependsOnMulti")
                .className(PACKAGE + "BeanWithDependsOn")
                .dependsOn(new String[]{"bean1", "bean2"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDependsOnMulti");
    }

    @Test
    public void BeanWithScope() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithScope", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithScope")
                .className(PACKAGE + "BeanWithScope")
                .scope("singleton")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithScope");
    }

    @Test
    public void BeanWithPrimary() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithPrimary", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithPrimary")
                .className(PACKAGE + "BeanWithPrimary")
                .primary(true)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithPrimary");
    }

    @Test
    public void defaultInitDestroyMethods() throws FileNotFoundException {
        XmlConfigurationReader testReader = new XmlConfigurationReader(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-defaultInitDestroyMethods.xml");

        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("testDefaultInitDestroyBean", testReader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("testDefaultInitDestroyBean")
                .className(PACKAGE + "BeanWithoutId")
                .initMethodName("initDef")
                .destroyMethodName("shutdownDef")
                .scope("")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "defaultInitDestroyMethods");
    }

    @Test
    public void BeanWithDescription() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithDescription", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithDescription")
                .className(PACKAGE + "BeanWithDescription")
                .description("Bean with description")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDescription");
    }

    @Test
    public void BeanWithLazyAnnotation() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithLazyAnnotation", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithLazyAnnotation")
                .className(PACKAGE + "BeanWithLazyAnnotation")
                .lazyInit(true)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithLazyAnnotation");
    }

    @Test
    public void BeanWithQualifier() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithQualifier", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithQualifier")
                .className(PACKAGE + "BeanWithQualifier")
                .qualifierName(Set.of("contentCount"))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithQualifier");
    }

    @Test
    public void BeanWithInt() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithInt", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithInt")
                .className(PACKAGE + "BeanWithInt")
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .type("int")
                                .value("255")
                                .index(0)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithInt");
    }

    @Test
    public void BeanWithSubBeanProperty() {
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithSubBeanProperty", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithSubBeanProperty")
                .className(PACKAGE + "BeanWithSubBeanProperty")
                .propertyParams(List.of(
                        PropertySubBeanParam.builder()
                                .name("param1")
                                .beanData(BeanData.builder()
                                        .className("pro.akvel.spring.converter.SubBean")
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithSubBeanProperty");
    }


    @Test
    public void BeanWithDependsOnJavaConfiguration() {
        XmlConfigurationReader reader = new XmlConfigurationReader(
                root
                        + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-with-java-configuration.xml"
        );

        var beanFromJavaConfig = ConfigurationDataConverter.getInstance()
                .getConfigurationData("testJavaBean", reader.getBeanFactory().get());
        //check that bean loaded fron java config
        Assertions.assertNotNull(reader.getBeanFactory().get().getBeanDefinition("testJavaBean"));
        //check that bean not supported for convertation
        Assertions.assertNull(beanFromJavaConfig);


        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData("BeanWithConstructorJavaBean", reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorJavaBean")
                .className(PACKAGE + "BeanWithConstructorJavaBean")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("testJavaBean")
                                .className("java.lang.String")
                                .build())
                ).build();


        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        assertGeneratedConfigClass(actualObject, "BeanWithConstructorJavaBeanConfiguration");
    }

    @SneakyThrows
    private static List<String> getLines(Path expectedFile) {
        return Files.readAllLines(expectedFile, StandardCharsets.UTF_8)
                .stream()
                .map(it -> it.replaceAll("[^\"';*@.()\\[\\]a-zA-Z0-9]", ""))
                .collect(Collectors.toList());
    }

    private static String getPath(String configClassName) {
        return OUTPUT_PATH +
                PACKAGE_NAME.replaceAll("\\.", "\\/") + "/" + configClassName + ".java";
    }


    private void assertGeneratedConfigClass(BeanData beanData, String configClassName) {
        classGenerator.generateClass(PACKAGE_NAME,
                configClassName,
                beanData,
                OUTPUT_PATH
        );

        Path generatedFile = Paths.get(getPath(configClassName));
        Path expectedFile = Paths.get(EXPECTED_CLASS_PATH + configClassName + ".java");

        Assertions.assertEquals(getLines(expectedFile), getLines(generatedFile));
    }


}