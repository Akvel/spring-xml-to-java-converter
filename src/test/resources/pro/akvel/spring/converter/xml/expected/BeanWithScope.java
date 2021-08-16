
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithScope {


    @Bean("BeanWithScope")
    @Scope("singleton")
    public pro.akvel.spring.converter.testbean.BeanWithScope BeanWithScope() {
        return new pro.akvel.spring.converter.testbean.BeanWithScope();
    }

}
