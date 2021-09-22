package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
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
public class PropertySubBeanParam implements PropertyParam {
    @NonNull
    private final BeanData beanData;

    @NonNull
    private final String name;

}
