package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import pro.akvel.spring.converter.generator.BeanData;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.List;
import java.util.stream.Collectors;

/**
 * FIXME
 */
public class XmlConfigurationWriter {

    //FIXME докинуть в новый XML включения сканна аннтоций и где сканить

    @SneakyThrows
    public void writeXmlWithoutConvertedBeans(List<BeanData> beans,
                                              String configFilePath,
                                              String newConfigFilePath) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        XmlConfigurationHandler handler = new XmlConfigurationHandler(
                beans.stream().map(it -> BeanKey.builder()
                                .name(it.getId())
                                .className(it.getClassName())
                                .build())
                        .collect(Collectors.toSet()),
                newConfigFilePath
        );

        saxParser.parse(configFilePath, handler);

    }
}
