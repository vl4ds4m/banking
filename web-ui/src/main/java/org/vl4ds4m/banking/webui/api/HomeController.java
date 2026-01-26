package org.vl4ds4m.banking.webui.api;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.vl4ds4m.banking.common.security.SecurityRole;

import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage() {
        String path;
        if (isUserAdmin()) {
            path = "/customers";
        } else {
            path = "/customers/info";
        }
        return "redirect:" + path;
    }

    private static boolean isUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Optional.ofNullable(authentication)
                .map(auth -> auth.getAuthorities())
                .orElse(List.of())
                .stream()
                .map(a -> a.getAuthority())
                .anyMatch(a -> SecurityRole.ADMIN.toAuthority().equals(a));
    }

}
