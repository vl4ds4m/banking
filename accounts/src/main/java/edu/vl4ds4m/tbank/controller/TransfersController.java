package edu.vl4ds4m.tbank.controller;

import edu.vl4ds4m.tbank.dto.TransactionResponse;
import edu.vl4ds4m.tbank.dto.TransferRequest;
import edu.vl4ds4m.tbank.service.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
public class TransfersController {
    private static final Logger logger = LoggerFactory.getLogger(TransfersController.class);

    private final TransferService transferService;

    public TransfersController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransactionResponse transfer(@RequestBody TransferRequest request) {
        logger.info("Accept a request to transfer currency");
        return transferService.transfer(request);
    }
}
