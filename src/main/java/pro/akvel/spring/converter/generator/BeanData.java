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
 * FIXME
 *
 * @author akvel
 * @since 12.08.2020
 */
@Data
@Builder
public class BeanData {

    //FIXME support scopes

    /**
     * Bean id ({@link Bean#name()}
     */
    @Nullable
    final String id;

    /**
     * Full class name, with package
     */
    @Nonnull
    final String clazzName;

    /**
     * Bean constructor params with same order as in xml configuration
     */
    @NonNull
    @Builder.Default
    final List<ConstructorParam> constructorParams = List.of();

    /**
     * Bena property params
     */
    @NonNull
    @Builder.Default
    final List<PropertyParam> propertyParams = List.of();

    /**
     * Name method for {@link Bean#initMethod()}
     */
    @Nullable
    final String initMethodName;

    /**
     * Name method for {@link Bean#destroyMethod()}
     */
    @Nullable
    final String destroyMethodName;

    /**
     * {@link BeanDefinition#getDependsOn()}}
     */
    @Nullable
    final String[] dependsOn;

    /**
     * {@link BeanDefinition#getScope()}
     */
    @Nullable
    @Builder.Default
    String scope = "";

    /**
     * {@link BeanDefinition#isPrimary()}
     */
    final boolean primary;

    /**
     * {@link BeanDefinition#getDescription()}
     */
    final String description;
}
