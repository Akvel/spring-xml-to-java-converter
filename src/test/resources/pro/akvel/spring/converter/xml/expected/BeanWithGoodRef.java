
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithGoodRef {


    @Bean("BeanWithGoodRef")
    public pro.akvel.spring.converter.testbean.BeanWithGoodRef BeanWithGoodRef(
        @Qualifier("BeanWithRefBeanWithFactoryConst")
        org.springframework.beans.BeanWithRefBeanWithFactoryConst BeanWithRefBeanWithFactoryConst) {
        return new pro.akvel.spring.converter.testbean.BeanWithGoodRef(BeanWithRefBeanWithFactoryConst);
    }

}
