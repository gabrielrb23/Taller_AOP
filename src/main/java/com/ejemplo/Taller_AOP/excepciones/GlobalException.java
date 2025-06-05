package com.ejemplo.Taller_AOP.excepciones;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class GlobalException {
    private static final Logger log = LoggerFactory.getLogger(GlobalException.class);

    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object handleControllerExceptions(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        } catch (EmailDuplicadoException ex) {
            log.warn("Excepcion controlada en {}: {}", pjp.getSignature(), ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (ResponseStatusException ex) {
            log.error("Error en {}: {}", pjp.getSignature(), ex.getReason());
            throw ex;
        } catch (Exception ex) {
            log.error("Excepcion no controlada en {}: {}", pjp.getSignature(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrio un error interno");
        }
    }
}

