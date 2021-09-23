package pro.akvel.spring.converter.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.ConfigurationData;
import pro.akvel.spring.converter.xml.builder.ParamBuildContext;
import pro.akvel.spring.converter.xml.builder.ParamBuilderProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * Converter from {@link BeanDefinition} to {@link ConfigurationData}
 *
 * @author akvel
 * @since 22.10.2020
 */
@Slf4j
public class ConfigurationDataConverter {
    public static final ConfigurationDataConverter INSTANCE = new ConfigurationDataConverter();


    private final ParamBuilderProvider paramBuilderProvider = ParamBuilderProvider.getInstance();
    private final BeanSupportValidator beanSupportValidator = new BeanSupportValidator();

    @Nullable
    public BeanData getConfigurationData(@Nonnull String name,
                                         @Nonnull BeanDefinitionRegistry beanDefinitionRegistry) {
        return getConfigurationData(beanDefinitionRegistry.getBeanDefinition(name), beanDefinitionRegistry, name);
    }


    @Nullable
    public BeanData getConfigurationData(@Nonnull BeanDefinition beanDefinition,
                                         @Nonnull BeanDefinitionRegistry beanDefinitionRegistry,
                                         @Nullable String beanName) {
        log.debug("Convert bean definition " + beanDefinition);

        if (!beanSupportValidator.isBeanSupport(beanDefinition, beanName, beanDefinitionRegistry)) {
            return null;
        }

        var beanClassName = requireNonNull(beanDefinition.getBeanClassName(), "Class name can not be null");

            return BeanData.builder()
                    .id(getBeanId(beanName, beanClassName))
                    .className(beanClassName)
                    .constructorParams(
                            Stream.concat(
                                            beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues()
                                                    .entrySet()
                                                    .stream(),
                                            beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                                                    .map(value -> Pair.of(Integer.MAX_VALUE, value)))
                                    .map(
                                            arg -> {
                                                log.debug("Convert param " + ReflectionToStringBuilder.toString(arg.getValue()));

                                                ParamBuildContext context = new ParamBuildContext(
                                                        arg.getValue().getValue(),
                                                        arg.getKey() != Integer.MAX_VALUE ? arg.getKey() : null,
                                                        beanDefinitionRegistry,
                                                        null,
                                                        arg.getValue().getType());

                                                return paramBuilderProvider.createConstructorParam(context, beanName);
                                            }
                                    )
                                    .collect(Collectors.toList()))
                    .propertyParams(beanDefinition.getPropertyValues()
                            .stream().map(it -> {
                                ParamBuildContext context = new ParamBuildContext(
                                        it.getValue(),
                                        null,
                                        beanDefinitionRegistry,
                                        it.getName(),
                                        null);

                                return paramBuilderProvider.createPropertyParam(context, beanName);

                            })
                            .collect(Collectors.toList()))
                    .initMethodName(beanDefinition.getInitMethodName())
                    .destroyMethodName(beanDefinition.getDestroyMethodName())
                    .dependsOn(beanDefinition.getDependsOn())
                    .description(beanDefinition.getDescription())
                    .scope(beanDefinition.getScope())
                    .primary(beanDefinition.isPrimary())
                    .lazyInit(beanDefinition.isLazyInit())
                    .qualifierName(getQualifier(beanDefinition))
                    .build();
    }

    @Nullable
    private static Set<String> getQualifier(@Nonnull BeanDefinition beanDefinition) {
        if (beanDefinition instanceof AbstractBeanDefinition){
            if (((AbstractBeanDefinition) beanDefinition).getQualifiers().isEmpty()){
                return null;
            }

            return ((AbstractBeanDefinition) beanDefinition).getQualifiers()
                    .stream().map(it -> it.getAttribute("value"))
                    .map(it -> (String) it)
                    .collect(Collectors.toSet());
        }

        return null;
    }

    @Nullable
    private static String getBeanId(@Nullable String name, @Nonnull String beanClassName) {
        if (name == null) {
            return null;
        }

        if (name.matches(beanClassName + "#\\d")) {
            return null;
        }

        return name;
    }

    /*public ConfigurationData getConfigurationData(@Nonnull BeanDefinitionRegistry beanDefinitionRegistry) {

    }*/

    public ConfigurationData getConfigurationData(@Nonnull Map<String, BeanDefinition> beanDefinitions,
                                                  @Nonnull BeanDefinitionRegistry allBeansDefinition) {
        AtomicInteger skippedBeansCount = new AtomicInteger();
        AtomicInteger convertedBeansCount = new AtomicInteger();

        return ConfigurationData.builder()
                .beans(Collections.unmodifiableSet(
                        beanDefinitions.entrySet().stream()
                                .map(it -> {
                                    var result = getConfigurationData(it.getValue(), allBeansDefinition, it.getKey());

                                    if (result == null) {
                                        skippedBeansCount.incrementAndGet();
                                        log.debug("\tSkipped: {}", it);
                                    } else {
                                        convertedBeansCount.incrementAndGet();
                                        log.debug("\tConverted: {}", it);
                                    }

                                    return result;
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet())
                ))
                .convertedBeansCount(convertedBeansCount.get())
                .skippedBeansCount(skippedBeansCount.get())
                .build();
    }

    public static ConfigurationDataConverter getInstance() {
        return INSTANCE;
    }
}
