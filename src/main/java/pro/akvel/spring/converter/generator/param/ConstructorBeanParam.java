package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * FIXME
 *
 * @author akvel
 * @since 12.08.2020
 */
@Data
@Builder
public class ConstructorBeanParam implements ConstructIndexParam, Param {
    @NonNull
    private final String className;

    @NonNull
    private final String ref;

    @NonNull
    private final Integer index;
}
