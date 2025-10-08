package com.url_shortner.repositories;

import com.url_shortner.entities.ShortLink;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ShortLinkRepository extends JpaRepository<ShortLink, UUID> {

    Optional<ShortLink> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    Page<ShortLink> findByCreatedById(UUID userId, Pageable pageable);

    Page<ShortLink> findByIsPrivateFalse(Pageable pageable);

    int deleteByIdInAndCreatedBy_Id(Set<UUID> ids, UUID userId);
}
