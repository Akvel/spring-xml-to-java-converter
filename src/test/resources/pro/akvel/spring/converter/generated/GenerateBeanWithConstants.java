
package pro.akvel.spring.converter.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanWithConstants {


    @Bean
    public TestBean testBean() {
        return new TestBean(1, 2L, "3", "4");
    }

}
