package com.kamleads.management.util; // You can put this in a temporary util package or test package

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// @Component // Uncomment this if you want it to run on application startup
public class PasswordHashGenerator implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String plainPassword = "password123"; // The password you want to use
        String hashedPassword = encoder.encode(plainPassword);
        System.out.println("Plain Password: " + plainPassword);
        System.out.println("Hashed Password: " + hashedPassword);
        // You can generate hashes for other passwords as needed
        // System.out.println("Hashed Password for 'anotherpass': " + encoder.encode("anotherpass"));
    }
}
    