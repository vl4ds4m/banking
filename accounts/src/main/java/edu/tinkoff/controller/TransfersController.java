package edu.tinkoff.controller;

import edu.tinkoff.dto.TransactionResponse;
import edu.tinkoff.dto.TransferRequest;
import edu.tinkoff.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transfers")
public class TransfersController {
    private static final Logger log = LoggerFactory.getLogger(TransfersController.class);

    private final TransferService transferService;

    public TransfersController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransactionResponse transfer(@RequestBody TransferRequest request) {
        log.info("Accept a request to transfer currency");
        return transferService.transfer(request);
    }
}
