
package pro.akvel.spring.converter.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.SubBean;
import pro.akvel.spring.converter.generator.SubSubBean;
import pro.akvel.spring.converter.generator.TestBean;
import pro.akvel.spring.converter.generator.TestBean1;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanWithSubBeanWithSubBean {


    @Bean
    public TestBean testBean(
        @Qualifier("value1")
        TestBean1 value1) {
        return new TestBean(new SubBean(new SubSubBean(null, value1)));
    }

}
