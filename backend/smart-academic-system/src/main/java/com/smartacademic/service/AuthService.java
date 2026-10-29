
package com.smartacademic.service;

import com.smartacademic.dto.LoginRequest;
import com.smartacademic.dto.LoginResponse;
import com.smartacademic.entity.UserEntity;
import com.smartacademic.repository.UserRepository;
import com.smartacademic.security.JwtService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import com.smartacademic.dto.ChangePasswordRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final long expirationMs;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            @Value("${app.jwt.expiration-ms}") long expirationMs) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.expirationMs = expirationMs;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String identifier = request.username().trim();

        UserEntity user = userRepository
                .findByUsernameIgnoreCaseOrEmailIgnoreCase(
                        identifier, identifier)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid username/email or password"));

        if (!user.isEnabled()
                || !passwordEncoder.matches(
                        request.password(), user.getPasswordHash())) {

            throw new BadCredentialsException(
                    "Invalid username/email or password");
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        String token = jwtService.generateToken(user.getUsername());

        return new LoginResponse(
                token,
                "Bearer",
                expirationMs,
                user.getUsername(),
                roles,
                user.isMustChangePassword());
    }

    
@Transactional
public void changePassword(
        String authenticatedUsername,
        ChangePasswordRequest request) {

    UserEntity user = userRepository.findByUsername(authenticatedUsername)
            .orElseThrow(() ->
                    new UsernameNotFoundException("User account not found"));

    if (!passwordEncoder.matches(
            request.currentPassword(), user.getPasswordHash())) {
        throw new BadCredentialsException("Current password is incorrect");
    }

    if (passwordEncoder.matches(
            request.newPassword(), user.getPasswordHash())) {
        throw new IllegalArgumentException(
                "New password must be different from the current password");
    }

    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    user.setMustChangePassword(false);

    userRepository.save(user);
}

}
