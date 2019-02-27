package com.carsa.credito.banco.web.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carsa.credito.banco.domain.AuditoriaEstadoBNA;
import com.carsa.credito.banco.domain.CarsaUserDetails;
import com.carsa.credito.banco.domain.CreditoBanco;
import com.carsa.credito.banco.domain.CreditoBanco.Estado;
import com.carsa.credito.banco.domain.Rol;
import com.carsa.credito.banco.service.AuditoriaBancoBNAService;
import com.carsa.credito.banco.service.CreditoBancoService;
import com.carsa.credito.banco.service.UsersService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/creditobanco")
public class CreditoBancoControllerImpl  {
	
	private Logger logger = Logger.getLogger(CreditoBancoControllerImpl.class);

	@Autowired
	CreditoBancoService creditoBancoService;
	
	@Autowired
	AuditoriaBancoBNAService auditoriaBancoBNAService;
	
	@Autowired
	UsersService userService;
	
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/solicitudes", method = RequestMethod.GET)
	public Page<CreditoBanco> getCreditos(  
			OAuth2Authentication user,
            @RequestHeader(value = "Authorization") String auth,
			@RequestParam(value="codigoOperacion", required=false) String codigo,
			@RequestParam(value="dniCliente", required=false) String dni,
			@RequestParam(value="sexoCliente", required=false) String sexo,
			@RequestParam(value="nombreCliente", required=false) String nombre,
			@RequestParam(value="monto", required=false) Double monto,
			@RequestParam(value="codigoLocal", required=false) String local,
			@RequestParam(value="legajoCajero", required=false) Integer legajo,
			@RequestParam(value="fecha", required=false) @DateTimeFormat(pattern="dd-MM-yyyy") Date fecha,
			@RequestParam(value="estado", required=false) Estado estado,
	 Pageable pageable,Sort sort) throws Exception 
	{
		//Date fechaActual = new Date();
		Date fechaIni = null;
		Date fechaFin = null;
		if(fecha!=null){
			fechaIni = DateUtils.addMilliseconds(DateUtils.addDays(fecha, 0), 1);
			fechaFin = DateUtils.addMilliseconds(DateUtils.addDays(fecha, 1), (-60)*1000);
		}
		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
		//GrantedAuthority[] autorizaciones = user.getAuthorities().toArray(new GrantedAuthority[]{});
		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		
		Integer legajoId = -1;
		try{
			legajoId = Integer.parseInt(principal.getInitials());
		}
		catch(NumberFormatException e){
			legajoId = -1;
		}
		if(usuarioJerarquico(user)){
			storeId = local;
			legajoId = legajo;
		}
		return creditoBancoService.findCreditosBanco(codigo,dni,sexo,nombre,monto,storeId,legajoId,fechaIni,fechaFin,estado,pageable);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/solicitud/{id}", method = RequestMethod.GET)
    public CreditoBanco getCredito(@PathVariable Long id){
		return creditoBancoService.findCreditosBanco(id);
	}
	
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/solicitud", method = RequestMethod.POST)
    public CreditoBanco saveCredito(
    		OAuth2Authentication user,
            @RequestHeader(value = "Authorization") String auth,
            @RequestBody CreditoBanco credito) throws Exception{
		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		String code = this.userService.getCode(auth).orElseThrow(RuntimeException::new);
		boolean primero = false;
		if(storeId==null || (storeId!=null && !storeId.equals(credito.getCodigoLocal()))){
			throw new AccessDeniedException("El codigo del local debe ser igual al del usuario");
		}
		credito.setCodigoLocal(storeId);
		credito.setLegajoCajero(Integer.parseInt(code));
		credito.setCodigoOperacion(credito.getCodigoOperacion().toUpperCase());
		Double montoCredito = credito.getMonto();
		if((montoCredito < 5000) || (montoCredito > 80000)){
			throw new Exception("El monto debe estar entre 5000 y 80000. "
					+ "Cod: " + credito.getCodigoOperacion() + " ID: " + credito.getId());
		}
		if(credito.getId() == null){
			primero = true;
		}
		credito = creditoBancoService.save(credito);
		if(primero){
			registrarCambioEstado(credito,Estado.INICIAL,Estado.PENDIENTE);
		}
			
		return credito;
	}
	
	@SuppressWarnings("unused")
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/cambiarEstado", method = RequestMethod.POST)
    public CreditoBanco setCambiarEstado(OAuth2Authentication user,
            @RequestHeader(value = "Authorization") String auth,
            @RequestBody CreditoBanco credito) throws Exception{
		CreditoBanco resultado = null;
		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		String code = this.userService.getCode(auth).orElseThrow(RuntimeException::new);
		if(storeId==null || (storeId!=null && !storeId.equals(credito.getCodigoLocal()))){
			throw new AccessDeniedException("El codigo del local debe ser igual al del usuario");
		}
		Estado estAnterior = credito.getEstadoAnt();
		
		credito.setCodigoLocal(storeId);
		credito.setLegajoCajero(Integer.parseInt(code));
		Estado nuevoEstado = credito.getEstado();
		
		if(rolesPermitidos(nuevoEstado,Rol.ROLE_BNA_USER) 
				&& isRoleUserValid(user,Rol.ROLE_BNA_USER)){ //Cajero
			credito = creditoBancoService.cambiarProximoEstado(credito);
			resultado = saveCredito(user, auth, credito);
			registrarCambioEstado(credito,estAnterior,credito.getEstado());
			
		}
		else if(rolesPermitidos(nuevoEstado,Rol.ROLE_BNA_CONTROLLER)){ //Riesgo
			if(isRoleUserValid(user, Rol.ROLE_BNA_CONTROLLER)){
				credito = creditoBancoService.cambiarProximoEstado(credito);
				resultado = saveCredito(user, auth, credito);
				registrarCambioEstado(credito,estAnterior,credito.getEstado());
			}
		}
		
		return resultado;
	}
	
	@SuppressWarnings("unused")
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER')")
	@RequestMapping(value = "/borrar", method = RequestMethod.POST)
    public CreditoBanco borrarCredito(
    		OAuth2Authentication user,
            @RequestHeader(value = "Authorization") String auth,
            @RequestBody CreditoBanco credito) throws Exception{
		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		String code = this.userService.getCode(auth).orElseThrow(RuntimeException::new);
		if(storeId==null || (storeId!=null && !storeId.equals(credito.getCodigoLocal()))){
			throw new AccessDeniedException("El codigo del local debe ser igual al del usuario");
		}
		credito.setCodigoLocal(storeId);
		credito.setLegajoCajero(Integer.parseInt(code));
		if(credito.getEstado() == Estado.PENDIENTE){
			credito.setEstado(Estado.CANCELADO);
			credito = creditoBancoService.cambiarProximoEstado(credito);
			this.setCambiarEstado(user, auth, credito);
		}
		return credito;
	}
	
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/disponible/{codigoLocal}", method = RequestMethod.GET)
	public Float getDisponible(@PathVariable Integer codigoLocal) throws Exception {

//		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
//		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		Float disponible = null;
		try {
			disponible = creditoBancoService.calcularDisponible(codigoLocal);
		} catch (Exception e) {
			
			throw new Exception("Problemas al obtener el saldo disponible para el local: "+codigoLocal);
		}
		return disponible;
	}
	
	/**
	 * Verifica si ya existe un Credito Guardado con el Codigo de Operacion suministrado 
	 * @param codigoCredito
	 * @return true: Existe, false: No Existe
	 * @throws Exception
	 */
	@PreAuthorize("hasAnyRole('ROLE_BNA_USER','ROLE_BNA_CONTROLLER')")
	@RequestMapping(value = "/existeCredito/{codigoCredito}", method = RequestMethod.GET)
	public int existeCreditoCodigo(@PathVariable String codigoCredito) throws Exception {

//		CarsaUserDetails principal = this.userService.getUserDetails(user).orElseThrow(RuntimeException::new);
//		String storeId = this.userService.getStore(auth).orElseThrow(RuntimeException::new);
		int existe = 0;
		try {
			existe = creditoBancoService.existeCodigoCredito(codigoCredito.toUpperCase());
		} catch (Exception e) {
			
			throw new Exception("Problemas al obtener el saldo disponible para el local: "+codigoCredito);
		}
		return existe;
	}
	
	/* METODOS AUXILIARES */
	
	/**
	 * Verifica si el usuario tiene privilegios de Administrador
	 * @param user
	 * @return
	 */
	
	private boolean usuarioJerarquico(OAuth2Authentication user){
		return user.getAuthorities().contains(new SimpleGrantedAuthority(Rol.ROLE_BNA_CONTROLLER.toString()));
	}
	
	private boolean isRoleUserValid(OAuth2Authentication user,Rol rol){
		return user.getAuthorities().contains(new SimpleGrantedAuthority(rol.toString()));
	}
	
	private boolean rolesPermitidos(Estado estado, Rol rol){
		if(rol == Rol.ROLE_BNA_USER){ //Cajero
			if(estado == Estado.ENVIADO_RIESGO || estado == Estado.ANULADO
					|| (estado == Estado.CANCELADO))
				return true;
		}
		else if(rol == Rol.ROLE_BNA_CONTROLLER){ //Riesgo
			if((estado == Estado.ANULADO) || (estado == Estado.RECIBIDO_RIESGO) 
					|| (estado == Estado.ENVIADO_BANCO) || (estado == Estado.RECHAZADO)
					|| (estado == Estado.PARA_RECTIFICAR) || (estado == Estado.CONCILIADO)
					|| (estado == Estado.REENVIADO) || (estado == Estado.PAGADO)){
				return true;
			}
		}
		return false;
	}
	
	private Estado estadoAnterior(Estado estado){
		
		Estado resultado = estado;
		
		if(estado.equals(Estado.PENDIENTE) || estado.equals(Estado.CONSUMIDO)){
			resultado = Estado.PENDIENTE;
		}
		else if(estado.equals(Estado.FACTURADO)){
			resultado = Estado.CONSUMIDO;
		}
		else if(estado.equals(Estado.ENVIADO_RIESGO)){
			resultado = Estado.FACTURADO;
		}
		else if(estado.equals(Estado.RECIBIDO_RIESGO)){
			resultado = Estado.ENVIADO_RIESGO;
		}
		else if(estado.equals(Estado.ENVIADO_BANCO)){
			resultado = Estado.RECIBIDO_RIESGO ;
		}
		else if(estado.equals(Estado.RECHAZADO) || estado.equals(Estado.PARA_RECTIFICAR) || estado.equals(Estado.CONCILIADO)){
			resultado = Estado.ENVIADO_BANCO;
		}
		else if(estado.equals(Estado.REENVIADO)){
			resultado = Estado.PARA_RECTIFICAR;
		}
		else if(estado.equals(Estado.PAGADO)){
			resultado = Estado.CONCILIADO;
		}
		else if(estado.equals(Estado.CANCELADO)){
			resultado = Estado.PENDIENTE;
		}
		return resultado;
	}
	
	private boolean registrarCambioEstado(CreditoBanco credito, Estado estadoInicial, Estado estadoFinal){
		
		AuditoriaEstadoBNA auditoriaEstadoBNA = new AuditoriaEstadoBNA();
		auditoriaEstadoBNA.setIdCreditoBanco(credito.getId());
		auditoriaEstadoBNA.setLegajo(credito.getLegajoCajero());
		auditoriaEstadoBNA.setEstadoInicial(estadoInicial);
		auditoriaEstadoBNA.setEstadoFinal(estadoFinal);
		auditoriaEstadoBNA.setFecha(new Date());
		
		if(auditoriaBancoBNAService.save(auditoriaEstadoBNA)!=null){
			return true;
		}
		return false;
	}
	
	private static boolean isParsable(String input){
	    boolean parsable = true;
	    try{
	        Integer.parseInt(input);
	    }catch(NumberFormatException e){
	        parsable = false;
	    }
	    return parsable;
	}
}
