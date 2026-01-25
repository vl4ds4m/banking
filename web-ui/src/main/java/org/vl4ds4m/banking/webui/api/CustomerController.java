package org.vl4ds4m.banking.webui.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vl4ds4m.banking.accounts.openapi.client.model.Account;
import org.vl4ds4m.banking.accounts.openapi.client.model.Customer;
import org.vl4ds4m.banking.accounts.openapi.client.model.CustomerInfo;
import org.vl4ds4m.banking.accounts.openapi.client.model.CustomerNames;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.webui.service.CustomerService;
import org.vl4ds4m.banking.webui.service.QueryExceptionHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.vl4ds4m.banking.webui.api.ControllersAdvice.userLogin;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @ModelAttribute
    public Customer customer(Customer customer, String login) {
        customer.setLogin(login);
        return customer;
    }

    @GetMapping("/info")
    public String info(Model model) {
        String login = userLogin(model);
        CustomerInfo info = customerService.getCustomer(login);
        model.addAttribute("customer", info.getCustomer());
        model.addAttribute("accounts", info.getAccounts());
        model.addAttribute("currencies", remainingCurrencies(info.getAccounts()));
        return "user/info";
    }

    @GetMapping
    public String customers(Model model) {
        List<CustomerNames> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "customers";
    }

    @GetMapping("/{login}/info")
    public String customerInfo(@PathVariable String login, Model model) {
        CustomerInfo info = customerService.getCustomer(login);
        model.addAttribute("customer", info.getCustomer());
        model.addAttribute("accounts", info.getAccounts());
        model.addAttribute("currencies", remainingCurrencies(info.getAccounts()));
        return "customer-info";
    }

    @GetMapping("/new")
    public String newCustomer() {
        return "registration";
    }

    @PostMapping("/new")
    public String newCustomer(
            Customer customer,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        try {
            customerService.createCustomer(customer);
        } catch (RestClientResponseException e) {
            String msg = QueryExceptionHandler.handle(e);
            ControllersAdvice.setProblemAttr(model, msg);
            return "registration";
        }

        redirectAttrs.addAttribute("login", customer.getLogin());
        return "redirect:/customers/{login}/info";
    }

    private static List<Currency> remainingCurrencies(List<Account> accounts) {
        Set<Currency> remaining = new HashSet<>(
                List.of(Currency.values()));
        for (var a : accounts) {
            remaining.remove(a.getCurrency());
        }
        return List.copyOf(remaining);
    }
}
