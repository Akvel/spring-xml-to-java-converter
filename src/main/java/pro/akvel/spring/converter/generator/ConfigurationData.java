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

    @NonNull
    private final List<BeanData> beans;
}
