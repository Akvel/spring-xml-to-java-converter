
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithDependsOn {


    @Bean("BeanWithDependsOn")
    @DependsOn({
            "bean1"
    })
    public pro.akvel.spring.converter.testbean.BeanWithDependsOn BeanWithDependsOn() {
        return new pro.akvel.spring.converter.testbean.BeanWithDependsOn();
    }

}
