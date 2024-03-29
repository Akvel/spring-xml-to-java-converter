package pro.akvel.spring.converter.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Bean;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * Bean definition model
 *
 * @author akvel
 * @since 12.08.2020
 */
@Data
@Builder
public class BeanData {
    /**
     * Bean id ({@link Bean#name()}
     */
    @Nullable
    private final String id;

    /**
     * Full class name, with package
     */
    @NonNull
    private final String className;

    /**
     * Bean constructor params with same order as in xml configuration
     */
    @NonNull
    @Builder.Default
    private final List<ConstructorParam> constructorParams = List.of();

    /**
     * Bena property params
     */
    @NonNull
    @Builder.Default
    private final List<PropertyParam> propertyParams = List.of();

    /**
     * Name method for {@link Bean#initMethod()}
     */
    @Nullable
    private final String initMethodName;

    /**
     * Name method for {@link Bean#destroyMethod()}
     */
    @Nullable
    private final String destroyMethodName;

    /**
     * {@link BeanDefinition#getDependsOn()}}
     */
    @Nullable
    private final String[] dependsOn;

    /**
     * {@link BeanDefinition#getScope()}
     */
    @Builder.Default
    private final String scope = "";

    /**
     * {@link BeanDefinition#isPrimary()}
     */
    private final boolean primary;

    /**
     * {@link BeanDefinition#getDescription()}
     */
    private final String description;

    /**
     * {@link BeanDefinition#isLazyInit()}
     */
    private final boolean lazyInit;

    /**
     * {@link AbstractBeanDefinition#getQualifier(String)}
      */
    @Nullable
    private final Set<String> qualifierName;

    /**
     * {@link AbstractBeanDefinition#getFactoryMethodName()}
     */
    @Nullable
    private final String factoryMethodName;
}
