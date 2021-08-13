package pro.akvel.spring.converter.generator.param;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.Set;

@Data
@SuperBuilder
public class ConstructorSetParam implements ConstructIndexParam {
    final Integer index;
    final Set<String> map;

    @Override
    public Integer getIndex() {
        return index;
    }
}
