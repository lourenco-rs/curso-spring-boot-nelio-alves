package com.lourenco.cursomc.resources.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lourenco.cursomc.services.exceptions.ObjectNotFoundException;

/**
 * Handler para para manipulação de erros
 *
 */
@ControllerAdvice
public class ResourceExceptionHandler {

	/**
	 * Esse método tratatá as exceções desse tipo. A anotação é que define isso.
	 * 
	 * A assinatura desse método é padrão.
	 * 
	 * @ExceptionHandler Define que o método será um tratador de exceções do tipo do
	 *                   parâmetro da anotação
	 */
	@ExceptionHandler(ObjectNotFoundException.class)
	public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException e, HttpServletRequest request) {
		StandardError err = new StandardError(HttpStatus.NOT_FOUND.value(), e.getMessage(), System.currentTimeMillis());

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
	}
}
