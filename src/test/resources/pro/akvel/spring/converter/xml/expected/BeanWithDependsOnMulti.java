
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithDependsOnMulti {


    @Bean("BeanWithDependsOnMulti")
    @DependsOn({
            "bean1",
            "bean2"
    })
    public pro.akvel.spring.converter.testbean.BeanWithDependsOn BeanWithDependsOnMulti() {
        return new pro.akvel.spring.converter.testbean.BeanWithDependsOn();
    }

}
