package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import org.xml.sax.SAXException;
import pro.akvel.spring.converter.generator.BeanData;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FIXME
 */
public class XmlConfigurationWriter {

    public void writeXmlWithoutConvertedBeans(List<BeanData> beans,
                                              String configFilePath,
                                              String newConfigFilePath) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = null;
        try {
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        XmlConfigurationHandler handler = null;
        try {
            handler = new XmlConfigurationHandler(
                    beans.stream().map(it -> BeanKey.builder()
                                    .name(it.getId())
                                    .className(it.getClazzName())
                                    .build())
                            .collect(Collectors.toSet()),
                    newConfigFilePath
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            saxParser.parse(configFilePath, handler);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
