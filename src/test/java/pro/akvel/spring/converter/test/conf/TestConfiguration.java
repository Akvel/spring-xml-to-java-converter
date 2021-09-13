package pro.akvel.spring.converter.test.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {

    @Bean
    public String testJavaBean(){
        return "Test";
    }

}
