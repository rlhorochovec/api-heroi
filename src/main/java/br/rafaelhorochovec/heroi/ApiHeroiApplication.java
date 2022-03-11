package br.rafaelhorochovec.heroi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import br.rafaelhorochovec.heroi.property.FileStorageProperties;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties({ FileStorageProperties.class })
public class ApiHeroiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiHeroiApplication.class, args);
	}

}
