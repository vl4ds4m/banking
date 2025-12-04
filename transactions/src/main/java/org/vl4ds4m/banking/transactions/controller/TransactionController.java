package org.vl4ds4m.banking.transactions.controller;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vl4ds4m.banking.transactions.service.TransactionService;

@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/reread")
    public void reread(
            @RequestParam
            Integer partition,

            @RequestParam
            Long offset,

            @RequestParam(required = false)
            @Nullable
            Integer count
    ) {
        transactionService.rereadAndSave(partition, offset, count == null ? 1 : count);
    }

}
