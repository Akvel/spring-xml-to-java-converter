
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import pro.akvel.spring.converter.testbean.BeanWithDependsOn;


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
    public BeanWithDependsOn BeanWithDependsOnMulti() {
        return new BeanWithDependsOn();
    }

}
