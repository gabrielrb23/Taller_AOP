package com.ejemplo.notasapp.controlador;

import com.ejemplo.notasapp.extra.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String,String> creds) {
        Long userId = Long.valueOf(creds.get("userId"));
        String role   = creds.get("role");
        String token  = Jwt.generateToken(userId, role);
        return Map.of("token", token);
    }
}
