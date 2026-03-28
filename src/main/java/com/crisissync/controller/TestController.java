package com.crisissync.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller to verify role-based access control.
 * Each endpoint restricts access to specific roles using @PreAuthorize.
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/staff")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    public String staffAccess() {
        return "Role access works: STAFF or MANAGER can access this endpoint.";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('MANAGER')")
    public String managerAccess() {
        return "Role access works: MANAGER can access this endpoint.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Role access works: ADMIN can access this endpoint.";
    }

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public String publicAccess() {
        return "Role access works: Anyone can access this endpoint.";
    }
}
