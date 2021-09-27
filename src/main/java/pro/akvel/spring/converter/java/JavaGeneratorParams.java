package pro.akvel.spring.converter.java;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JavaGeneratorParams {
    private final boolean trueFalseAsBoolean;
}
