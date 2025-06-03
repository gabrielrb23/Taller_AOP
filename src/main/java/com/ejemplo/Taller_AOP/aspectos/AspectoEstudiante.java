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
public class AspectoEstudiante {
    private static final Logger log = LoggerFactory.getLogger(AspectoEstudiante.class);

    @Around("execution(* com.ejemplo.notasapp.controlador.EstudianteController.*(..))")
    public Object checkEstudianteSecurity(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        String token = auth.substring(7);
        String role  = Jwt.getRole(token);

        if ("ALUMNO".equals(role)) {
            // Ni una sola operación en EstudianteController queda permitida
            String method = pjp.getSignature().getName();
            log.warn("ALUMNO intentando {} en EstudianteController", method);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permiso para acceder a estudiantes");
        }

        // PROFESOR u otro rol: deja proceder
        return pjp.proceed();
    }
}

