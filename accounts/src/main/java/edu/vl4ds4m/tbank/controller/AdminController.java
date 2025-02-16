package edu.vl4ds4m.tbank.controller;

import edu.vl4ds4m.tbank.dto.UpdateConfigsRequest;
import edu.vl4ds4m.tbank.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/configs")
    public void updateConfigs(@RequestBody UpdateConfigsRequest request) {
        logger.info("Accept a request to update configuration");
        adminService.updateConfigs(request);
    }
}
