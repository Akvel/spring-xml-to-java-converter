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
import pro.akvel.spring.converter.java.JavaGeneratorParams;

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

    private JavaConfigurationGenerator classGenerator = new JavaConfigurationGenerator(
            JavaGeneratorParams.builder()
                    .trueFalseAsBoolean(true)
                    .build()
    );

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
        String bean = "BeanWithIdOnly";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithConstructorParams() {
        String bean = "BeanWithConstructorParams";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
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
        String bean = "BeanWithConstructorParamsWithNull";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(
                        ConstructorNullParam.builder().build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithConstructorParamsWithIndex() {
        String bean = "BeanWithConstructorParamsWithIndex";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithProperty() {
        String bean = "BeanWithProperty";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }


    @Test
    public void BeanWithPropertyList() {
        String bean = "BeanWithPropertyList";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }


    @Test
    public void BeanWithConstructorListArg() {
        String beanWithConstructorListArg = "BeanWithConstructorListArg";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(beanWithConstructorListArg, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(beanWithConstructorListArg)
                .className(PACKAGE + beanWithConstructorListArg)
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

        assertGeneratedConfigClass(actualObject, beanWithConstructorListArg);
    }


    @Test
    public void BeanWithConstructorAndPropsParams() {
        String bean = "BeanWithConstructorAndPropsParams";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }


    @Test
    public void BeanWithConstructorConstArgs() {
        String bean = "BeanWithConstructorConstArgs";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithConstructorWithCreateSubBean() {
        String bean = "BeanWithConstructorWithCreateSubBean";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithSubBean() {
        String bean = "BeanWithConstructorWithCreateSubBeanWithSubBean";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());


        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(ConstructorSubBeanParam.builder()
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
                                                                        ).build()
                                                                ).build()
                                                )
                                        ).build())
                                .build()
                        )
                ).build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithSubBeanWithProperty() {
        String bean = "BeanWithSubBeanWithProperty";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithTrueFalse() {
        String bean = "BeanWithTrueFalse";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .value("true")
                                .type("java.lang.String")
                                .build(),
                        ConstructorConstantParam.builder()
                                .value("false")
                                .type("java.lang.String")
                                .build()
                ))
                .propertyParams(List.of(
                        PropertyValueParam.builder()
                                .name("property1")
                                .value("true")
                                .build(),
                        PropertyValueParam.builder()
                                .name("property2")
                                .value("false")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithPlaceholder() {
        String bean = "BeanWithPlaceholder";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .value("test${pl1}passed")
                                .type("java.lang.String")
                                .build(),
                        ConstructorConstantParam.builder()
                                .value("${pl2}")
                                .type("java.lang.String")
                                .build()
                ))
                .propertyParams(List.of(
                        PropertyValueParam.builder()
                                .name("property1")
                                .value("test${pl1}passed")
                                .build(),
                        PropertyValueParam.builder()
                                .name("property2")
                                .value("${pl2}")
                                .build(),
                        PropertyValueParam.builder()
                                .name("property3")
                                .value("${pl1} and ${pl2}")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }


    @Test
    public void BeanWithConstructorWithCreateSubBeanWithConstArg() {
        String bean = "BeanWithConstructorWithCreateSubBeanWithConstArg";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithInitDestroyMethod() {
        String bean = "BeanWithInitDestroyMethod";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + "BeanWithInitMethod")
                .initMethodName("initMethod")
                .destroyMethodName("destroyMethod")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithDependsOn() {
        String bean = "BeanWithDependsOn";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .dependsOn(new String[]{"bean1"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithDependsOnMulti() {
        String bean = "BeanWithDependsOnMulti";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + "BeanWithDependsOn")
                .dependsOn(new String[]{"bean1", "bean2"})
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithScope() {
        String bean = "BeanWithScope";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .scope("singleton")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithPrimary() {
        String bean = "BeanWithPrimary";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .primary(true)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
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
        String bean = "BeanWithDescription";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .description("Bean with description")
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithLazyAnnotation() {
        String bean = "BeanWithLazyAnnotation";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .lazyInit(true)
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithQualifier() {
        String bean = "BeanWithQualifier";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .qualifierName(Set.of("contentCount"))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithInt() {
        String bean = "BeanWithInt";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .type("int")
                                .value("255")
                                .index(0)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithSubBeanProperty() {
        String bean = "BeanWithSubBeanProperty";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithFactoryMethod() {
        String bean = "BeanWithFactoryMethod";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .factoryMethodName("getBean")
                .constructorParams(List.of(
                        ConstructorConstantParam.builder()
                                .type("java.lang.String")
                                .value("param")
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithFactoryMethodParamRef() {
        String bean = "BeanWithFactoryMethodParamRef";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .factoryMethodName("getBean")
                .constructorParams(List.of(
                        ConstructorBeanParam.builder()
                                .ref("bean1")
                                .className(CLASS_BEAN_1)
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithSubBeanWithFactory() {
        String bean = "BeanWithSubBeanWithFactory";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .constructorParams(List.of(
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .className("org.springframework.beans.ManagementServerNode")
                                        .factoryMethodName("getManagementServerId")
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
    }

    @Test
    public void BeanWithSubBeanWithFactoryProp() {
        String bean = "BeanWithSubBeanWithFactoryProp";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
                .propertyParams(List.of(
                        PropertySubBeanParam.builder()
                                .name("param1")
                                .beanData(BeanData.builder()
                                        .className("org.springframework.beans.ManagementServerNode")
                                        .factoryMethodName("getManagementServerId")
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);

        assertGeneratedConfigClass(actualObject, bean);
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


        String bean = "BeanWithConstructorJavaBean";
        BeanData actualObject = ConfigurationDataConverter.getInstance()
                .getConfigurationData(bean, reader.getBeanFactory().get());

        var expectedObject = BeanData.builder()
                .id(bean)
                .className(PACKAGE + bean)
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