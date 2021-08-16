package pro.akvel.spring.converter.xml;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.InputSource;
import pro.akvel.spring.converter.generator.BeanData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Beans reader
 *
 * @author akvel
 * @since 12.08.2020
 */
public class XmlConfigurationReader {

    private final BeanDefinitionRegistry beanFactory;

    private final XmlBeanDefinitionReader reader;

    public XmlConfigurationReader() {
        beanFactory = new DefaultListableBeanFactory();
        reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setValidationMode(XmlValidationModeDetector.VALIDATION_NONE);
    }

    public List<BeanData> readXmlFile(File file) throws FileNotFoundException {
        reader.loadBeanDefinitions(new InputSource(new FileInputStream(file)));

        return Arrays.stream(beanFactory.getBeanDefinitionNames())
                .map(name -> ConfigurationDataConverter.getConfigurationData(name, beanFactory))
                .collect(Collectors.toList());
    }

    BeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }
}
