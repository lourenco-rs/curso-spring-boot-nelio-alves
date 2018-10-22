package com.lourenco.cursomc.domain.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.lourenco.cursomc.security.JWTAuthenticationFilter;
import com.lourenco.cursomc.security.JWTUtil;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	// Aqui é injetada a interface. O Spring busca a implementação adequada.
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private Environment env;
	
	@Autowired
	private JWTUtil jwtUtil;

	private static final String[] PUBLIC_MATCHERS = { "/h2-console/**" };

	private static final String[] PUBLIC_MATCHERS_GET = { "/produtos/**", "/categorias/**", "/clientes/**" };

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		/**
		 * Condição específica por conta do uso do h2 no profile de "test". Para liberar
		 * o acesso ao console do h2 (após a conexão)...
		 */
		if (Arrays.asList(env.getActiveProfiles()).contains("test")) {
			http.headers().frameOptions().disable();
		}

		/**
		 * executa o método cors (que foi implementado abaixo) e também desabilita o
		 * esquema de segurança para aplicações que armazenam estado (nossa aplicação é
		 * stateless, por isso esse recurso é desnecessário)
		 */
		http.cors().and().csrf().disable();

		http
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, PUBLIC_MATCHERS_GET)
				.permitAll()
				.antMatchers(PUBLIC_MATCHERS)
				.permitAll()
				.anyRequest()
				.authenticated();
		
		http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));

		// Assegura que o backend não criará seção de usuário
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	/**
	 * Nesse método define-se quem é o UserDetailService e o algorítmo de
	 * criptografia de senha usado.
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
	}

	/**
	 * Aula 56 : 7'35
	 * 
	 * Por padrão, não são permitidas requisições cross-origin (multiplas fontes). A
	 * liberação de requisições por multiplas fontes deve ser feita explicitamente.
	 * [aula 56: 8'25]
	 * 
	 * Isso será necessário porque para o ambiente de desenvolvimento, onde estão
	 * sendo realizados testes por meio do Postman
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
		return source;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
