package pro.akvel.spring.converter.generator.param;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@SuperBuilder
public class ConstructorConstantParam implements ConstructIndexParam {

    private final String type;

    @NonNull
    private final String value;

    private final Integer index;
}
