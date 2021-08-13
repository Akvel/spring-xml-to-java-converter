package pro.akvel.spring.converter.xml.builder;

import lombok.NonNull;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class ParamBuildContext<TObject> {

    private final TObject value;

    private final String fieldName;

    private final Integer index;
    //FIXME может можно ограничить?
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

    public @NonNull Integer getIndex() {
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
