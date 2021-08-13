package pro.akvel.spring.converter.generator.param;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
public class ConstructorMapParam implements ConstructIndexParam {
    final Integer index;
    final Map<String, String> map;

    @Override
    public Integer getIndex() {
        return index;
    }
}
