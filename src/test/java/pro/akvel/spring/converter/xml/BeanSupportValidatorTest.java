package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import pro.akvel.spring.converter.generator.BeanData;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class BeanSupportValidatorTest {
    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    private static BeanSupportValidator validator = new BeanSupportValidator();

    private static final XmlConfigurationReader reader = new XmlConfigurationReader(
            root
                    + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml"
    );

    @Test
    public void validBean(){
        String beanName = "BeanWithConstructorParams";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertTrue(validator.isBeanSupport(definition, "test"));
    }


    @Test
    public void beanWithFactory(){
        String beanName = "BeanWithFactoryBean";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithParamFactory(){
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactoryAndType";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }


    @Test
    public void beanWithMap(){
        String beanName = "BeanWithMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithList(){
        String beanName = "BeanWithConstructorListArg";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithNamedConstructorParams() {
        String beanName = "BeanWithConstructorParamsWithNames";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithExpressionConstructorParams() {
        String beanName = "BeanWithConstructorParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithExpressionPropertyParams() {
        String beanName = "BeanWithPropertyParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithMissedParamIndex() {
        String beanName = "BeanWithMissedIndex";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void beanWithMixedIndexAndNonIndexParams() {
        String beanName = "beanWithMixedIndexAndNonIndexParams";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void subBeanWithNoSupportedTypeMap(){
        String beanName = "BeanWithConstructorWithCreateSubBeanWithSubBeanAndMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }

    @Test
    public void subBeanWithNoSupportedTypeFactory(){
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactory";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test"));
    }
}