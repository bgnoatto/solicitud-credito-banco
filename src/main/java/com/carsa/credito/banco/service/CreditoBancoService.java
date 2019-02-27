package com.carsa.credito.banco.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.carsa.credito.banco.domain.CreditoBanco;
import com.carsa.credito.banco.domain.CreditoBanco.Estado;
import com.carsa.credito.banco.repository.CreditoBancoRepository;

@Service
public class CreditoBancoService {

	private Logger logger = Logger.getLogger(CreditoBancoService.class);
	
	@Autowired
	CreditoBancoRepository creditoBancoRepository;
	
	@Autowired 
	@Qualifier("jdbcTemplateMs")
    private JdbcTemplate jdbcTemplate;

	public Page<CreditoBanco> findCreditosBanco(String codigo, String dni, String sexo,String nombre, Double monto, String local,
			Integer legajo, Date fechaIni, Date fechaFin,Estado estado, Pageable page) {
		if(nombre!=null && !nombre.isEmpty()){
			nombre= "%"+nombre.toUpperCase() +"%";
		}
		if(codigo!=null && !codigo.isEmpty()){
			codigo= codigo.toUpperCase() +"%";
		}
		return creditoBancoRepository.findFilterAndPages(codigo,dni,nombre,sexo,monto,local,legajo,fechaIni,fechaFin,estado,page);
		//return creditoBancoRepository.findAll(page);
	}

	public CreditoBanco findCreditosBanco(Long id) {
		return creditoBancoRepository.findOne(id);
	}

	public CreditoBanco save(CreditoBanco credito) {
		return creditoBancoRepository.save(credito);
	}
	
	public void delete(CreditoBanco credito) {
		this.creditoBancoRepository.delete(credito);
	}

	/**
	 * Cambia al proximo estado correspondiente para el credito
	 * @param credito
	 * @return
	 */
	public CreditoBanco cambiarProximoEstado(CreditoBanco credito) {
		CreditoBanco resultado = new CreditoBanco();
		resultado = credito;
		Estado estado = credito.getEstado();
		Estado estadoViejo = creditoBancoRepository.findOne(credito.getId()).getEstado();
		
		if(estadoViejo.equals(Estado.PENDIENTE)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.CONSUMIDO)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.FACTURADO)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.ENVIADO_RIESGO)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.RECIBIDO_RIESGO)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.ENVIADO_BANCO)){
			resultado.setEstado(estado);
		}
		else if(estado.equals(Estado.PARA_RECTIFICAR)){
			resultado.setEstado(estado);
		}
		return resultado;
	}
	
	
	public Float calcularDisponible(Integer codigoLocal) throws Exception {
		
		Map<String, Object> row = null;
		try{
			String sql =
					"SET DATEFIRST 1 select "
					+ "(select LIMITE from LIMITE_CBN_SUCURSAL where CODIGO_LOCAL="+codigoLocal+") - isnull(sum(monto),0) as sumatoria "
					+ "from CREDITO_BANCO_NACION cbn "
					+ "where DATEPART(week,GETDATE()) = DATEPART(week,cbn.FECHA_HORA) "
					+ "and DATEPART(weekday,cbn.FECHA_HORA) <= DATEPART(weekday,GETDATE()) "
					+ "and cbn.CODIGO_LOCAL=" + codigoLocal
					+ " and cbn.estado != 'CANCELADO'";
			
			
			
			row = jdbcTemplate.queryForMap(sql);
		}catch(Exception e){
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("error", e.getCause().getMessage());
//			throw new Exception("Problemas al obtener el saldo disponible para el local: "+codigoLocal);
			throw new Exception();
		}
		Float resultado = 0.0f;
		try{
			resultado = Float.parseFloat(row.get("sumatoria").toString());
		}
		catch(Exception e){
			resultado = null;
		}
		
		if(resultado != null){
			return resultado;
		}
		return 0.0f;
	}
	
	/**
	 * Verifica si ya existe un Credito Guardado con el Codigo de Operacion suministrado 
	 * @param codigoCredito
	 * @return true: Existe, false: No Existe
	 * @throws Exception
	 */
	public int existeCodigoCredito(String codigoCredito) throws Exception {
		
		List<CreditoBanco> creditosEncontrados = this.creditoBancoRepository.findAllByCodigoOperacion(codigoCredito);
		int resultado = creditosEncontrados.size();
		if(resultado > 0){
			Iterator<CreditoBanco> it = creditosEncontrados.iterator();
			int encontreCancelado = 0;
			while(it.hasNext()){
				Estado aux = it.next().getEstado();
				if(aux == Estado.CANCELADO){
					encontreCancelado++;
				}
			}
			if(encontreCancelado == creditosEncontrados.size()){
				resultado = 0;
			}
		}
		return resultado;		
	}
}
