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
    //FIXME нужно искать по xml id и вытаскивать тип
    @NonNull
    private final String className;

    private final String ref;

    @Builder.Default
    private final int index = Integer.MAX_VALUE;
}
