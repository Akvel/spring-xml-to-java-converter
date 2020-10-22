package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Test;
import pro.akvel.spring.converter.generator.BeanData;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlConfigurationReaderTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    @Test
    void readXmlFile() throws FileNotFoundException {

        XmlConfigurationReader reader = new XmlConfigurationReader();

        List<BeanData> beans = reader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/spring-bean-configuration.xml"));

    }
}