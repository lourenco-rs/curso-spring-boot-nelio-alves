package com.lourenco.cursomc.domain.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.lourenco.cursomc.services.DBService;

@Configuration
@Profile("dev")
public class DevConfig {

	@Autowired
	private DBService dbService;

	/**
	 * Essa anotação busca no arquivo "application-dev.properties (por conta da
	 * anotação da classe @Profile("dev")) a o valor da chave especificada no como
	 * parâmetro.
	 */
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String strategy;

	@Bean
	public boolean instantiateDatabase() throws ParseException {

		/**
		 * Se o valor for "create" indica que o banco será criado. Nesse caso, será
		 * chamado o método que popula o banco. Caso contrário, possivelmente o banco já
		 * estará populado.
		 */
		if (!"create".equals(strategy)) {
			return false;
		}
		dbService.instantiateTestDatabase();
		return true;
	}
}
