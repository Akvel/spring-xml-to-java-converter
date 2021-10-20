package pro.akvel.spring.converter.xml;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.InputSource;

import java.io.FileInputStream;
import java.util.Optional;
import java.util.Set;

/**
 * Beans reader
 *
 * @author akvel
 * @since 12.08.2020
 */
@Slf4j
public class XmlConfigurationReader {
    private DefaultListableBeanFactory beanFactory;

    public XmlConfigurationReader(String configurationPath) {
        this(Set.of(configurationPath), true);
    }

    @SneakyThrows
    public XmlConfigurationReader(Set<String> configurationPath, boolean strictMode) {
        beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.setValidationMode(XmlValidationModeDetector.VALIDATION_NONE);

        configurationPath.forEach(it -> {
                    try {
                        reader.loadBeanDefinitions(new InputSource(new FileInputStream(it)), it);
                        //Load beans from java configurations (if it is possible)
                        new ConfigurationClassPostProcessor()
                                .processConfigBeanDefinitions(beanFactory);
                    } catch (Exception e) {
                        log.debug("Error read: {} {}", e.getMessage(), e.getClass());
                        log.trace("Exception", e);

                        if (strictMode) {
                            if (e instanceof BeanDefinitionParsingException) {
                                throw new IllegalStateException(e);
                            }
                        }

                        if (configurationPath.size() == 1) {
                            beanFactory = null;
                        }
                    }
                }
        );


    }

    public Optional<DefaultListableBeanFactory> getBeanFactory() {
        return Optional.ofNullable(beanFactory);
    }
}
