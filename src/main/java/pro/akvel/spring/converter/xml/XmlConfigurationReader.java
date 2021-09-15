package pro.akvel.spring.converter.xml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.util.Optional;

/**
 * Beans reader
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class XmlConfigurationReader {
    private DefaultListableBeanFactory beanFactory;

    @SneakyThrows
    public XmlConfigurationReader(String configurationPath) {
        beanFactory = new DefaultListableBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setValidationMode(XmlValidationModeDetector.VALIDATION_NONE);
        try {
            reader.loadBeanDefinitions(new InputSource(new FileInputStream(configurationPath)));
            //Load beans from java configurations
            new ConfigurationClassPostProcessor()
                    .processConfigBeanDefinitions(beanFactory);
        } catch (Exception e) {
            log.debug("Error", e);
            beanFactory = null;
        }


    }

    public Optional<BeanDefinitionRegistry> getBeanFactory() {
        return Optional.ofNullable(beanFactory);
    }
}
