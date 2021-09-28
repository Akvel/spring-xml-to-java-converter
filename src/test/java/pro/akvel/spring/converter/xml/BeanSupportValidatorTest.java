package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class BeanSupportValidatorTest {
    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    private static BeanSupportValidator validator = new BeanSupportValidator();

    private static final BeanDefinitionRegistry mockRegistry = Mockito.mock(BeanDefinitionRegistry.class);

    private static final XmlConfigurationReader reader = new XmlConfigurationReader(
            root
                    + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml"
    );

    static {
        Mockito.when(mockRegistry.containsBeanDefinition(anyString())).thenReturn(true);
    }

    @Test
    public void validBean() {
        String beanName = "BeanWithConstructorParams";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertTrue(validator.isBeanSupport(definition, "test", mockRegistry));
    }


    @Test
    public void beanWithFactory() {
        String beanName = "BeanWithFactoryBean";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithParamFactory() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactoryAndType";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }


    @Test
    public void beanWithMap() {
        String beanName = "BeanWithMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void BeanWithSubBeanWithFactory() {
        String beanName = "BeanWithSubBeanWithFactory";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }



    @Test
    public void beanWithList() {
        String beanName = "BeanWithConstructorListArg";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertTrue(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithNamedConstructorParams() {
        String beanName = "BeanWithConstructorParamsWithNames";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithExpressionConstructorParams() {
        String beanName = "BeanWithConstructorParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithExpressionPropertyParams() {
        String beanName = "BeanWithPropertyParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithExpressionPropertyParams3() {
        String beanName = "beanPropertyWithClassPath";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }


    @Test
    public void BeanWithAutowire() {
        String beanName = "BeanWithAutowire";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithMissedParamIndex() {
        String beanName = "BeanWithMissedIndex";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithMixedIndexAndNonIndexParams() {
        String beanName = "beanWithMixedIndexAndNonIndexParams";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void subBeanWithNoSupportedTypeMap() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithSubBeanAndMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void subBeanWithNoSupportedTypeFactory() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactory";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithAbstract() {
        String beanName = "inheritedTestBeanWithoutClass";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithParent() {
        String beanName = "inheritsWithClass";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }

    @Test
    public void beanWithMissedRef() {
        String beanName = "beanWithMissedRef";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", new DefaultListableBeanFactory()));
    }

    @Test
    public void beanWithMissedRefInConst() {
        String beanName = "beanWithMissedRefConst";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertFalse(validator.isBeanSupport(definition, "test", mockRegistry));
    }


}