package pro.akvel.spring.converter.xml.builder;

import org.springframework.beans.factory.config.TypedStringValue;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

public class TypedStringValueBuilder implements ParamBuilder<TypedStringValue> {
    @Override
    public ConstructorParam createConstructorParam(ParamBuildContext<TypedStringValue> context) {
        TypedStringValue value = context.getValue();

        if (value.getValue() == null) {
            return ConstructorNullParam.builder()
                    .index(context.getIndex())
                    .build();
        } else {
            return ConstructorConstantParam.builder()
                    .index(context.getIndex())
                    .type(context.getType() != null ? context.getType() : "java.lang.String")
                    .value(value.getValue())
                    .build();
        }
    }

    @Override
    public PropertyParam createPropertyParam(ParamBuildContext<TypedStringValue> context) {
        return PropertyValueParam.builder()
                .name(context.getFieldName())
                .value(context.getValue().getValue())
                .build();
    }
}
