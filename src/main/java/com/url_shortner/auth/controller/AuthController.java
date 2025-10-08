package com.url_shortner.auth.controller;

import com.url_shortner.auth.services.AuthService;
import com.url_shortner.auth.services.RefreshTokenService;
import com.url_shortner.auth.utils.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns authentication tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(
            summary = "Login user",
            description = "Authenticates user and returns JWT tokens"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Takes a valid refresh token and issues a new access token and refresh token pair."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully refreshed access token",
            content = @Content(
                    schema = @Schema(implementation = RefreshTokenResponse.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Invalid or expired refresh token",
            content = @Content
    )
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The refresh token request containing the refresh token string",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshTokenRequest.class)
                    )
            )
            @RequestBody RefreshTokenRequest request
    ) {
        RefreshTokenResponse tokenPair = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenPair);
    }
}
