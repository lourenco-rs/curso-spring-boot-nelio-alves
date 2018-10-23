package com.lourenco.cursomc.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.lourenco.cursomc.security.UserSS;

public class UserService {

	public static UserSS authenticated() {
		try {
			return (UserSS) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			/**
			 * O caso mais clássico de erro seria o caso de não haver um usuário logado no
			 * sistema. Nesse caso, ocorreria um erro no cast para UserSS.
			 */
			return null;
		}
	}
}
