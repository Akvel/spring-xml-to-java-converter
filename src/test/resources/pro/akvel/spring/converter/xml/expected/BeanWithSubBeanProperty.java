
package pro.akvel.spring.converter.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.SubBean;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithSubBeanProperty {


    @Bean("BeanWithSubBeanProperty")
    public pro.akvel.spring.converter.testbean.BeanWithSubBeanProperty BeanWithSubBeanProperty() {
        pro.akvel.spring.converter.testbean.BeanWithSubBeanProperty bean = new pro.akvel.spring.converter.testbean.BeanWithSubBeanProperty();
        bean.setParam1(new SubBean());
        return bean;
    }

}
