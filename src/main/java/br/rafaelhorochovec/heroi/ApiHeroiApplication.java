package br.rafaelhorochovec.heroi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiHeroiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiHeroiApplication.class, args);
	}

}
