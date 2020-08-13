
package pro.akvel.spring.converter.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.PropertyBean;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanProperties {


    @Bean
    public TestBean testBean(
        @Qualifier("refToBean")
        PropertyBean refToBean) {
        TestBean bean = new TestBean();
        bean.setProperty1("value1");
        bean.setProperty2(refToBean);
        return bean;
    }

}
