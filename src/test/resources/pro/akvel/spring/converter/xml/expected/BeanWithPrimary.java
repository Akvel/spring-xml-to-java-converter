
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithPrimary {


    @Bean("BeanWithPrimary")
    @Primary
    public pro.akvel.spring.converter.testbean.BeanWithPrimary BeanWithPrimary() {
        return new pro.akvel.spring.converter.testbean.BeanWithPrimary();
    }

}
