package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Collection;

@Data
@Builder
public class PropertyMergeableParam implements MergeableParam<PropertyParam>, PropertyParam {
    @NonNull
    private final String name;


    private final Type type;

    @NonNull
    private final Collection<PropertyParam> values;
}
