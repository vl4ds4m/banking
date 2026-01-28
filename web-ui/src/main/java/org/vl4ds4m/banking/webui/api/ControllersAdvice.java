package org.vl4ds4m.banking.webui.api;

import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.common.openapi.model.ErrorMessageResponse;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class ControllersAdvice extends ResponseEntityExceptionHandler {

    private static final String USER_ATTR = "user";

    public static String userLogin(Model model) {
        Object user = model.getAttribute(USER_ATTR);
        if (user instanceof String login) return login;
        throw new IllegalStateException("User login attribute is not set");
    }

    public static String getRefererPathRedirect(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        String path;
        if (referer == null) {
            throw new RuntimeException("Default referer not implemented");
        } else {
            path = UriComponentsBuilder.fromUriString(referer)
                    .build()
                    .getPath();
        }
        return "redirect:" + path;
    }

    @ModelAttribute
    public void authenticatedUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = Optional.ofNullable(auth)
                .map(a -> a instanceof OAuth2AuthenticationToken t ? t : null)
                .map(a -> a.getPrincipal())
                .map(p -> p.getAttribute("preferred_username"))
                .map(n -> n instanceof String s ? s : null)
                .orElse("ANONYMOUS");
        model.addAttribute(USER_ATTR, username);
    }

    @ModelAttribute
    public void currencyListModel(Model model) {
        model.addAttribute("currencies", List.of(Currency.values()));
    }

    @ModelAttribute
    public void successFlag(@Nullable Boolean success, Model model) {
        if (success == Boolean.TRUE) {
            setSuccessAttr(model);
        }
    }

    @ModelAttribute
    public void problemMessage(@Nullable String problem, Model model) {
        if (problem != null) {
            setProblemAttr(model, problem);
        }
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(RestClientResponseException e, WebRequest request) {
        String message = getErrorResponseMessage(e);
        HttpStatusCode status = e.getStatusCode();
        Object body = createProblemDetail(e, status, message, null, null, request);
        return createResponseEntity(body, new HttpHeaders(), status, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleException(RestClientException e, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Object body = createProblemDetail(e, status, e.getMessage(), null, null, request);
        return createResponseEntity(body, new HttpHeaders(), status, request);
    }

    public static void setSuccessAttr(Model model) {
        model.addAttribute("success", true);
    }

    public static void setProblemAttr(Model model, String message) {
        model.addAttribute("problem", message);
    }

    private static String getErrorResponseMessage(RestClientResponseException e) {
        HttpStatusCode status = e.getStatusCode();
        if (status.is4xxClientError()) {
            ErrorMessageResponse body = e.getResponseBodyAs(ErrorMessageResponse.class);
            if (body != null) {
                return body.getMessage();
            }
            return e.getResponseBodyAsString();
        }
        return e.getMessage();
    }
}
