
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithConstParam;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly1;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorWithCreateSubBeanWithConstArg {


    @Bean("BeanWithConstructorWithCreateSubBeanWithConstArg")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorWithCreateSubBeanWithConstArg BeanWithConstructorWithCreateSubBeanWithConstArg(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1) {
        return new pro.akvel.spring.converter.testbean.BeanWithConstructorWithCreateSubBeanWithConstArg(bean1, new BeanWithConstParam(123));
    }

}
