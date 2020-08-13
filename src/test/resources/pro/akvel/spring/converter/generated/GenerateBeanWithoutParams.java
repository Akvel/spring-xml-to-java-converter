
package pro.akvel.spring.converter.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanWithoutParams {


    @Bean("beanId")
    public TestBean beanId() {
        return new TestBean();
    }

}
