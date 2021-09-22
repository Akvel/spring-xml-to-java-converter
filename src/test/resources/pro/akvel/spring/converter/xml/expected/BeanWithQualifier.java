
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 *
 */
@Configuration
public class BeanWithQualifier {


    @Bean("BeanWithQualifier")
    @Qualifier("contentCount")
    public pro.akvel.spring.converter.testbean.BeanWithQualifier BeanWithQualifier() {
        return new pro.akvel.spring.converter.testbean.BeanWithQualifier();
    }

}