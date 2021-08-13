package pro.akvel.spring.converter.xml.builder;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.ConstructorSubBeanParam;
import pro.akvel.spring.converter.generator.param.Param;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;

@Log4j
public class BeanDefinitionHolderBuilder implements ParamBuilder<BeanDefinitionHolder> {
    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<BeanDefinitionHolder> context) {
        BeanDefinitionHolder value = context.getValue();

        if (value.getBeanDefinition().getFactoryBeanName() != null) {
            //FIXME
            return null;
        }

        var subBean = ConfigurationDataConverter.getConfigurationData(value.getBeanDefinition(),
                context.getBeanDefinitionRegistry(), null);

        if (subBean == null) {
            //FIXME тут надо как-то выдавать наружу чуто бин не получится сделать
            log.info("subBean return null");
            return null;
        }

        return ConstructorSubBeanParam.builder()
                .beanData(subBean)
                .index(context.getIndex())
                .build();
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<BeanDefinitionHolder> context) {
        //FIXME
        return null;
    }
}
