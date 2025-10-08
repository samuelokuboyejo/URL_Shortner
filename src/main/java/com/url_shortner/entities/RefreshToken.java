package com.url_shortner.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID tokenId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Instant expirationTime;

    @OneToOne(mappedBy = "refreshToken")
    @JsonBackReference
    @ToString.Exclude
    private User user;

}