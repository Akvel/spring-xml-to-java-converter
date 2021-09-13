
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithInitMethod;


/**
 * Generated Java based configuration
 *
 */
@Configuration
public class BeanWithInitDestroyMethod {


    @Bean(name = "BeanWithInitDestroyMethod", initMethod = "initMethod", destroyMethod = "destroyMethod")
    public BeanWithInitMethod BeanWithInitDestroyMethod() {
        return new BeanWithInitMethod();
    }

}
