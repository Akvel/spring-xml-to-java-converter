package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;

/**
 * Null value
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@Builder
public class ConstructorNullParam implements ConstructIndexParam, Param {
    @Builder.Default
    final int index = Integer.MAX_VALUE;
}
