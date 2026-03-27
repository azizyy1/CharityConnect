package com.charityconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/user/dashboard")
    public String dashboard() {
        return "user/dashboard";
    }
}