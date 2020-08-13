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
public class PropertyBeanParam implements Param, PropertyParam {

    @NonNull
    final String name;

    @NonNull
    final String ref;

    @NonNull
    final String className;
}
