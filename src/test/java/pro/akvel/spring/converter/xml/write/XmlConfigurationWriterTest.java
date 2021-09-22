package pro.akvel.spring.converter.xml.write;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pro.akvel.spring.converter.generator.BeanData;
import pro.akvel.spring.converter.xml.ConfigurationDataConverter;
import pro.akvel.spring.converter.xml.XmlConfigurationReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class XmlConfigurationWriterTest {

    private static final String OUTPUT_PATH = "build/tmp";

    private static final Path root = Paths.get(".").normalize().toAbsolutePath();

    @Test
    public void allConverted() {
        XmlConfigurationWriter writer = new XmlConfigurationWriter();

        BeanData bean = Mockito.mock(BeanData.class);
        when(bean.getClassName()).thenReturn("pro.akvel.spring.converter.testbean.BeanWithoutId");

        String configFilePath = root + "/src/test/resources/pro/akvel/spring/converter/xml/write/allConverted.xml";
        String newConfigFilePath = OUTPUT_PATH + "/allConverted.xml";
        writer.writeXmlWithoutConvertedBeans(
                Set.of(bean),
                configFilePath,
                newConfigFilePath,
                "pro.akvel.text.MainConfig"
        );


        Path generatedFile = Paths.get(newConfigFilePath);
        Path expectedFile = Paths.get(root + "/src/test/resources/pro/akvel/spring/converter/xml/write/expected/allConverted.xml");


        assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void allSkipped() {
        XmlConfigurationWriter writer = new XmlConfigurationWriter();

        BeanData bean = Mockito.mock(BeanData.class);
        when(bean.getClassName()).thenReturn("AnotherClass");

        String configFilePath = root + "/src/test/resources/pro/akvel/spring/converter/xml/write/allSkipped.xml";
        String newConfigFilePath = OUTPUT_PATH + "/allSkipped.xml";
        writer.writeXmlWithoutConvertedBeans(
                Set.of(bean),
                configFilePath,
                newConfigFilePath,
                "pro.akvel.text.MainConfig"
        );

        Path generatedFile = Paths.get(newConfigFilePath);
        Path expectedFile = Paths.get(root + "/src/test/resources/pro/akvel/spring/converter/xml/write/expected/allSkipped.xml");


        assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @Test
    public void testFullXml() {
        XmlConfigurationWriter writer = new XmlConfigurationWriter();


        String configFilePath = root + "/src/test/resources/pro/akvel/spring/converter/xml/configs/spring-bean-configuration-full.xml";

        XmlConfigurationReader reader = new XmlConfigurationReader(configFilePath);
        var beansConf = ConfigurationDataConverter.getInstance()
                .getConfigurationData(Arrays.stream(reader.getBeanFactory().get()
                                        .getBeanDefinitionNames())
                                .collect(Collectors.toMap(it -> it,
                                        it -> reader.getBeanFactory().get().getBeanDefinition(it))),
                        reader.getBeanFactory().get());

        String newConfigFilePath = OUTPUT_PATH + "/spring-bean-configuration-full.xml";
        writer.writeXmlWithoutConvertedBeans(
                beansConf.getBeans(),
                configFilePath,
                newConfigFilePath,
                "pro.akvel.text.MainConfig"
        );

        Path generatedFile = Paths.get(newConfigFilePath);
        Path expectedFile = Paths.get(root + "/src/test/resources/pro/akvel/spring/converter/xml/write/expected/spring-bean-configuration-full.xml");

        assertEquals(getLines(expectedFile), getLines(generatedFile));
    }

    @SneakyThrows
    private static String getLines(Path expectedFile) {
        return Files.readAllLines(expectedFile, StandardCharsets.UTF_8)
                .stream()
                .map(it -> it.replaceAll("[\\n\\s]", ""))
                .collect(Collectors.joining());
    }


}