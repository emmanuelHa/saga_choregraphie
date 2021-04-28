package com.soat.formation.saga.clientui;

//import com.soat.formation.saga.order.config.DatabaseConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages={"com.soat.formation.saga"})
@EnableConfigurationProperties//(DatabaseConfiguration.class)
//@EnableJpaRepositories(value = {"com.soat.formation.saga.payment.infra.dao"})
@EntityScan("com.soat.formation.saga.payment.domain.model")
/*@ComponentScan({"com.soat.formation.saga.payment.application",
    "com.soat.formation.saga.payment.domain", "com.soat.formation.saga.payment.infra"})*/
public class ClientUIApp {


    public static void main(String[] args) {
        /*ApplicationContext ctx = SpringApplication.run(ClientUIApp.class, args);
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/
        SpringApplication.run(ClientUIApp.class, args);
    }

}
