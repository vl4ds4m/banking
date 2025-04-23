package edu.vl4ds4m.banking.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/config")
    public void updateConfig(@RequestBody Map<String, String> request) {
        logger.debug("Accept a request to update configuration");
        adminService.updateConfig(request);
    }
}
