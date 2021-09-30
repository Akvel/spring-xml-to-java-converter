
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithFactoryMethod {


    @Bean("BeanWithFactoryMethod")
    public pro.akvel.spring.converter.testbean.BeanWithFactoryMethod BeanWithFactoryMethod() {
        return pro.akvel.spring.converter.testbean.BeanWithFactoryMethod.getBean("param");
    }

}
