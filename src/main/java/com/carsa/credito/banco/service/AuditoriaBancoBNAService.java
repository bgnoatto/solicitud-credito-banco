package com.carsa.credito.banco.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carsa.credito.banco.domain.AuditoriaEstadoBNA;
import com.carsa.credito.banco.repository.AuditoriaBancoBNARepository;

@Service
public class AuditoriaBancoBNAService {
	
	private Logger logger = Logger.getLogger(CreditoBancoService.class);
	
	@Autowired
	AuditoriaBancoBNARepository auditoriaBancoBNARepository;
	
	public AuditoriaEstadoBNA findCreditosBanco(Long id) {
		return auditoriaBancoBNARepository.findOne(id);
	}

	public AuditoriaEstadoBNA save(AuditoriaEstadoBNA auditoriaEstado) {
		return auditoriaBancoBNARepository.save(auditoriaEstado);
	}
	
	public void delete(AuditoriaEstadoBNA auditoriaEstado) {
		this.auditoriaBancoBNARepository.delete(auditoriaEstado);
	}

}
