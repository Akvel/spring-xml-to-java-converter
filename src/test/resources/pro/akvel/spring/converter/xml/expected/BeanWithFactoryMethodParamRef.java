
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly1;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithFactoryMethodParamRef {


    @Bean("BeanWithFactoryMethodParamRef")
    public pro.akvel.spring.converter.testbean.BeanWithFactoryMethodParamRef BeanWithFactoryMethodParamRef(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1) {
        return pro.akvel.spring.converter.testbean.BeanWithFactoryMethodParamRef.getBean(bean1);
    }

}
