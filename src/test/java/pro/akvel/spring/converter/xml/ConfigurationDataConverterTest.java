package pro.akvel.spring.converter.xml;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.JavaConfigurationGenerator;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigurationDataConverterTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    private static final String OUTPUT_PATH = "build/tmp/";

    private static final XmlConfigurationReader reader = new XmlConfigurationReader();
    private static final String PACKAGE = "pro.akvel.spring.converter.testbean.";
    private static final String CLASS_BEAN_1 = PACKAGE + "BeanWithIdOnly1";
    private static final String CLASS_BEAN_2 = PACKAGE + "BeanWithIdOnly2";
    public static final String PACKAGE_NAME = "pro.akvel.spring.converter.generator";
    public static final String EXPECTED_CLASS_PATH = "src/test/resources/pro/akvel/spring/converter/xml/expected/";

    private JavaConfigurationGenerator classGenerator = new JavaConfigurationGenerator();

    @BeforeAll
    public static void init() throws FileNotFoundException {
        reader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml"));
    }

    @Test
    public void BeanWithoutId() {
        String beanName = PACKAGE + "BeanWithoutId#0";

        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData(beanName, reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .clazzName(PACKAGE + "BeanWithoutId")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithoutId");
    }


    @Test
    public void BeanWithIdOnly() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithIdOnly", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithIdOnly")
                .clazzName(PACKAGE + "BeanWithIdOnly")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithIdOnly");
    }

    @Test
    public void BeanWithConstructorParams() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorParams", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParams")
                .clazzName(PACKAGE + "BeanWithConstructorParams")
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
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorParamsWithNames", reader.getBeanFactory());

        Assertions.assertEquals(null, actualObject);
    }

    @Test
    public void BeanWithConstructorParamsWithNull() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorParamsWithNull", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParamsWithNull")
                .clazzName(PACKAGE + "BeanWithConstructorParamsWithNull")
                .constructorParams(List.of(
                        ConstructorNullParam.builder().build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorParamsWithNull");
    }

    @Test
    public void BeanWithConstructorParamsWithIndex() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorParamsWithIndex", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorParamsWithIndex")
                .clazzName(PACKAGE + "BeanWithConstructorParamsWithIndex")
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
                                .index(3)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorParamsWithIndex");
    }

    @Test
    public void BeanWithProperty() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithProperty", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithProperty")
                .clazzName(PACKAGE + "BeanWithProperty")
                .propertyParams(List.of(
                        PropertyValueParam.builder()
                                .name("property1")
                                .value("value1")
                                .build(),

                        PropertyBeanParam.builder()
                                .name("property2")
                                .className(CLASS_BEAN_1)
                                .ref("bean1")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithProperty");
    }

    @Test
    public void BeanWithConstructorConstArgs() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorConstArgs", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorConstArgs")
                .clazzName(PACKAGE + "BeanWithConstructorConstArgs")
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .type("java.lang.String")
                                .value("stringValue")
                                .build(),

                        ConstructorNullParam.builder().build(),

                        ConstructorConstantParam.builder()
                                .type("java.lang.String")
                                .value("param3Value")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithConstructorConstArgs");
    }

    @Test
    @Disabled
    public void BeanWithMap() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBean() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorWithCreateSubBean", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBean")
                .clazzName(PACKAGE + "BeanWithConstructorWithCreateSubBean")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder().beanData(
                                BeanData.builder()
                                        .clazzName("pro.akvel.spring.converter.testbean.SubBean")
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
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorWithCreateSubBeanWithSubBean", reader.getBeanFactory());


        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBeanWithSubBean")
                .clazzName(PACKAGE + "BeanWithConstructorWithCreateSubBeanWithSubBean")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .clazzName(PACKAGE + "SubBeanWithSubBean")
                                        .constructorParams(List.of(
                                                        ConstructorSubBeanParam.builder()
                                                                .beanData(BeanData.builder()
                                                                        .clazzName(PACKAGE + "SubBean")
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
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithSubBeanWithProperty", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithSubBeanWithProperty")
                .clazzName(PACKAGE + "BeanWithSubBeanWithProperty")
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .clazzName(PACKAGE + "SubBeanWithSubBeanWithProperty")
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
    @Disabled
    public void BeanWithConstructorWithCreateSubBeanWithSubBeanAndMap() {
        Assertions.fail();
    }

    @Test
    @Disabled
    public void BeanWithConstructorWithCreateSubBeanWithFactory() {
        Assertions.fail();
    }

    @Test
    @Disabled
    public void BeanWithConstructorWithCreateSubBeanWithFactoryAndType() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithConstArg() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorWithCreateSubBeanWithConstArg", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstructorWithCreateSubBeanWithConstArg")
                .clazzName(PACKAGE + "BeanWithConstructorWithCreateSubBeanWithConstArg")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build(),
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .clazzName(PACKAGE + "BeanWithConstParam")
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
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithInitDestroyMethod", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithInitDestroyMethod")
                .clazzName(PACKAGE + "BeanWithInitMethod")
                .initMethodName("initMethod")
                .destroyMethodName("destroyMethod")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithInitDestroyMethod");
    }

    @Test
    public void BeanWithDependsOn() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithDependsOn", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithDependsOn")
                .clazzName(PACKAGE + "BeanWithDependsOn")
                .dependsOn(new String[]{"bean1"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDependsOn");
    }

    @Test
    public void BeanWithDependsOnMulti() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithDependsOnMulti", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithDependsOnMulti")
                .clazzName(PACKAGE + "BeanWithDependsOn")
                .dependsOn(new String[]{"bean1", "bean2"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDependsOnMulti");
    }

    @Test
    @Disabled
    public void BeanWithFactoryBean() {
        Assertions.fail();
    }

    @Test
    @Disabled
    public void BeanWithConstructorListArg() {
        Assertions.fail();
    }

    @Test
    public void BeanWithScope() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithScope", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithScope")
                .clazzName(PACKAGE + "BeanWithScope")
                .scope("singleton")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithScope");
    }

    @Test
    public void BeanWithPrimary() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithPrimary", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithPrimary")
                .clazzName(PACKAGE + "BeanWithPrimary")
                .primary(true)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithPrimary");
    }

    @Test
    public void defaultInitDestroyMethods() throws FileNotFoundException {
        XmlConfigurationReader testReader = new XmlConfigurationReader();
        testReader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-defaultInitDestroyMethods.xml"));


        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("testDefaultInitDestroyBean", testReader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("testDefaultInitDestroyBean")
                .clazzName(PACKAGE + "BeanWithoutId")
                .initMethodName("initDef")
                .destroyMethodName("shutdownDef")
                .scope("")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "defaultInitDestroyMethods");
    }

    @Test
    public void BeanWithDescription() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithDescription", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithDescription")
                .clazzName(PACKAGE + "BeanWithDescription")
                .description("Bean with description")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, "BeanWithDescription");
    }

    @SneakyThrows
    private static List<String> getLines(Path expectedFile) {
        return Files.readAllLines(expectedFile, StandardCharsets.UTF_8)
                .stream()
                .map(it -> it.replaceAll("[^;*@.()\\[\\]a-zA-Z0-9]", ""))
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

    @Test
    public void subBeanWithNoSupportedType(){
        //Factory
        //Merged
        //named params
        Assertions.fail();
    }

    @Test
    public void beanWithNoSupportedType(){
        //Factory
        //Merged
        //named params
        Assertions.fail();
    }
}