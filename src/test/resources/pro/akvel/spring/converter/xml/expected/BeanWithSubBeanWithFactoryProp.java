
package pro.akvel.spring.converter.generator;

import org.springframework.beans.ManagementServerNode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithSubBeanWithFactoryProp {


    @Bean("BeanWithSubBeanWithFactoryProp")
    public pro.akvel.spring.converter.testbean.BeanWithSubBeanWithFactoryProp BeanWithSubBeanWithFactoryProp() {
        pro.akvel.spring.converter.testbean.BeanWithSubBeanWithFactoryProp bean = new pro.akvel.spring.converter.testbean.BeanWithSubBeanWithFactoryProp();
        bean.setParam1(ManagementServerNode.getManagementServerId());
        return bean;
    }

}
