package com.lourenco.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lourenco.cursomc.domain.Categoria;
import com.lourenco.cursomc.repositories.CategoriaRepository;
import com.lourenco.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	public Categoria buscar(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		// ao se setar ID com NULL se garante que será criado um novo objeto. Caso
		// contrário, se houver um ID especificado será atualizado o objeto
		// correspondente.
		obj.setId(null);

		return repo.save(obj);
	}
}
