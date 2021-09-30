
package pro.akvel.spring.converter.generator;

import org.springframework.beans.ManagementServerNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithSubBeanWithFactory {


    @Bean("BeanWithSubBeanWithFactory")
    public pro.akvel.spring.converter.testbean.BeanWithSubBeanWithFactory BeanWithSubBeanWithFactory() {
        return new pro.akvel.spring.converter.testbean.BeanWithSubBeanWithFactory(ManagementServerNode.getManagementServerId());
    }

}
