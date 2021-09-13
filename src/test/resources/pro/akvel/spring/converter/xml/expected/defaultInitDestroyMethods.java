
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithoutId;


/**
 * Generated Java based configuration
 *
 */
@Configuration
public class defaultInitDestroyMethods {


    @Bean(name = "testDefaultInitDestroyBean", initMethod = "initDef", destroyMethod = "shutdownDef")
    public BeanWithoutId testDefaultInitDestroyBean() {
        return new BeanWithoutId();
    }

}
