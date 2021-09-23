package pro.akvel.spring.converter.generator.param;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Collection;

@Data
@SuperBuilder
public class ConstructorMergeableParam implements MergeableParam<PropertyParam>, ConstructIndexParam {
    private final Type type;

    private final Collection<PropertyParam> values;

    private final Integer index;
}
