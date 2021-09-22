
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithInt {


    @Bean("BeanWithInt")
    public pro.akvel.spring.converter.testbean.BeanWithInt BeanWithInt() {
        return new pro.akvel.spring.converter.testbean.BeanWithInt(255);
    }

}
