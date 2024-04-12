package edu.tinkoff.controller;

import edu.tinkoff.dto.TransferRequest;
import edu.tinkoff.service.TransferService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transfers")
public class TransfersController {
    private final TransferService transferService;

    public TransfersController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public void transfer(@RequestBody TransferRequest request) {
        transferService.transfer(request);
    }
}
