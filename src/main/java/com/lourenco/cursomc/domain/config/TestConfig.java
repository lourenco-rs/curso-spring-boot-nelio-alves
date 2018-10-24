package com.lourenco.cursomc.domain.config;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.lourenco.cursomc.services.DBService;
import com.lourenco.cursomc.services.EmailService;
import com.lourenco.cursomc.services.MockEmailService;

@Configuration
@Profile("test")
public class TestConfig {

	@Autowired
	private DBService dbService;
	
	@Bean
	public boolean instantiateDatabase() throws ParseException {
		dbService.instantiateTestDatabase();
		return true;
	}
	
	/*
	 * Injetado na classe PedidoService
	 */
	@Bean
	public EmailService emailService() {
		return new MockEmailService();
	}
}