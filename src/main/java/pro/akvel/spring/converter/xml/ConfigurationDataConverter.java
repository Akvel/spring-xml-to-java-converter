package pro.akvel.spring.converter.xml;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.support.ManagedSet;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.ConfigurationData;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Convertor from {@link BeanDefinition} to {@link ConfigurationData}
 *
 * @author akvel
 * @since 22.10.2020
 */
public class ConfigurationDataConverter {
    private final static Logger logger = Logger.getLogger(ConfigurationDataConverter.class);

    @Nullable
    public static BeanData getConfigurationData(@Nonnull String name,
                                                @Nonnull BeanDefinitionRegistry beanDefinitionRegistry) {
        return getConfigurationData(beanDefinitionRegistry.getBeanDefinition(name), beanDefinitionRegistry, name);
    }


    @Nullable
    private static BeanData getConfigurationData(@Nonnull BeanDefinition beanDefinition,
                                                 @Nonnull BeanDefinitionRegistry beanDefinitionRegistry,
                                                 @Nullable String beanName) {
        logger.info("Convert bean definition " + beanDefinition);
        return BeanData.builder()
                .id(beanName)
                .clazzName(beanDefinition.getBeanClassName())
                .constructorParams(
                        Stream.concat(
                                beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues()
                                        .entrySet()
                                        .stream(),
                                beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                                        .map(value -> Pair.of(Integer.MAX_VALUE, value)))
                                .map(
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
                                                    return ConstructorNullParam.builder()
                                                            .index(arg.getKey())
                                                            .build();
                                                } else {
                                                    return ConstructorConstantParam.builder()
                                                            .index(arg.getKey())
                                                            .type(value.getTargetTypeName())
                                                            .value(value.getValue())
                                                            .build();
                                                }
                                            }

                                            if (arg.getValue().getValue() instanceof BeanDefinitionHolder) {
                                                BeanDefinitionHolder value = (BeanDefinitionHolder) arg.getValue().getValue();

                                                if (value.getBeanDefinition().getFactoryBeanName() != null) {
                                                    //FIXME
                                                    return null;
                                                }

                                                var subBean = getConfigurationData(value.getBeanDefinition(), beanDefinitionRegistry, null);

                                                if (subBean == null) {
                                                    logger.info("subBean return null");
                                                    return null;
                                                }

                                                return ConstructorSubBeanParam.builder()
                                                        .beanData(subBean)
                                                        .index(arg.getKey())
                                                        .build();
                                            }

                                            if (arg.getValue().getValue() instanceof ManagedMap) {
                                                //FIXME
                                                return null;
                                            }

                                            if (arg.getValue().getValue() instanceof ManagedSet) {
                                                //FIXME
                                                return null;
                                            }

                                            //FIXME return null - and skip unsupported bean
                                            throw new IllegalArgumentException("Unknown ValueHolder type:" + arg.getValue().getValue().getClass().getSimpleName());
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
