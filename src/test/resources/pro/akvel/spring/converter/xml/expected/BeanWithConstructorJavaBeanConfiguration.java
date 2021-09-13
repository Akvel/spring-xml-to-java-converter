
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorJavaBeanConfiguration {


    @Bean("BeanWithConstructorJavaBean")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorJavaBean BeanWithConstructorJavaBean(
        @Qualifier("testJavaBean")
        String testJavaBean) {
        return new pro.akvel.spring.converter.testbean.BeanWithConstructorJavaBean(testJavaBean);
    }

}
