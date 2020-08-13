
package pro.akvel.spring.converter.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.PropertyBean;
import pro.akvel.spring.converter.generator.SubBean;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateSubBeanProperties {


    @Bean
    public TestBean testBean(
        @Qualifier("ref")
        PropertyBean ref) {
        SubBean bean = new SubBean();
        bean.setProperty1("value1");
        bean.setProperty2(ref);
        return new TestBean(bean);
    }

}
