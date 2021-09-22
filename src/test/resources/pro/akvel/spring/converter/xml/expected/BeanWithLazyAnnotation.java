
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithLazyAnnotation {


    @Bean("BeanWithLazyAnnotation")
    @Lazy
    public pro.akvel.spring.converter.testbean.BeanWithLazyAnnotation BeanWithLazyAnnotation() {
        return new pro.akvel.spring.converter.testbean.BeanWithLazyAnnotation();
    }

}
