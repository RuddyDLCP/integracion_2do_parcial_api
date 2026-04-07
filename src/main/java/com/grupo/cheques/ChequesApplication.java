package com.grupo.cheques;

import com.grupo.cheques.config.ContabilidadProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ContabilidadProperties.class)
public class ChequesApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChequesApplication.class, args);
	}

}
