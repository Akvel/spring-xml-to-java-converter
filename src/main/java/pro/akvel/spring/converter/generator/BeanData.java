package pro.akvel.spring.converter.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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
    @Nonnull
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
}
