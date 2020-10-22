package pro.akvel.spring.converter.xml;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.ConfigurationData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;

import java.util.stream.Collectors;

/**
 * Convertor from {@link BeanDefinition} to {@link ConfigurationData}
 *
 * @author akintsev
 * @since 22.10.2020
 */
public class ConfigurationDataConverter {
    private final static Logger logger = Logger.getLogger(ConfigurationDataConverter.class);

    public static BeanData getConfigurationData(String name, BeanDefinitionRegistry beanDefinitionRegistry) {
        BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(name);

        logger.info("Convert bean definition " + beanDefinition);

        return BeanData.builder()
                .id(name)
                .clazzName(beanDefinition.getBeanClassName())
                .constructorParams(beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues()
                        .entrySet()
                        .stream().map(
                                arg -> {
                                    logger.info("Convert param " + ReflectionToStringBuilder.toString(arg.getValue()));

                                    if (arg.getValue().getValue() instanceof RuntimeBeanReference) {
                                        RuntimeBeanReference refBeanName = (RuntimeBeanReference) arg.getValue().getValue();
                                        return ConstructorBeanParam.builder()
                                                .ref(refBeanName.getBeanName())
                                                .className(beanDefinitionRegistry.getBeanDefinition(refBeanName.getBeanName()).getBeanClassName())
                                                .index(arg.getKey())
                                                .build();
                                    }

                                    if (arg.getValue().getValue() instanceof TypedStringValue) {
                                        TypedStringValue value = (TypedStringValue) arg.getValue().getValue();

                                        if (value.getValue() == null) {
                                            return ConstructorNullParam.builder().build();
                                        } else {
                                            return ConstructorConstantParam.builder()
                                                    .index(arg.getKey())
                                                    .type(value.getTargetType().getName())
                                                    .value(value.getValue())
                                                    .build();
                                        }
                                    }

                                    throw new IllegalArgumentException("Unknown ValueHolder " + ReflectionToStringBuilder.toString(arg.getValue()));
                                }
                        )
                        .collect(Collectors.toList()))
                .initMethodName(beanDefinition.getInitMethodName())
                .destroyMethodName(beanDefinition.getDestroyMethodName())
                //FIXME
                //beanDefinition.getDependsOn()
                //beanDefinition.getDescription()
                //beanDefinition.getRole()
                //beanDefinition.getScope()
                .build();
    }
}
