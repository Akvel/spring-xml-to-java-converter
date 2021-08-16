
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class defaultInitDestroyMethods {


    @Bean(name = "testDefaultInitDestroyBean", initMethod = "initDef", destroyMethod = "shutdownDef")
    public pro.akvel.spring.converter.testbean.BeanWithoutId testDefaultInitDestroyBean() {
        return new pro.akvel.spring.converter.testbean.BeanWithoutId();
    }

}
