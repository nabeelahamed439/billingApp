package com.example.demo.auth;

import com.example.demo.config.JwtService;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.demo.exception.BadRequestException;

import java.security.cert.CertificateException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;  

    @Transactional(rollbackOn = Exception.class)
    public AuthenticationResponse register(RegisterRequest registerRequest) throws BadRequestException {
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new BadRequestException("User Already Registered","User Already Registered");
        }
        User user = userTransform(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setLoginCount(0);
        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }


    @Transactional(rollbackOn = Exception.class)
    public AuthenticationResponse login(LoginRequest loginRequest) throws CertificateException {
        //it checks the email and password matches .
        //Password from request and encrypted password from db is checked with the help of passwordEncoder set in authentication provided
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new BadRequestException("User Not Found","User Not Found"));
        user.setLoginCount(user.getLoginCount() + 1);
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public User userTransform(RegisterRequest registerRequest){
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setCreatedBy(TokenData.getUserId());
        user.setCreatorRole(TokenData.getUserRole());
        user.setRole(User.Role.valueOf(registerRequest.getUserType().toUpperCase()));
        return user;
    }

}
