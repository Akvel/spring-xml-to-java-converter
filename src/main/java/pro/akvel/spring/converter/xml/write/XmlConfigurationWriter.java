package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import pro.akvel.spring.converter.generator.BeanData;

import javax.annotation.Nullable;
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

    //FIXME докинуть в новый XML включения сканна аннтоций и классы которые были созданы
    //FIXME генерить общий конфиг с импортом остальных и докидывать его в xml?

    @SneakyThrows
    public void writeXmlWithoutConvertedBeans(Set<BeanData> beans,
                                              String configFilePath,
                                              String newConfigFilePath,
                                              String mainConfigClassName) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

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
