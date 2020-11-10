package org.openmbee.mms.example.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Monitor")
public class HealthCheckController {
    @GetMapping(value = "/healthcheck")
    public String healthcheck() {
        return "healthy";
    }
}
