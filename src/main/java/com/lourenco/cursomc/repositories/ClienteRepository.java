package com.lourenco.cursomc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.lourenco.cursomc.domain.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

	/**
	 * Ao nomear o método iniciando com o padrão findBy o Spring Data
	 * automaticamente gera um método de busca para o campo correspondente a segunda
	 * parte do nome do método. Nesse caso, ele buscará no atribudo email.
	 * 
	 * Ao anotar o método com @Transactional(readOnly=true) diz-se que ela não
	 * necessita ser envolvida como uma transação de banco de dados. Isso faz a
	 * busca ser mais rápida e diminui o locking no gerenciamento de transações do
	 * banco de dados.
	 * 
	 * @param email Email que será pesquisado
	 */
	@Transactional(readOnly = true)
	Cliente findByEmail(String email);
}
