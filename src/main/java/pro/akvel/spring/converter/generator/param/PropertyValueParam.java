package pro.akvel.spring.converter.generator.param;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author akvel
 * @since 13.08.2020
 */
@Data
@Builder
public class PropertyValueParam implements PropertyParam {
    private final String name;
    private final String value;
}
