package pro.akvel.spring.converter.xml;

import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.generator.ConfigurationData;
import pro.akvel.spring.converter.xml.builder.ParamBuildContext;
import pro.akvel.spring.converter.xml.builder.ParamBuilderProvider;

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
@Log4j
public class ConfigurationDataConverter {
    public static final ConfigurationDataConverter INSTANCE = new ConfigurationDataConverter();


    private final ParamBuilderProvider paramBuilderProvider = new ParamBuilderProvider();
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
        log.info("Convert bean definition " + beanDefinition);

        if (!beanSupportValidator.isBeanSupport(beanDefinition, beanName)) {
            return null;
        }

        return BeanData.builder()
                .id(getBeanId(beanName, beanDefinition.getBeanClassName()))
                .className(beanDefinition.getBeanClassName())
                .constructorParams(
                        Stream.concat(
                                        beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues()
                                                .entrySet()
                                                .stream(),
                                        beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().stream()
                                                .map(value -> Pair.of(Integer.MAX_VALUE, value)))
                                .map(
                                        arg -> {
                                            log.info("Convert param " + ReflectionToStringBuilder.toString(arg.getValue()));

                                            ParamBuildContext context = new ParamBuildContext(
                                                    arg.getValue().getValue(),
                                                    arg.getKey() != Integer.MAX_VALUE ? arg.getKey() : null,
                                                    beanDefinitionRegistry,
                                                    null,
                                                    arg.getValue().getType());

                                            return paramBuilderProvider.createConstructorParam(context);
                                        }
                                )
                                .collect(Collectors.toList()))
                .propertyParams(beanDefinition.getPropertyValues()
                        .stream().map(it -> {
                            log.info("Convert param " + ReflectionToStringBuilder.toString(it));

                            ParamBuildContext context = new ParamBuildContext(
                                    it.getValue(),
                                    null,
                                    beanDefinitionRegistry,
                                    it.getName(),
                                    null);

                            return paramBuilderProvider.createPropertyParam(context);

                        })
                        .collect(Collectors.toList()))
                .initMethodName(beanDefinition.getInitMethodName())
                .destroyMethodName(beanDefinition.getDestroyMethodName())
                .dependsOn(beanDefinition.getDependsOn())
                .description(beanDefinition.getDescription())
                .scope(beanDefinition.getScope())
                .primary(beanDefinition.isPrimary())
                .build();


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

    public static ConfigurationDataConverter getInstance(){
        return INSTANCE;
    }
}
