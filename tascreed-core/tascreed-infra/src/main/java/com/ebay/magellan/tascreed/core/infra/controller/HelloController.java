package com.ebay.magellan.tascreed.core.infra.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @Value("${tumbler.version:unknown}")
    private String tumblerVersion;

    @GetMapping("/hello")
    public String hello() {
        return String.format("Hello Tumbler %s !", tumblerVersion);
    }
}
