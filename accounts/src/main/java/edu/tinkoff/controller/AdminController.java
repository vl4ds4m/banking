package edu.tinkoff.controller;

import edu.tinkoff.dto.UpdateConfigsRequest;
import edu.tinkoff.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/configs")
    public void updateConfigs(@RequestBody UpdateConfigsRequest request) {
        log.info("Accept a request to update configuration");
        adminService.updateConfigs(request);
    }
}
