package com.carsa.credito.banco.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.carsa.credito.banco.domain.CreditoBanco;
import com.carsa.credito.banco.domain.CreditoBanco.Estado;

public interface CreditoBancoRepository extends PagingAndSortingRepository<CreditoBanco,Long>{

	 @Query("select c from CreditoBanco c "
	 		+ "where (:codigo is null or c.codigoOperacion like :codigo) "
	 		+ "and (:dni is null or c.dniCliente = :dni) "
	 		+ "and (:sexo is null or c.sexoCliente = :sexo) "
	 		+ "and (:monto is null or c.monto = :monto) "
	 		+ "and (:nombre is null or UPPER(c.nombreCliente) like :nombre) "
	 		+ "and (:local is null or c.codigoLocal = :local) "
	 		+ "and (:legajo is null or c.legajoCajero = :legajo) "
	 		+ "and (:fechaIni is null or c.fecha BETWEEN :fechaIni AND :fechaFin) "
	 		//+ "and (:fechaIni is null or c.fecha >= :fechaIni AND c.fecha <=:fechaFin) "
	 		+ "and (:estado is null or c.estado = :estado) "
	 		+ "and (c.estado != 'CANCELADO') "
	 		+ "order by c.fecha desc ")
	 
	Page<CreditoBanco> findFilterAndPages(
			@Param("codigo") String codigo, 
			@Param("dni") String dni, 
			@Param("nombre") String nombre,
			@Param("sexo") String sexo,
			@Param("monto") Double monto,
			@Param("local") String local,
			@Param("legajo")  Integer legajo,
			@Param("fechaIni") Date fechaIni,
			@Param("fechaFin") Date fechaFin,
			@Param("estado") Estado estado,
			Pageable page);

	List<CreditoBanco> findAllByCodigoOperacion(String codigoCredito);
	 }
