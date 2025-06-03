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

    @Around("execution(* com.ejemplo.notasapp.controlador.NotaController.*(..))")
    public Object checkNotaSecurity(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }

        String token = auth.substring(7);
        Long userId = Jwt.getUserId(token);
        String role   = Jwt.getRole(token);
        String method = pjp.getSignature().getName();

        if ("ALUMNO".equals(role)) {
            // Solo permite el método 'promedio' y solo sobre su propio ID
            if (!"promedio".equals(method)) {
                log.warn("ALUMNO intentando método prohibido en Notas: {}", method);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }
            // Verifica que el primer argumento (studentId) coincida con su propio userId
            Object[] args = pjp.getArgs();
            if (args.length == 0 || !(args[0] instanceof Long) || !userId.equals((Long) args[0])) {
                log.warn("ALUMNO {} intentando ver promedio de otro: {}", userId, args.length>0? args[0] : null);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sólo puedes ver tu promedio");
            }
        }

        // PROFESOR o cualquier otro rol válido: deja proceder
        return pjp.proceed();
    }
}

