package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @GetMapping("/info")
    public String info(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        model.addAttribute("name", oauth2User.getAttributes().get("name"));
        return "info";
    }

    @GetMapping("/oauth_login")
    public String getLoginPage(Model model) {
        Map<String, String> urls = new HashMap<>();
        ((Iterable<ClientRegistration>) clientRegistrationRepository).forEach(registration ->
                urls.put(registration.getClientName(),
                        "oauth2/authorization/" + registration.getRegistrationId()));
        model.addAttribute("urls", urls);
        return "oauth_login";
    }
}
