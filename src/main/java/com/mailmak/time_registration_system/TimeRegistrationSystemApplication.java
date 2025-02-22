package com.mailmak.time_registration_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.mailmak.time_registration_system.repository")
public class TimeRegistrationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimeRegistrationSystemApplication.class, args);
	}

}


