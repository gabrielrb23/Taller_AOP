package com.ejemplo.Taller_AOP.aspectos;


import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.ejemplo.Taller_AOP.roles.Jwt;

import org.springframework.web.context.request.*;

@Aspect
@Component
public class AspectoNota {
    private static final Logger log = LoggerFactory.getLogger(AspectoNota.class);

    @Around("execution(* com.ejemplo.Taller_AOP.controlador.NotaController.*(..))")
    public Object checkNotaSecurity(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }

        String token = auth.substring(7);
        String role   = Jwt.getRole(token);
        String method = pjp.getSignature().getName();

        if ("ALUMNO".equals(role)) {
            // Solo permite los métodos 'promedio' y 'listarNotas'
            if (!"promedio".equals(method) && !"listar".equals(method)) {
                log.warn("ALUMNO intentando metodo prohibido en Notas: {}", method);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }
        }

        return pjp.proceed();
    }
}

