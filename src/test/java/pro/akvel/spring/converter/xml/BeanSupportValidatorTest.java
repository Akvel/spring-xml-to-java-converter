package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import pro.akvel.spring.converter.xml.BeanSupportValidator.NotSupportBeanType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(Optional.empty(),
                validator.isBeanSupport(definition, "test", reader.getBeanFactory().get(), false));
    }


    @Test
    public void beanWithFactory() {
        String beanName = "BeanWithFactoryBean";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithParamFactory() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactoryAndType";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }


    @Test
    public void beanWithMap() {
        String beanName = "BeanWithMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPOTED_MERGEABLE_TYPE),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void BeanWithRefBeanWithFactoryConst() {
        String beanName = "BeanWithRefBeanWithFactoryConst";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY),
                validator.isBeanSupport(definition, "test", reader.getBeanFactory().get(), false));
    }


    @Test
    public void BeanWithRefBeanWithFactoryProp() {
        String beanName = "BeanWithRefBeanWithFactoryProp";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY),
                validator.isBeanSupport(definition, "test", reader.getBeanFactory().get(), false));
    }

    @Test
    public void BeanWithSubBeanWithFactory() {
        String beanName = "BeanWithSubBeanWithFactory";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.empty(),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }


    @Test
    public void beanWithList() {
        String beanName = "BeanWithConstructorListArg";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        Assertions.assertEquals(Optional.empty(), validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithNamedConstructorParams() {
        String beanName = "BeanWithConstructorParamsWithNames";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.CONSTRUCTOR_WITH_NAME),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithExpressionConstructorParams() {
        String beanName = "BeanWithConstructorParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_EL),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithExpressionPropertyParams() {
        String beanName = "BeanWithPropertyParamsFromCode";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_EL),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithExpressionPropertyParams3() {
        String beanName = "beanPropertyWithClassPath";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_EL),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }


    @Test
    public void BeanWithAutowire() {
        String beanName = "BeanWithAutowire";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPORTED_AUTOWIRE_CONSTRUCTOR),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithMissedParamIndex() {
        String beanName = "BeanWithMissedIndex";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_MISSED_CONSTRUCTOR_INDEX),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithMixedIndexAndNonIndexParams() {
        String beanName = "beanWithMixedIndexAndNonIndexParams";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_MIXED_CONSTRUCTO_PARAM),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void subBeanWithNoSupportedTypeMap() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithSubBeanAndMap";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_NOT_SUPPOTED_MERGEABLE_TYPE),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void subBeanWithNoSupportedTypeFactory() {
        String beanName = "BeanWithConstructorWithCreateSubBeanWithFactory";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_FACTORY),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithAbstract() {
        String beanName = "inheritedTestBeanWithoutClass";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.ABSTRACT_BEAN),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithParent() {
        String beanName = "inheritsWithClass";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAN_WITH_PARENT),
                validator.isBeanSupport(definition, "test", mockRegistry, false));
    }

    @Test
    public void beanWithMissedRef() {
        String beanName = "beanWithMissedRef";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_MISSED_REF),
                validator.isBeanSupport(definition, "test", reader.getBeanFactory().get(), false));
    }

    @Test
    public void beanWithMissedRefInConst() {
        String beanName = "beanWithMissedRefConst";
        BeanDefinition definition = reader.getBeanFactory().get().getBeanDefinition(beanName);
        assertEquals(Optional.of(NotSupportBeanType.BEAT_WITH_MISSED_REF),
                validator.isBeanSupport(definition, "test", reader.getBeanFactory().get(), false));
    }


}