
package pro.akvel.spring.converter.generator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithPlaceholder {

    @Value("${pl1}")
    private String pl1;
    @Value("${pl2}")
    private String pl2;

    @Bean("BeanWithPlaceholder")
    public pro.akvel.spring.converter.testbean.BeanWithPlaceholder BeanWithPlaceholder() {
        pro.akvel.spring.converter.testbean.BeanWithPlaceholder bean = new pro.akvel.spring.converter.testbean.BeanWithPlaceholder("test${pl1}passed", "${pl2}");
        bean.setProperty1((("test"+ pl1)+"passed"));
        bean.setProperty2(pl2);
        bean.setProperty3(((pl1 +" and ")+ pl2));
        return bean;
    }

}
