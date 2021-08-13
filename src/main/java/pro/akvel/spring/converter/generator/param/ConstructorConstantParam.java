package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * FIXME
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

    //@NonNull
    private final Integer index;
}
