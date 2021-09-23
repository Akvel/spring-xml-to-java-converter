
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
public class BeanWithPropertyList {


    @Bean("BeanWithPropertyList")
    public pro.akvel.spring.converter.testbean.BeanWithPropertyList BeanWithPropertyList(
            @Qualifier("bean1")
                    BeanWithIdOnly1 bean1,
            @Qualifier("bean2")
                    BeanWithIdOnly2 bean2) {
        pro.akvel.spring.converter.testbean.BeanWithPropertyList bean = new pro.akvel.spring.converter.testbean.BeanWithPropertyList();
        ArrayList list0 = new ArrayList();
        list0 .add(bean1);
        list0 .add(bean2);
        bean.setProp1(list0);
        ArrayList list1 = new ArrayList();
        list1 .add(bean1);
        bean.setProp2(list1);
        HashSet set2 = new HashSet();
        set2 .add(bean1);
        bean.setProp3(set2);
        HashSet set3 = new HashSet();
        set3 .add(bean1);
        bean.setProp4(set3);
        return bean;
    }

}
