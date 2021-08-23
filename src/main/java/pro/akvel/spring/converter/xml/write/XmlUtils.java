package pro.akvel.spring.converter.xml.write;

import lombok.experimental.UtilityClass;
import org.xml.sax.Attributes;

@UtilityClass
public class XmlUtils {

    public static String printAll(Attributes attributes) {
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < attributes.getLength(); index++){
            result.append(" ")
                    .append(attributes.getQName(index))
                    .append("=")
                    .append(attributes.getValue(index));
        }
        return result.toString();
    }
}
