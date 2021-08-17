package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * FIXME
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@Builder
public class PropertyBeanParam implements PropertyParam {

    @NonNull
    private final String name;

    @NonNull
    private final String ref;

    @NonNull
    private final String className;
}
