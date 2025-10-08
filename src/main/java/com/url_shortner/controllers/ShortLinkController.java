package com.url_shortner.controllers;
import com.url_shortner.auth.services.JwtService;
import com.url_shortner.dto.CreateShortLinkRequest;
import com.url_shortner.dto.PageResult;
import com.url_shortner.dto.ShortLinkDto;
import com.url_shortner.entities.UserInfoUserDetails;
import com.url_shortner.services.ShortLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequestMapping("/url-shortner")
@Tag(name = "Short Link Management", description = "Endpoints for creating, retrieving, and redirecting short links")
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;
    private final JwtService jwtService;

    @Operation(
            summary = "Create a short link",
            description = "Generates a short URL for a given target URL. The authenticated user's email is associated with the created link.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Short link successfully created",
                            content = @Content(schema = @Schema(implementation = ShortLinkDto.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
            }
    )
    @PostMapping
    public ResponseEntity<ShortLinkDto> createShortLink(
            @RequestBody CreateShortLinkRequest request,
            @AuthenticationPrincipal UserInfoUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        ShortLinkDto shortLink = shortLinkService.generateShortLink(request, email);
        return ResponseEntity.ok(shortLink);
    }

    @Operation(
            summary = "Redirect to original URL",
            description = "Redirects users to the original long URL based on the provided short code.",
            responses = {
                    @ApiResponse(responseCode = "302", description = "Redirect successful"),
                    @ApiResponse(responseCode = "404", description = "Short code not found")
            }
    )
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirectToOriginal(
            @PathVariable String shortCode
    ) {
        String targetUrl = shortLinkService.resolveRedirectUrl(shortCode);

        if (targetUrl == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(targetUrl))
                .build();
    }


    @Operation(
            summary = "Get logged-in user's links",
            description = "Retrieves all short links created by the authenticated user, paginated by default.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user's links",
                            content = @Content(schema = @Schema(implementation = PageResult.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not authenticated")
            }
    )
    @GetMapping("/users/me")
    public ResponseEntity<PageResult<ShortLinkDto>> getUserLinks(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getUsername();
        return ResponseEntity.ok(shortLinkService.getMyUrls(email, page, size));
    }


    @Operation(
            summary = "Get public links",
            description = "Fetches a paginated list of public short links created by any user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved public links",
                            content = @Content(schema = @Schema(implementation = PageResult.class)))
            }
    )
    @GetMapping("/public")
    public ResponseEntity<PageResult<ShortLinkDto>> getPublicLinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(shortLinkService.getPublicUrls(page, size));
    }

}
