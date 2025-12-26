package com.anuragNepal.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.anuragNepal.ecommerce.entity.User;
import com.anuragNepal.ecommerce.repository.UserRepository;

import java.util.Properties;

@Configuration
public class DevConfig {

    @Autowired
    private Environment env;

    @Bean
    public JavaMailSender javaMailSender() {
        // If mail properties are not set, return a harmless JavaMailSenderImpl with no credentials.
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        String host = env.getProperty("spring.mail.host", "");
        if (host.isEmpty()) {
            // Leave host blank — the application code should handle send failure or skip sending in dev.
            mailSender.setHost("");
            mailSender.setPort(25);
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.debug", "false");
            return mailSender;
        }
        // Otherwise, Spring Boot will normally auto-configure based on properties; set host for completeness
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(env.getProperty("spring.mail.port", "25")));
        mailSender.setUsername(env.getProperty("spring.mail.username", ""));
        mailSender.setPassword(env.getProperty("spring.mail.password", ""));
        return mailSender;
    }

    @Bean
    CommandLineRunner ensureAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                User admin = userRepository.findByEmail("admin@local.dev");
                if (admin == null) {
                    User u = new User();
                    u.setName("Administrator");
                    u.setEmail("admin@local.dev");
                    u.setPassword(passwordEncoder.encode("Admin123"));
                    u.setRole("ROLE_ADMIN");
                    u.setIsEnable(true);
                    u.setAccountStatusNonLocked(true);
                    u.setAccountfailedAttemptCount(0);
                    userRepository.save(u);
                    System.out.println("Created default admin user: admin@local.dev / Admin123");
                }
            } catch (Exception ex) {
                System.out.println("DevConfig runner error: " + ex.getMessage());
            }
        };
    }
}
