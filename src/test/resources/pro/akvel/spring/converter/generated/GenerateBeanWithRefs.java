
package pro.akvel.spring.converter.test;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.generator.TestBean;
import pro.akvel.spring.converter.generator.TestBean1;
import pro.akvel.spring.converter.generator.TestBean2;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class GenerateBeanWithRefs {


    @Bean("beanId")
    public TestBean beanId(
        @Qualifier("value1")
        TestBean1 value1,
        @Qualifier("value2")
        TestBean2 value2) {
        return new TestBean(value1, value2);
    }

}
