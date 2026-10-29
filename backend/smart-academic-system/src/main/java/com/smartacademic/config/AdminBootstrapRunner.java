
package com.smartacademic.config;

import com.smartacademic.entity.RoleEntity;
import com.smartacademic.entity.UserEntity;
import com.smartacademic.repository.RoleRepository;
import com.smartacademic.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Locale;

@Component
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.username:}")
    private String adminUsername;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    public AdminBootstrapRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // Do not create a second Admin if one already exists.
        if (userRepository.existsByRoleName("ADMIN")) {
            log.info("An Admin account already exists. Bootstrap skipped.");
            return;
        }

        // Never create an account with missing credentials.
        if (adminUsername == null || adminUsername.isBlank()
                || adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {

            log.warn(
                "Initial Admin not created. Set BOOTSTRAP_ADMIN_USERNAME, "
                + "BOOTSTRAP_ADMIN_EMAIL and BOOTSTRAP_ADMIN_PASSWORD."
            );
            return;
        }

        if (adminPassword.length() < 12) {
            throw new IllegalStateException(
                "The initial Admin password must contain at least 12 characters."
            );
        }

        String username = adminUsername.trim();
        String email = adminEmail.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalStateException(
                "The configured Admin username already belongs to another account."
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException(
                "The configured Admin email already belongs to another account."
            );
        }

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException(
                    "ADMIN role is missing. Check the database seed migration."
                ));

        UserEntity admin = new UserEntity();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setEnabled(true);

        // Require a password change at first login.
        admin.setMustChangePassword(false);

        admin.setRoles(new HashSet<>());
        admin.getRoles().add(adminRole);

        userRepository.save(admin);

        log.info("Initial Admin account created successfully.");
        log.info("Admin username: {}", username);
        log.info("The password was stored as a BCrypt hash.");
    }
}
