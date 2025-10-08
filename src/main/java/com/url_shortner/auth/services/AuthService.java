package com.url_shortner.auth.services;

import com.url_shortner.auth.utils.AuthResponse;
import com.url_shortner.auth.utils.LoginRequest;
import com.url_shortner.auth.utils.LoginResponse;
import com.url_shortner.auth.utils.RegisterRequest;
import com.url_shortner.entities.User;
import com.url_shortner.enums.UserRole;
import com.url_shortner.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;



    public AuthResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public LoginResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setLastLogin(LocalDateTime.now());
        var accessToken = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public String extractEmailFromToken(String token) {
        return jwtService.extractUsername(token);
    }


}
