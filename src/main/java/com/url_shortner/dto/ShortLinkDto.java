package com.url_shortner.dto;

import com.url_shortner.entities.ShortLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkDto {

    private UUID id;
    private String shortCode;
    private String originalUrl;
    private String shortenedUrl;
    private Integer clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expirationTime;
    private boolean isPrivate;

    public static ShortLinkDto from(ShortLink shortLink, String baseUrl) {
        return ShortLinkDto.builder()
                .id(shortLink.getId())
                .shortCode(shortLink.getShortCode())
                .originalUrl(shortLink.getOriginalUrl())
                .clickCount(shortLink.getClickCount())
                .createdAt(shortLink.getCreatedAt())
                .expirationTime(shortLink.getExpirationTime())
                .isPrivate(shortLink.isPrivate())
                .shortenedUrl(baseUrl + shortLink.getShortCode())
                .build();
    }
}
