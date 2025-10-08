package com.url_shortner.services;

import com.url_shortner.dto.CreateShortLinkRequest;
import com.url_shortner.dto.PageResult;
import com.url_shortner.dto.ShortLinkDto;
import com.url_shortner.entities.ShortLink;
import com.url_shortner.entities.User;
import com.url_shortner.exceptions.ExpiredLinkException;
import com.url_shortner.exceptions.InvalidURLException;
import com.url_shortner.exceptions.ResourceNotFoundException;
import com.url_shortner.exceptions.UserNotFoundException;
import com.url_shortner.repositories.ShortLinkRepository;
import com.url_shortner.repositories.UserRepository;
import com.url_shortner.utils.UrlUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkService {
    private final ShortLinkRepository shortLinkRepository;
    private final UserRepository userRepository;

    @Value("${app.base-url}")
    private String baseUrl;


    @Transactional
        public ShortLinkDto generateShortLink(CreateShortLinkRequest request, String email) {

            if (!UrlUtils.isValid(request.getOriginalUrl())) {
                throw new InvalidURLException("Invalid URL provided: " + request.getOriginalUrl());
            }

        String shortCode = request.getCustomShortCode();
        if (email != null && shortCode != null) {
            if (shortLinkRepository.existsByShortCode(shortCode)) {
                throw new IllegalArgumentException("Custom short code already in use.");
            }
        } else {
            shortCode = UrlUtils.generateShortCode();
            while (shortLinkRepository.existsByShortCode(shortCode)) {
                shortCode = UrlUtils.generateShortCode();
            }
        }
        ShortLink shortLink = new ShortLink();
        shortLink.setShortCode(shortCode);
        shortLink.setOriginalUrl(request.getOriginalUrl());
        shortLink.setClickCount(0);
        shortLink.setExpirationTime(request.getTtlDays() != null
                ? LocalDateTime.now().plusDays(request.getTtlDays())
                : LocalDateTime.now().plusDays(7));


        if (email != null) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
            shortLink.setCreatedBy(user);
            shortLink.setPrivate(request.isPrivate());
        }

        shortLinkRepository.save(shortLink);
        return ShortLinkDto.from(shortLink, baseUrl);
    }

    @Transactional
    public Optional<ShortLinkDto> retrieveShortUrl(String shortCode, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email"));
        Optional<ShortLink> optionalShortUrl = shortLinkRepository.findByShortCode(shortCode);

        if (optionalShortUrl.isEmpty()) return Optional.empty();
        ShortLink shortLink = optionalShortUrl.get();

        if (shortLink.isExpired()) {
            log.info("Short URL expired: {}", shortCode);
            return Optional.empty();
        }

        if (shortLink.isPrivate() && !Objects.equals(shortLink.getCreatedBy().getId(), user.getId())) {
            return Optional.empty();
        }

        shortLink.incrementClickCount();
        shortLinkRepository.save(shortLink);

        return Optional.of(ShortLinkDto.from(shortLink, baseUrl));
    }

    @Transactional
    public String resolveRedirectUrl(String shortCode) {
        ShortLink shortLink = shortLinkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new ResourceNotFoundException("Short link not found"));

        if (shortLink.isExpired()) {
            throw new ExpiredLinkException("Short link has expired");
        }
        shortLink.incrementClickCount();
        shortLinkRepository.save(shortLink);

        return shortLink.getOriginalUrl();
    }


    @Transactional(readOnly = true)
    public PageResult<ShortLinkDto> getMyUrls(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = shortLinkRepository.findByCreatedById(user.getId(), pageable)
                .map(sl -> ShortLinkDto.from(sl, baseUrl));
        return PageResult.from(pageResult);
    }

    @Transactional
    public PageResult<ShortLinkDto> getPublicUrls(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var pageResult = shortLinkRepository.findByIsPrivateFalse(pageable)
                .map(sl -> ShortLinkDto.from(sl, baseUrl));
        return PageResult.from(pageResult);
    }

}
