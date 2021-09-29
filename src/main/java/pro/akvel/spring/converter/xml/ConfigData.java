package pro.akvel.spring.converter.xml;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.io.File;

@Builder
@Data
public class ConfigData {
    @NonNull
    private final File path;
    @NonNull
    private final String sourcePath;
}
