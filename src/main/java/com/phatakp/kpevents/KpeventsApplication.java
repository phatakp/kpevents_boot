package com.phatakp.kpevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KpeventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(KpeventsApplication.class, args);
	}

}
