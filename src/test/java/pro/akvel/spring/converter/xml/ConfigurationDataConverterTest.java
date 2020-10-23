package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigurationDataConverterTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    private static final XmlConfigurationReader reader = new XmlConfigurationReader();
    private static final String PACKAGE = "pro.akvel.spring.converter.testbean.";
    private static final String CLASS_BEAN_1 = PACKAGE + "BeanWithIdOnly1";
    private static final String CLASS_BEAN_2 = PACKAGE + "BeanWithIdOnly2";

    @BeforeAll
    public static void init() throws FileNotFoundException {
        reader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml"));
    }

    @Test
    public void BeanWithoutId() {
        //FIXME ignore names like this in BeanData
        var beanName = PACKAGE + "BeanWithoutId#0";

        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData(beanName, reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .clazzName(PACKAGE + "BeanWithoutId")
                .constructorParams(List.of())
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
    }

    @Test
    public void BeanWithIdOnly() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithIdOnly", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithIdOnly")
                .clazzName(PACKAGE + "BeanWithIdOnly")
                .constructorParams(List.of())
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
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
    }

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
    }

    @Test
    public void BeanWithProperty() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithProperty", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithProperty")
                .clazzName(PACKAGE + "BeanWithProperty")
                .constructorParams(List.of(
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
    }

    @Test
    public void BeanWithConstructorConstArgs() {
        BeanData actualObject = ConfigurationDataConverter
                .getConfigurationData("BeanWithConstructorConstArgs", reader.getBeanFactory());

        var expectedObject = BeanData.builder()
                .id("BeanWithConstuctorConstArgs")
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
    }

    @Test
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
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .id("bean1")
                                        .clazzName(CLASS_BEAN_1)
                                        .build())
                                .build(),
                        ConstructorSubBeanParam.builder()
                                .beanData(BeanData.builder()
                                        .id("bean2")
                                        .clazzName(CLASS_BEAN_2)
                                        .build())
                                .build()
                ))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
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
                                        .constructorParams(List.of(ConstructorSubBeanParam.builder()
                                                        .beanData(BeanData.builder()
                                                                .id("bean1")
                                                                .clazzName(CLASS_BEAN_1)
                                                                .build())
                                                        .build(),
                                                ConstructorSubBeanParam.builder()
                                                        .beanData(BeanData.builder()
                                                                .id("bean2")
                                                                .clazzName(CLASS_BEAN_2)
                                                                .build())
                                                        .build()))
                                        .build())
                                .build()))
                .build();

        assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
    }

    @Test
    public void BeanWithSubBeanWithProperty() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithSubBeanAndMap() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithFactory() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithFactoryAndType() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorWithCreateSubBeanWithConstArg() {
        Assertions.fail();
    }

    @Test
    public void BeanWithInitDestroyMethod() {
        Assertions.fail();
    }

    @Test
    public void BeanWithDependsOn() {
        Assertions.fail();
    }

    @Test
    public void BeanWithDependsOnMulti() {
        Assertions.fail();
    }

    @Test
    public void BeanWithFactoryBean() {
        Assertions.fail();
    }

    @Test
    public void BeanWithConstructorListArg() {
        Assertions.fail();
    }

    @Test
    public void BeanWithScope() {
        Assertions.fail();
    }

    @Test
    public void BeanWithPrimary() {
        Assertions.fail();
    }

    @Test
    public void defaultInitDestroyMethods() {
        Assertions.fail();
    }

}