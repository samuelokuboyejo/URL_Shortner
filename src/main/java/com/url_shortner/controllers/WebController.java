package com.url_shortner.controllers;

import com.url_shortner.auth.services.AuthService;
import com.url_shortner.auth.utils.RegisterRequest;
import com.url_shortner.dto.CreateShortLinkRequest;
import com.url_shortner.dto.ShortLinkDto;
import com.url_shortner.services.ShortLinkService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {
    private final AuthService authService;
    private final ShortLinkService shortLinkService;

    @GetMapping("login")
    public String showLoginForm() {
        return "auth/login";
    }

    @GetMapping("register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("register")
    public String register(@ModelAttribute RegisterRequest request, Model model) {
        try {
            authService.register(request);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "User already exists or invalid input");
            return "auth/register";
        }
    }

    @GetMapping("dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) return "redirect:/login";

        model.addAttribute("links", shortLinkService.getMyUrls(email, 0, 10).getContent());
        return "dashboard";
    }

    @GetMapping("links/create")
    public String showCreateLinkForm(Model model) {
        model.addAttribute("createShortLinkRequest", new CreateShortLinkRequest());
        return "links/create";
    }

    @PostMapping("links/create")
    public String createLink(@ModelAttribute CreateShortLinkRequest request,
                             HttpServletRequest servletRequest,
                             Model model) {
        String email = getEmailFromToken(servletRequest);
        if (email == null) return "redirect:/login";

        ShortLinkDto shortLink = shortLinkService.generateShortLink(request, email);
        model.addAttribute("shortLink", shortLink);
        return "links/success";
    }

    @GetMapping("links/public")
    public String viewPublicLinks(Model model) {
        model.addAttribute("publicLinks", shortLinkService.getPublicUrls(0, 10).getContent());
        return "links/public";
    }

    @GetMapping("/links/my")
    public List<ShortLinkDto> getMyLinks(HttpServletRequest request) {
        String email = getEmailFromToken(request);
        if (email == null) throw new RuntimeException("Unauthorized");
        return shortLinkService.getMyUrls(email, 0, 50).getContent();
    }

    private String getEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return authService.extractEmailFromToken(token);
        }
        return null;
    }
}
