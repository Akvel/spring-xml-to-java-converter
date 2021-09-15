package pro.akvel.spring.converter.xml.builder;

import org.springframework.beans.factory.config.TypedStringValue;
import pro.akvel.spring.converter.generator.param.ConstructorConstantParam;
import pro.akvel.spring.converter.generator.param.ConstructorNullParam;
import pro.akvel.spring.converter.generator.param.ConstructorParam;
import pro.akvel.spring.converter.generator.param.PropertyParam;
import pro.akvel.spring.converter.generator.param.PropertyValueParam;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 *
 * @author akvel
 * @since 12.09.2021
 */
public class TypedStringValueBuilder implements ParamBuilder<TypedStringValue> {

    public static final String DEFAULT_VALUE_TYPE = "java.lang.String";

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
                    .type(context.getType() != null ? context.getType() : DEFAULT_VALUE_TYPE)
                    .value(requireNonNull(value.getValue(), "value"))
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
