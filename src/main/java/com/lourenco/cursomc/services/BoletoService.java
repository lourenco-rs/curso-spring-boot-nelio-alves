package com.lourenco.cursomc.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.lourenco.cursomc.domain.PagamentoComBoleto;

/**
 * Classe criada apenas para simulação de um serviço de boleto para criação de
 * uma data de vencimento (7 dias a partir da data do boleto).
 * 
 */
@Service
public class BoletoService {

	/*
	 * numa situação real seria necessário chamar um ws para gerar o boleto.
	 */
	public void preencherPagamentoComBoleto(PagamentoComBoleto pagto, Date instanteDoPedido) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(instanteDoPedido);
		cal.add(Calendar.DAY_OF_MONTH, 7);
		pagto.setDataPagamento(cal.getTime());
	}
}
