package org.openmbee.sdvc.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping(value = "/healthcheck")
    public String healthcheck() {
        return "healthy";
    }
}
