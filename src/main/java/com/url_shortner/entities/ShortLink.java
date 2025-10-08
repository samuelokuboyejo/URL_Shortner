package com.url_shortner.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "short_links")
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLink {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String originalUrl;

    @Column(name = "click_count", nullable = false)
    private int clickCount=0;

    @Column(unique = true, nullable = false)
    private String shortCode;

    @Column(name="is_private", nullable = false)
    private boolean isPrivate = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User createdBy;

    private LocalDateTime expirationTime;

    public boolean isExpired() {
        return expirationTime != null && LocalDateTime.now().isAfter(expirationTime);
    }

    public void incrementClickCount() {
        this.clickCount = this.clickCount + 1;
    }
}
