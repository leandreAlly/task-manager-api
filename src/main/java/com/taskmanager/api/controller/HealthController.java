package com.taskmanager.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    // US-7: Health check endpoint
    @GetMapping
    public ResponseEntity<Map<String, String>> health() {
        log.info("GET /health - health check requested");
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
