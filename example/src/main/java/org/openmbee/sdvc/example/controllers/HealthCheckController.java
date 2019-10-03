package org.openmbee.sdvc.example.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @PostMapping(value = "/healthcheck")
    public ResponseEntity<?> index() {
        return ResponseEntity.ok("healthy");
    }
}
