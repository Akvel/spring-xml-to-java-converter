
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorConstArgs {


    @Bean("BeanWithConstructorConstArgs")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorConstArgs BeanWithConstructorConstArgs() {
        return new pro.akvel.spring.converter.testbean.BeanWithConstructorConstArgs("stringValue", null, "param3Value", 0, 1L);
    }

}
