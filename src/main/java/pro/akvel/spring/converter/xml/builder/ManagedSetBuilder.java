package pro.akvel.spring.converter.xml.builder;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedSet;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.ConstructorSetParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ManagedSetBuilder implements ParamBuilder<ManagedSet> {
    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<ManagedSet> context) {

        //FIXME when IDEA stop frizzing
        Set<String> values = (Set<String>) context.getValue().stream()
                .map(it -> ((TypedStringValue) it).getValue())
                .collect(Collectors.toSet());

        return ConstructorSetParam.builder()
                .index(context.getIndex())
                .map(values)
                .build();
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<ManagedSet> context) {
        //FIXME ?
        return null;
    }
}
