package com.charityconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrganizationController {
    @GetMapping("/organization/dashboard")
    public String dashboard() {
        return "organization/dashboard";
    }
}