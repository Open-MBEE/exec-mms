package org.openmbee.sdvc.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping(value = "/healthcheck")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("healthy");
    }
}
