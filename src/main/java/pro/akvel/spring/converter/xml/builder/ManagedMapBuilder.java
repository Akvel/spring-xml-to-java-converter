package pro.akvel.spring.converter.xml.builder;

import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.ManagedMap;
import pro.akvel.spring.converter.generator.param.ConstructorMapParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;

import java.util.Map;
import java.util.stream.Collectors;

public class ManagedMapBuilder implements ParamBuilder<ManagedMap> {
    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<ManagedMap> context) {

        //FIXME when IDEA stop frizzing
        Map<String, String> values = (Map<String, String>) context.getValue().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> ((TypedStringValue) ((Map.Entry) e).getKey()).getValue(),
                        e -> ((TypedStringValue) ((Map.Entry) e).getValue()).getValue()
                ));

        return ConstructorMapParam.builder()
                .index(context.getIndex())
                .map(values)
                .build();
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<ManagedMap> context) {
        //FIXME ?
        return null;
    }
}
