package edu.tinkoff.controller;

import edu.tinkoff.model.TransferMessage;
import edu.tinkoff.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transfers")
public class TransfersController {
    private final TransferService transferService;

    public TransfersController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<?> transfer(@RequestBody TransferMessage message) {
        return transferService.transfer(message) ?
                ResponseEntity.ok().build() :
                ResponseEntity.badRequest().build();
    }
}
