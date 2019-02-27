package com.carsa.credito.banco.domain;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.carsa.credito.banco.domain.CreditoBanco.Estado;

@Entity
@Table(name="AUDITORIA_ESTADO_BNA")
@Cacheable(value = true)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class AuditoriaEstadoBNA {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_AUDITORIA_ESTADO_BNA")
	private Long id;
	
	@Column(name="ID_CREDITO_BANCO_NACION")
	private Long idCreditoBanco;
	
	@Column(name="LEGAJO")
	private Integer legajo;
	
	@Enumerated(EnumType.STRING)
	private Estado estadoInicial;
	
	@Enumerated(EnumType.STRING)
	private Estado estadoFinal;
	
	@Column(name="FECHA_HORA")
	private Date fecha;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdCreditoBanco() {
		return idCreditoBanco;
	}

	public void setIdCreditoBanco(Long idCreditoBanco) {
		this.idCreditoBanco = idCreditoBanco;
	}

	public Integer getLegajo() {
		return legajo;
	}

	public void setLegajo(Integer legajo) {
		this.legajo = legajo;
	}

	public Estado getEstadoInicial() {
		return estadoInicial;
	}

	public void setEstadoInicial(Estado estadoInicial) {
		this.estadoInicial = estadoInicial;
	}

	public Estado getEstadoFinal() {
		return estadoFinal;
	}

	public void setEstadoFinal(Estado estadoFinal) {
		this.estadoFinal = estadoFinal;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
