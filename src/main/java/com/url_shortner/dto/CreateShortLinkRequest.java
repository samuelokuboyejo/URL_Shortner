package com.url_shortner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShortLinkRequest {
    private String originalUrl;
    private String customShortCode;
    private Integer ttlDays;
    private boolean isPrivate;
}
