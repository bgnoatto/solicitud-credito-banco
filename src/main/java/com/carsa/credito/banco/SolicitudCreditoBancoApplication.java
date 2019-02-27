package com.carsa.credito.banco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import com.carsa.credito.banco.config.OAuth2ResourceServerConfig;
import com.carsa.credito.banco.domain.CreditoBanco;



@SpringBootApplication
public class SolicitudCreditoBancoApplication  {

	public static void main(String[] args) {
		SpringApplication.run(SolicitudCreditoBancoApplication.class, args);
	}
	
}
