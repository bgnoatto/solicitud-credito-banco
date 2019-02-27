package com.carsa.credito.banco.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * Created by esteban on 15/09/16.
 */
@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity runTimeException(RuntimeException rex){
        LOGGER.error("Error: ", rex);
        LOGGER.info("Something wrong happened check the printed stacktrace");
        return ResponseEntity.status(500).build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity accessDeniedException(AccessDeniedException rex){
        LOGGER.error("Error: ", rex);
        LOGGER.info("The user that request this service is not allowed to use it");
        return ResponseEntity.status(403).build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity methodNotAllowedException(HttpRequestMethodNotSupportedException rex){
        LOGGER.error("Error: ", rex);
        LOGGER.info("The Http Method called is not available to use");
        return ResponseEntity.status(405).build();
    }



}
