package pro.akvel.spring.converter.xml.builder;

import lombok.Builder;
import lombok.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 *
 * @author akvel
 * @since 12.09.2021
 */
public class ParamBuildContext<TObject> {

    private final TObject value;

    private final String fieldName;

    private final Integer index;

    private final BeanDefinitionRegistry beanDefinitionRegistry;

    private final String type;


    public ParamBuildContext(TObject value,
                             Integer index,
                             BeanDefinitionRegistry beanDefinitionRegistry,
                             String fieldName,
                             String type) {
        this.value = value;
        this.index = index;
        this.beanDefinitionRegistry = beanDefinitionRegistry;
        this.fieldName = fieldName;
        this.type = type;
    }

    @NonNull
    public Integer getIndex() {
        return index;
    }

    public TObject getValue() {
        return value;
    }

    public BeanDefinitionRegistry getBeanDefinitionRegistry() {
        return beanDefinitionRegistry;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getType() {
        return type;
    }
}
