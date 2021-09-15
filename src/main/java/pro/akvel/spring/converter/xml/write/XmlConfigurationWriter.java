package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import pro.akvel.spring.converter.generator.BeanData;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author akvel
 * @since 12.09.2021
 */
public class XmlConfigurationWriter {
    @SneakyThrows
    public void writeXmlWithoutConvertedBeans(Set<BeanData> beans,
                                              String configFilePath,
                                              String newConfigFilePath,
                                              String mainConfigClassName) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        SAXParser saxParser = factory.newSAXParser();
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        saxParser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        XmlConfigurationHandler handler = new XmlConfigurationHandler(
                beans.stream().map(it -> BeanKey.builder()
                                .name(it.getId())
                                .className(it.getClassName())
                                .build())
                        .collect(Collectors.toSet()),
                newConfigFilePath,
                mainConfigClassName
        );

        saxParser.parse(configFilePath, handler);
    }
}
