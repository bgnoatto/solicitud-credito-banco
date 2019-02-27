package com.carsa.credito.banco.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.carsa.credito.banco.domain.AuditoriaEstadoBNA;

public interface AuditoriaBancoBNARepository extends PagingAndSortingRepository<AuditoriaEstadoBNA, Long> {

}
