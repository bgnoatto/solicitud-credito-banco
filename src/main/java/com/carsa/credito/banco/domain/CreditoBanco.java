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

@Entity
@Table(name="CREDITO_BANCO_NACION")
@Cacheable(value = true)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class CreditoBanco  {
	
	public enum Estado {
		PENDIENTE,CONSUMIDO,FACTURADO,CANCELADO,ENVIADO_RIESGO,RECIBIDO_RIESGO,ENVIADO_BANCO,RECHAZADO,
		PARA_RECTIFICAR,REENVIADO,CONCILIADO,ANULADO,INICIAL,PAGADO;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID_CREDITO_BANCO_NACION")
	private Long id;
	
	@Column(name="CODIGO_OPERACION")
	private String codigoOperacion;
	@Column(name="DNI_CLIENTE")
	private String dniCliente;	
	@Column(name="NOMBRE_CLIENTE")
	private String nombreCliente;
	@Column(name="SEXO_CLIENTE")
	private String sexoCliente;
	@Column(name="MONTO")
	private Double monto;
	@Column(name="CODIGO_LOCAL")
	private String codigoLocal;
	@Column(name="LEGAJO_CAJERO")
	private Integer legajoCajero;
	@Column(name="OBSERVACIONES")
	private String observaciones;
	@Column(name="FECHA_HORA")
	//@Temporal(TemporalType.DATE)
	private Date fecha;
	
	@Enumerated(EnumType.STRING)
	private Estado estado;
	
	@Enumerated(EnumType.STRING)
	private transient Estado estadoAnt;
	
	@Column(name="CANTIDAD_CUOTAS")
	private Integer cuotas;
	@Column(name="VERSION")
	private Integer version=0;
	@Column(name="SECOND_LEVEL_VERSION")
	private Integer secondLevelVersion=0;
	@Column(name="PUNTO_DE_VENTA")
	private Integer puntoVenta;
	@Column(name="NUMERO_CORRELATIVO")
	private Long numeroCorrelativo;
	@Column(name="TIPO_COMPROBANTE")
	private Integer condicionVenta;
	
	public String getCodigoOperacion() {
		return codigoOperacion;
	}

	public void setCodigoOperacion(String codigoOperacion) {
		this.codigoOperacion = codigoOperacion;
	}

	public String getDniCliente() {
		return dniCliente;
	}

	public void setDniCliente(String dniCliente) {
		this.dniCliente = dniCliente;
	}

	public String getSexoCliente() {
		return sexoCliente;
	}

	public void setSexoCliente(String sexoCliente) {
		this.sexoCliente = sexoCliente;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public String getCodigoLocal() {
		return codigoLocal;
	}

	public void setCodigoLocal(String codigoLocal) {
		this.codigoLocal = codigoLocal;
	}

	public Integer getLegajoCajero() {
		return legajoCajero;
	}

	public void setLegajoCajero(Integer legajoCajero) {
		this.legajoCajero = legajoCajero;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Estado getEstado() {
		return estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

	public Estado getEstadoAnt() {
		return estadoAnt;
	}

	public void setEstadoAnt(Estado estadoAnt) {
		this.estadoAnt = estadoAnt;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCuotas() {
		return cuotas;
	}

	public void setCuotas(Integer cuotas) {
		this.cuotas = cuotas;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getSecondLevelVersion() {
		return secondLevelVersion;
	}

	public void setSecondLevelVersion(Integer secondLevelVersion) {
		this.secondLevelVersion = secondLevelVersion;
	}

	public Integer getPuntoVenta() {
		return puntoVenta;
	}

	public void setPuntoVenta(Integer puntoVenta) {
		this.puntoVenta = puntoVenta;
	}

	public Long getNumeroCorrelativo() {
		return numeroCorrelativo;
	}

	public void setNumeroCorrelativo(Long numeroCorrelativo) {
		this.numeroCorrelativo = numeroCorrelativo;
	}

	public Integer getCondicionVenta() {
		return condicionVenta;
	}

	public void setCondicionVenta(Integer condicionVenta) {
		this.condicionVenta = condicionVenta;
	}
}
