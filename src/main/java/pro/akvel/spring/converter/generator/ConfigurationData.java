package pro.akvel.spring.converter.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.List;

/**
 * ConfigurationData
 *
 * @author akvel
 * @since 17.08.2020
 */
@Data
@Builder
public class ConfigurationData {
    /**
     * <beans ...default-init-method="defaultBeanInitMethod" />
     */
    private final String defaultBeanInitMethod;
    /**
     * <beans ...default-destroy-method="defaultBeanDestroyMethod" />
     */
    private final String defaultBeanDestroyMethod;

    @NonNull
    private final List<BeanData> beans;
}
