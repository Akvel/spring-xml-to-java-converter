package pro.akvel.spring.converter.xml.builder;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.type.MethodMetadata;
import pro.akvel.spring.converter.generator.param.ConstructorBeanParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyBeanParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

/**
 *
 * @author akvel
 * @since 12.09.2021
 */
public class RuntimeBeanReferenceBuilder implements ParamBuilder<RuntimeBeanReference> {

    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<RuntimeBeanReference> context) {
        RuntimeBeanReference refBeanName = context.getValue();

        return ConstructorBeanParam.builder()
                .ref(refBeanName.getBeanName())
                .className(getBeanClassName(context, refBeanName))
                .index(context.getIndex())
                .build();


    }

    @NonNull
    private String getBeanClassName(ParamBuildContext<RuntimeBeanReference> context, RuntimeBeanReference refBeanName) {
        BeanDefinition beanDefinition = context.getBeanDefinitionRegistry().getBeanDefinition(refBeanName.getBeanName());

        if (beanDefinition.getBeanClassName() != null) {
            return beanDefinition.getBeanClassName();
        }

        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            //
            MethodMetadata factoryMethodData = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
            if (factoryMethodData != null) {
                return factoryMethodData.getReturnTypeName();
            }else {
                throw new IllegalStateException("Can not get factory " + refBeanName.getBeanName());
            }
        }

        throw new IllegalStateException("Can not get bean class " + refBeanName.getBeanName());
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<RuntimeBeanReference> context) {
        RuntimeBeanReference refBeanName = context.getValue();

        return PropertyBeanParam.builder()
                .ref(refBeanName.getBeanName())
                .className(getBeanClassName(context, refBeanName))
                .name(context.getFieldName())
                .build();
    }
}
