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
public class ConstructorBeanParam implements Param {
    //FIXME нужно искать по xml id и вытаскивать тип
    @NonNull
    final String className;

    final String ref;
}
