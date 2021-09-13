package pro.akvel.spring.converter.generator;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Set;

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
    private final Set<BeanData> beans;
    @NonNull
    private final Integer convertedBeansCount;
    @NonNull
    private final Integer skippedBeansCount;
}
