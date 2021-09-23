
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.SubBeanWithSubBeanWithProperty;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithSubBeanWithProperty {


    @Bean("BeanWithSubBeanWithProperty")
    public pro.akvel.spring.converter.testbean.BeanWithSubBeanWithProperty BeanWithSubBeanWithProperty(
        @Qualifier("BeanWithIdOnly")
        pro.akvel.spring.converter.testbean.BeanWithIdOnly BeanWithIdOnly) {
        SubBeanWithSubBeanWithProperty bean0 = new SubBeanWithSubBeanWithProperty();
        bean0.setProperty1("value1");
        bean0.setProperty2(BeanWithIdOnly);
        return new pro.akvel.spring.converter.testbean.BeanWithSubBeanWithProperty(bean0);
    }

}
