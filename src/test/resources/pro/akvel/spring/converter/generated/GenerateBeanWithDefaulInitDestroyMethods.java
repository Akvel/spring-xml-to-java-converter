
package pro.akvel.spring.converter.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanWithDefaulInitDestroyMethods {


    /**
     * initMethod added by default-init-method
     * destroyMethod added by bean element default-destroy-method
     * 
     * 
     */
    @Bean(initMethod = "defaultInitMethod", destroyMethod = "defaultDestroyMethod")
    public TestBean testBean() {
        return new TestBean();
    }

}
