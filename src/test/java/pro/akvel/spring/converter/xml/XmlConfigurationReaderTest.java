package pro.akvel.spring.converter.xml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author akvel
 * @since 23.10.2020
 */
public class XmlConfigurationReaderTest {

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    @Test
    public void emptyXml() throws FileNotFoundException {
        var xmlReader = new XmlConfigurationReader();
        xmlReader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-empty.xml"));

        Assertions.assertEquals(0, xmlReader.getBeanFactory().getBeanDefinitionCount());
    }

    @Test()
    public void xmlFileWithImport() throws FileNotFoundException {
        var xmlReader = new XmlConfigurationReader();

        xmlReader.readXmlFile(new File(root
                + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-withImport.xml"));

        Assertions.assertEquals(1, xmlReader.getBeanFactory().getBeanDefinitionCount());
    }


    @Test
    public void notBeansDefinition() throws FileNotFoundException {
        var xmlReader = new XmlConfigurationReader();

        xmlReader.readXmlFile(new File(root + "/src/test/resources/pro/akvel/spring/converter/xml/configs/simple.xml"));

        Assertions.assertEquals(xmlReader.getBeanFactory().getBeanDefinitionCount(), 0);
    }

    @Test
    public void xmlWithClassImport() throws FileNotFoundException {
        Assertions.fail();
    }
}
