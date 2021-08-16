
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly1;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly2;
import pro.akvel.spring.converter.testbean.SubBean;
import pro.akvel.spring.converter.testbean.SubBeanWithSubBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorWithCreateSubBeanWithSubBean {


    @Bean("BeanWithConstructorWithCreateSubBeanWithSubBean")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorWithCreateSubBeanWithSubBean BeanWithConstructorWithCreateSubBeanWithSubBean(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1,
        @Qualifier("bean2")
        BeanWithIdOnly2 bean2) {
        return new pro.akvel.spring.converter.testbean.BeanWithConstructorWithCreateSubBeanWithSubBean(new SubBeanWithSubBean(new SubBean(bean1, bean2)));
    }

}
