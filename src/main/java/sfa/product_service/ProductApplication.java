package sfa.product_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;
import sfa.auth.AuthUtils.JwtHelper;


@SpringBootApplication(scanBasePackages = {"sfa.product_service", "sfa.auth"})
@EnableJpaRepositories(basePackages = {"sfa.auth.repo","sfa.product_service.repo"})
@EntityScan(basePackages = {"sfa.auth.entity", "sfa.product_service.entity"})
@ComponentScan
public class ProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApplication.class, args);
	}

	@Bean
	public JwtHelper jwtHelper() {
		return new JwtHelper();
	}
}