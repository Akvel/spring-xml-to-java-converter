
package pro.akvel.spring.converter.generator;

import java.util.ArrayList;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly1;
import pro.akvel.spring.converter.testbean.BeanWithIdOnly2;


/**
 * Generated Java based configuration
 * 
 */
@Configuration
public class BeanWithConstructorListArg {


    @Bean("BeanWithConstructorListArg")
    public pro.akvel.spring.converter.testbean.BeanWithConstructorListArg BeanWithConstructorListArg(
        @Qualifier("bean1")
        BeanWithIdOnly1 bean1,
        @Qualifier("bean2")
        BeanWithIdOnly2 bean2) {
        ArrayList list0 = new ArrayList();
        list0 .add(bean1);
        list0 .add(bean2);
        list0 .add(bean2);
        HashSet set1 = new HashSet();
        set1 .add(bean1);
        set1 .add(bean2);
        pro.akvel.spring.converter.testbean.BeanWithConstructorListArg bean = new pro.akvel.spring.converter.testbean.BeanWithConstructorListArg(list0, set1);
        return bean;
    }

}
