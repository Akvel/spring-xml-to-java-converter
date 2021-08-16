
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
public class BeanWithProperty {


    @Bean("BeanWithProperty")
    public pro.akvel.spring.converter.testbean.BeanWithProperty BeanWithProperty(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1) {
        pro.akvel.spring.converter.testbean.BeanWithProperty bean = new pro.akvel.spring.converter.testbean.BeanWithProperty();
        bean.setProperty1("value1");
        bean.setProperty2(bean1);
        return bean;
    }

}
