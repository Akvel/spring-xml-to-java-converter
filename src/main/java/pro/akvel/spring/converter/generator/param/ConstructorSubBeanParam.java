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
public class ConstructorSubBeanParam implements Param {
    @NonNull
    private final BeanData beanData;

    public ConstructorSubBeanParam(BeanData beanData) {
        this.beanData = beanData;
    }
}
