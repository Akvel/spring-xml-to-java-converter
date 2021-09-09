package pro.akvel.spring.converter.xml.builder;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

public class RuntimeBeanReferenceBuilder implements ParamBuilder<RuntimeBeanReference> {

    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<RuntimeBeanReference> context) {
        RuntimeBeanReference refBeanName = context.getValue();

        return ConstructorBeanParam.builder()
                .ref(refBeanName.getBeanName())
                .className(context.getBeanDefinitionRegistry().getBeanDefinition(refBeanName.getBeanName()).getBeanClassName())
                .index(context.getIndex())
                .build();


    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<RuntimeBeanReference> context) {
        RuntimeBeanReference refBeanName = context.getValue();

        return PropertyBeanParam.builder()
                .ref(refBeanName.getBeanName())
                .className(context.getBeanDefinitionRegistry().getBeanDefinition(refBeanName.getBeanName()).getBeanClassName())
                .name(context.getFieldName())
                .build();
    }
}
