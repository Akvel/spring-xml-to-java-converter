
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.SubBeanWithDash;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithDash {


    @Bean("BeanWith-Dash")
    public pro.akvel.spring.converter.testbean.BeanWithDash BeanWith_Dash(
        @Qualifier("SubBeanWith-Dash")
        SubBeanWithDash SubBeanWith_Dash) {
        pro.akvel.spring.converter.testbean.BeanWithDash bean = new pro.akvel.spring.converter.testbean.BeanWithDash(SubBeanWith_Dash);
        bean.setParam(SubBeanWith_Dash);
        return bean;
    }

}
