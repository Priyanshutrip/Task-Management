package com.taskmanager.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.auth_service.dto.AuthResponse;
import com.taskmanager.auth_service.dto.LoginRequest;
import com.taskmanager.auth_service.dto.RegisterRequest;
import com.taskmanager.auth_service.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(
			@Validated @RequestBody RegisterRequest request){
		
		authService.register(request);
		return ResponseEntity.ok(
				new AuthResponse(null, "User registered successfully")
				);
	}
	
	@PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Validated @RequestBody LoginRequest request) {

        String token = authService.login(request);
        return ResponseEntity.ok(
                new AuthResponse(token, "Login successful")
        );
    }
}
