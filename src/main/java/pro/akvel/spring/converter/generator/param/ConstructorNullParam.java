package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * Null value
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@Builder
public class ConstructorNullParam implements ConstructIndexParam {

    private final Integer index;
}
