package com.url_shortner.auth.services;

import com.url_shortner.auth.utils.RefreshTokenResponse;
import com.url_shortner.entities.RefreshToken;
import com.url_shortner.entities.User;
import com.url_shortner.repositories.RefreshTokenRepository;
import com.url_shortner.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    private static final long REFRESH_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        if (user.getRefreshToken() != null) {
            refreshTokenRepository.delete(user.getRefreshToken());
        }

        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(UUID.randomUUID().toString())
                .expirationTime(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY))
                .build();

        refreshToken.setUser(user);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return refreshToken;
    }


    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refToken);
            throw new RuntimeException("Refresh Token expired");
        }

        return refToken;
    }

    public RefreshTokenResponse refreshAccessToken(String requestRefreshToken) {
        RefreshToken refToken = verifyRefreshToken(requestRefreshToken);
        User user = refToken.getUser();

        refreshTokenRepository.delete(refToken);

        RefreshToken newRefreshToken = createRefreshToken(user.getEmail());

        String newAccessToken = jwtService.generateToken(user);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken.getRefreshToken());

    }

}

