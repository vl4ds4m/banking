package edu.tinkoff.controller;

import edu.tinkoff.dto.UpdateConfigsRequest;
import edu.tinkoff.service.AdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/configs")
    public void updateConfigs(@RequestBody UpdateConfigsRequest request) {
        adminService.updateConfigs(request);
    }
}
