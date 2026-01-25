package org.vl4ds4m.banking.webui.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.vl4ds4m.banking.accounts.openapi.client.model.AccountInfo;
import org.vl4ds4m.banking.common.openapi.model.Currency;
import org.vl4ds4m.banking.webui.service.AccountService;
import org.vl4ds4m.banking.webui.service.QueryExceptionHandler;
import org.vl4ds4m.banking.webui.service.TransferService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final AccountService accountService;

    private final TransferService transferService;

    @GetMapping("/{number}")
    public String userTransferPage(@PathVariable Long number, Model model) {
        AccountInfo info = accountService.getAccountInfo(number);
        model.addAttribute("accountNumber", number)
                .addAttribute("accountInfo", info);
        return "user/transfer";
    }

    @PostMapping("/{number}")
    public String userTransferOp(
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
            return "user/transfer";
        }

        ControllersAdvice.setSuccessAttr(redirectAttrs);
        return "redirect:/transfer/{number}";
    }

    @GetMapping
    public String transferPage() {
        return "transfer-common";
    }

    @PostMapping
    public String transferOp(
            @RequestParam String senderLogin,
            @RequestParam String receiverLogin,
            @RequestParam Currency senderCurrency,
            @RequestParam Currency receiverCurrency,
            @RequestParam BigDecimal amount,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        try {
            transferService.transfer(senderLogin, senderCurrency, receiverLogin, receiverCurrency, amount);
        } catch (RestClientResponseException e) {
            String msg = QueryExceptionHandler.handle(e);
            ControllersAdvice.setProblemAttr(model, msg);
            model.addAttribute("senderLogin", senderLogin)
                 .addAttribute("receiverLogin", receiverLogin)
                 .addAttribute("senderCurrency", senderCurrency)
                 .addAttribute("receiverCurrency", receiverCurrency)
                 .addAttribute("amount", amount);
            return "transfer-common";
        }

        ControllersAdvice.setSuccessAttr(redirectAttrs);
        return "redirect:/transfer";
    }
}
