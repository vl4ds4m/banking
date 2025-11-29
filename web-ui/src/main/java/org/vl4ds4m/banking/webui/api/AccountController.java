package org.vl4ds4m.banking.webui.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vl4ds4m.banking.accounts.openapi.client.model.AccountInfo;
import org.vl4ds4m.banking.accounts.openapi.client.model.CreateAccountRequest;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.webui.service.AccountService;
import org.vl4ds4m.banking.webui.service.QueryExceptionHandler;
import org.vl4ds4m.banking.webui.service.TransferService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    private final TransferService transferService;

    @PostMapping("/new")
    public String newAccount(
            @RequestParam String login,
            @RequestParam String currency,
            RedirectAttributes redirectAttrs
    ) {
        var redirect = "redirect:/customers/{login}/info";
        redirectAttrs.addAttribute("login", login);

        if (currency.isBlank()) {
            ControllersAdvice.setProblemAttr(redirectAttrs, "Необходимо выбрать валюту");
            return redirect;
        }

        CreateAccountRequest request = new CreateAccountRequest();
        request.setCustomerLogin(login);
        request.setCurrency(Currency.fromValue(currency));
        accountService.createAccount(request);

        return redirect;
    }

    @GetMapping("/{number}/top-up")
    public String accountTopUpPage(@PathVariable Long number, Model model) {
        AccountInfo info = accountService.getAccountInfo(number);
        model.addAttribute("accountNumber", number)
             .addAttribute("accountInfo", info);
        return "top-up";
    }

    @PostMapping("/{number}/top-up")
    public String accountTopUpOp(@PathVariable Long number, @RequestParam BigDecimal augend) {
        accountService.topUpAccount(number, augend);
        return "redirect:/accounts/{number}/top-up";
    }

    @GetMapping("/{number}/withdraw")
    public String accountWithdrawPage(@PathVariable Long number, Model model) {
        AccountInfo info = accountService.getAccountInfo(number);
        model.addAttribute("accountNumber", number)
             .addAttribute("accountInfo", info);
        return "withdraw";
    }

    @PostMapping("/{number}/withdraw")
    public String accountWithdrawOp(@PathVariable Long number, @RequestParam BigDecimal subtrahend) {
        accountService.withdrawAccount(number, subtrahend);
        return "redirect:/accounts/{number}/withdraw";
    }

    @GetMapping("/{number}/transfer")
    public String accountTransferPage(@PathVariable Long number, Model model) {
        AccountInfo info = accountService.getAccountInfo(number);
        model.addAttribute("accountNumber", number)
             .addAttribute("accountInfo", info);
        return "transfer-personal";
    }

    @PostMapping("/{number}/transfer")
    public String accountTransferOp(
            @PathVariable Long number,
            @RequestParam String receiverLogin,
            @RequestParam Currency receiverCurrency,
            @RequestParam BigDecimal transferAmount,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        try {
            transferService.transfer(number, receiverLogin, receiverCurrency, transferAmount);
        } catch (RestClientResponseException e) {
            String msg = QueryExceptionHandler.handle(e);
            ControllersAdvice.setProblemAttr(model, msg);
            AccountInfo info = accountService.getAccountInfo(number);
            model.addAttribute("accountNumber", number)
                 .addAttribute("accountInfo", info);
            model.addAttribute("receiverLogin", receiverLogin)
                 .addAttribute("receiverCurrency", receiverCurrency)
                 .addAttribute("transferAmount", transferAmount);
            return "transfer-personal";
        }

        ControllersAdvice.setSuccessAttr(redirectAttrs);
        return "redirect:/accounts/{number}/transfer";
    }
}
