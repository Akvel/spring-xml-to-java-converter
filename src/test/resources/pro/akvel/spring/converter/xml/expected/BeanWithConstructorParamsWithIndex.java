
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly1;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly2;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorParamsWithIndex {


    @Bean("BeanWithConstructorParamsWithIndex")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorParamsWithIndex BeanWithConstructorParamsWithIndex(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1,
        @Qualifier("bean2")
        BeanWithIdOnly2 bean2) {
        return new pro.akvel.spring.converter.testbean.BeanWithConstructorParamsWithIndex(bean1, bean2, null);
    }

}
