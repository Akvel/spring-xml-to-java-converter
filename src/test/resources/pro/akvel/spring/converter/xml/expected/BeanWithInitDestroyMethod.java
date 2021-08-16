
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithInitDestroyMethod {


    @Bean(name = "BeanWithInitDestroyMethod", initMethod = "initMethod", destroyMethod = "destroyMethod")
    public pro.akvel.spring.converter.testbean.BeanWithInitMethod BeanWithInitDestroyMethod() {
        return new pro.akvel.spring.converter.testbean.BeanWithInitMethod();
    }

}
