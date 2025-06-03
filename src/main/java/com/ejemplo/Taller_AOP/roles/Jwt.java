package com.ejemplo.Taller_AOP.roles;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class Jwt {

    // Clave secreta para firmar el token (en producción ponla en properties/env)
    private static final Key SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    // Validez 24h
    private static final long EXP_MS = 24 * 60 * 60 * 1000;

    public static String generateToken(Long userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXP_MS))
                .signWith(SECRET)
                .compact();
    }

    public static Long getUserId(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(c.getSubject());
    }

    public static String getRole(String token) {
        Claims c = Jwts.parserBuilder()
                .setSigningKey(SECRET)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return c.get("role", String.class);
    }
}
