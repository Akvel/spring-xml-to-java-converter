
package pro.akvel.spring.converter.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanInitDestroyMethod {


    @Bean(name = "beanId", initMethod = "initMethod", destroyMethod = "destroyMethod")
    public TestBean beanId() {
        return new TestBean();
    }

}
