package com.makethediference.mtdapi;

import com.makethediference.mtdapi.domain.entity.Admin;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
@EnableCaching
public class MtdApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtdApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
                Admin defaultAdmin = new Admin();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
                defaultAdmin.setRole(Role.ADMIN);
                defaultAdmin.setName("Admin");
                defaultAdmin.setSurname("Mostacero Cieza");
                defaultAdmin.setDni("00000000");
                defaultAdmin.setEmail("admin@admin.com");
                defaultAdmin.setAge(20);
                defaultAdmin.setPhoneNumber("0123456789");
                defaultAdmin.setCountry("Peru");
                defaultAdmin.setRegion("La Libertad");
                defaultAdmin.setMotivation("Luisda Luisda Luisda Luisda Luisda Luisda");
                defaultAdmin.setEnabled(true);

                userRepository.save(defaultAdmin);
            }
        };
    }
}
