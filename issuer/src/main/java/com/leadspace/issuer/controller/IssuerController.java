package com.leadspace.issuer.controller;

import com.leadspace.issuer.dto.CreateTokenRequest;
import com.leadspace.issuer.service.TokenIssuerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuerController {

    private final TokenIssuerService service;

    public IssuerController(TokenIssuerService service) {
        this.service = service;
    }

    @PostMapping("/token")
    public String generateToken(@RequestBody CreateTokenRequest request) {
        return service.publishToken(request);
    }
}
