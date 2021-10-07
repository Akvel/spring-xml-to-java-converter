package pro.akvel.spring.converter.metadata;

import lombok.Builder;
import lombok.Data;

/**
 * @author akvel
 * @since 12.09.2021
 */
@Builder
@Data
public class JavaConfigurationMetadata {
    private final String className;
    private final String packageName;
    private final String configsPath;

    public String getFullClassName() {
        return packageName + "." + className;
    }
}
