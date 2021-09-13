package pro.akvel.spring.converter.xml;

import lombok.SneakyThrows;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.InputSource;

import java.io.FileInputStream;

/**
 * Beans reader
 *
 * @author akvel
 * @since 12.08.2020
 */



public class XmlConfigurationReader {
    @SneakyThrows
    public XmlConfigurationReader(String configurationPath){
        beanFactory = new DefaultListableBeanFactory();
        reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setValidationMode(XmlValidationModeDetector.VALIDATION_NONE);
        reader.loadBeanDefinitions(new InputSource(new FileInputStream(configurationPath)));

        //Load beans from java configurations
        new ConfigurationClassPostProcessor()
                .processConfigBeanDefinitions(beanFactory);
    }

    private final DefaultListableBeanFactory beanFactory;

    private final XmlBeanDefinitionReader reader;

    public BeanDefinitionRegistry getBeanFactory() {
        return beanFactory;
    }
}
