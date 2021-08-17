package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import pro.akvel.spring.converter.generator.BeanData;

/**
 *
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@Builder
public class ConstructorSubBeanParam implements ConstructIndexParam {
    @NonNull
    private final BeanData beanData;

    private final Integer index;

}
