
package pro.akvel.spring.converter.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 *
 */
@Configuration
public class GenerateBeanWithoutId {


    @Bean
    public TestBean testBean() {
        return new TestBean();
    }

}
