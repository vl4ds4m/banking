package edu.vl4ds4m.banking.transfer;

import edu.vl4ds4m.banking.transaction.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(TransferController.PATH)
public class TransferController {
    static final String PATH = "/transfers";

    private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public TransactionResponse transfer(@RequestBody TransferRequest request) {
        logger.debug("Accept POST {}: {}", PATH, request);
        return transferService.transfer(request);
    }
}
