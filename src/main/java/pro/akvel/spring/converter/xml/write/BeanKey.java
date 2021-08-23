package pro.akvel.spring.converter.xml.write;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j;
import org.xml.sax.Attributes;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_CLASS;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_CLASS_NAME;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_ID;
import static pro.akvel.spring.converter.xml.write.BeanDefinitionElement.ATTR_BEAN_NAME;

@Builder
@EqualsAndHashCode
@ToString
@Log4j
public class BeanKey {
    private final String name;
    private final String className;


    @Nullable
    public static BeanKey getInstance(Attributes attributes) {
        if (attributes.getValue(ATTR_BEAN_CLASS) == null
        && attributes.getValue(ATTR_BEAN_CLASS_NAME) == null){
            log.info("No class attrs " + XmlUtils.printAll(attributes));
            return null;
        }

        var result = BeanKey.builder()
                .name(ofNullable(attributes.getValue(ATTR_BEAN_ID))
                        .orElse(attributes.getValue(ATTR_BEAN_NAME)))
                .className(ofNullable(attributes.getValue(ATTR_BEAN_CLASS))
                                .orElse(attributes.getValue(ATTR_BEAN_CLASS_NAME)))
                .build();

        return result;
    }
}
