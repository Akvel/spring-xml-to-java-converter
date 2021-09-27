
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithTrueFalse {


    @Bean("BeanWithTrueFalse")
    public pro.akvel.spring.converter.testbean.BeanWithTrueFalse BeanWithTrueFalse() {
        pro.akvel.spring.converter.testbean.BeanWithTrueFalse bean = new pro.akvel.spring.converter.testbean.BeanWithTrueFalse(true, false);
        bean.setProperty1(true);
        bean.setProperty2(false);
        return bean;
    }

}
