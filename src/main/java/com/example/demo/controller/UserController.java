package com.example.demo.controller;

import com.example.demo.auth.AuthenticationResponse;
import com.example.demo.auth.RegisterRequest;
import com.example.demo.auth.AuthenticationService;
import com.example.demo.config.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    
    private final AuthenticationService authenticationService;

    @PostMapping("/merchant")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public AuthenticationResponse createMerchant(@Valid @RequestBody RegisterRequest request) {
        request.setUserType(Constants.MERCHANT);
        return authenticationService.register(request);
    }

    @PostMapping("/cashier")
    @PreAuthorize("hasRole('MERCHANT')")
    public AuthenticationResponse createCashier(@Valid @RequestBody RegisterRequest request) {
        request.setUserType(Constants.CASHIER);
        return authenticationService.register(request);
    }


}
