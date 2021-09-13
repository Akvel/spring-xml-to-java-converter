package pro.akvel.spring.converter.xml.builder;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.Param;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;

@Slf4j
public class BeanDefinitionHolderBuilder implements ParamBuilder<BeanDefinitionHolder> {

    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<BeanDefinitionHolder> context) {
        BeanDefinitionHolder value = context.getValue();

        if (value.getBeanDefinition().getFactoryBeanName() != null) {
            log.debug("getFactoryBeanName not supported");
            return null;
        }

        var subBean = ConfigurationDataConverter.getInstance().getConfigurationData(value.getBeanDefinition(),
                context.getBeanDefinitionRegistry(), null);

        if (subBean == null) {
            log.debug("subBean not supported");
            return null;
        }

        return ConstructorSubBeanParam.builder()
                .beanData(subBean)
                .index(context.getIndex())
                .build();
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<BeanDefinitionHolder> context) {
        log.debug("createPropertyParam not supported");
        return null;
    }
}
