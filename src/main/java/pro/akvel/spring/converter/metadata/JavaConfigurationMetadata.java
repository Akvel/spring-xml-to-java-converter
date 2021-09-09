package pro.akvel.spring.converter.metadata;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JavaConfigurationMetadata {
    private final String javaConfigFileClassName;
    private final String packageName;
}
