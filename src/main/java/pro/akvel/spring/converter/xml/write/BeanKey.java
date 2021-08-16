package pro.akvel.spring.converter.xml.write;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xml.sax.Attributes;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_CLASS;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_ID;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_NAME;

@Builder
@EqualsAndHashCode
@ToString
public class BeanKey {
    private final String name;
    private final String className;


    public static BeanKey getInstance(Attributes attributes) {
        var result = BeanKey.builder()
                .name(ofNullable(attributes.getValue(ATTR_BEAN_ID))
                        .orElse(attributes.getValue(ATTR_BEAN_NAME)))
                .className(requireNonNull(attributes.getValue(ATTR_BEAN_CLASS), "class attr"))
                .build();

        return result;
    }
}
