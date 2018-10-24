package com.lourenco.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lourenco.cursomc.domain.Cliente;
import com.lourenco.cursomc.domain.ItemPedido;
import com.lourenco.cursomc.domain.PagamentoComBoleto;
import com.lourenco.cursomc.domain.Pedido;
import com.lourenco.cursomc.domain.enums.EstadoPagamento;
import com.lourenco.cursomc.repositories.ItemPedidoRepository;
import com.lourenco.cursomc.repositories.PagamentoRepository;
import com.lourenco.cursomc.repositories.PedidoRepository;
import com.lourenco.cursomc.security.UserSS;
import com.lourenco.cursomc.services.exceptions.AuthorizationException;
import com.lourenco.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;

	@Autowired
	private BoletoService boletoService;

	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Autowired
	private ClienteService clienteService;

	/*
	 * para o ambiente de teste será instanciado um MockEmailService, configurado em
	 * TestConfig num método anotado com @Bean. Para o ambiente DEV, será
	 * instanciado um SmtpEmailService.
	 * 
	 * public EmailService emailService() {
	 */
	@Autowired
	private EmailService emailService;

	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj
				.orElseThrow(() -> new ObjectNotFoundException(
						"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}

	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());

		/*
		 * No arbumento a fererência ao cliente vem apenas com o id. Para gravar no
		 * banco isso já é suficiente, mas para poder pegar o nome do cliente e mostrar
		 * é necessário setar o cliente no Pedido, como segue:
		 */
		obj.setCliente(clienteService.find(obj.getCliente().getId()));

		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);

		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());

		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);

			/*
			 * A manobra de setar o produto no itemPedido é apenas para poder pegar os dados
			 * de cada produto para mostrar. Para gravar no banco isso não é necessário.
			 * Bastaria a linha abaixo para setar o preço.
			 * 
			 * ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
			 */
			ip.setProduto(produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());

			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());

		emailService.sendOrderConfirmationEmail(obj);

		return obj;
	}

	public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		UserSS user = UserService.authenticated();
		if (user == null) {
			throw new AuthorizationException("Acesso negado");
		}
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		Cliente cliente = clienteService.find(user.getId());
		return repo.findByCliente(cliente, pageRequest);
	}
}
