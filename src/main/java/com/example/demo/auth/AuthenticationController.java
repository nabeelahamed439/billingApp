package com.example.demo.auth;


import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateException;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;


    @PostMapping("/register")
    public AuthenticationResponse register(@RequestBody RegisterRequest registerRequest) throws BadRequestException, CertificateException {
        return authenticationService.register(registerRequest);
    }
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) throws CertificateException {
        return authenticationService.login(loginRequest);
    }


}
