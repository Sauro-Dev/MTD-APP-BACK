package com.makethediference.mtdapi;

import com.makethediference.mtdapi.domain.entity.Admin;
import com.makethediference.mtdapi.domain.entity.EstimatedHours;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

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
            boolean existsByEmail = userRepository.findByEmail("admin@admin.com").isPresent();
            boolean existsByUsername = userRepository.findByUsername("admin").isPresent();
            boolean existsByPhoneNumber = userRepository.findByPhoneNumber("123456789").isPresent();

            if (!existsByEmail && !existsByUsername && !existsByPhoneNumber) {
                Admin defaultAdmin = new Admin();
                defaultAdmin.setUsername("admin");
                defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
                defaultAdmin.setRole(Role.ADMIN);
                defaultAdmin.setName("Luis");
                defaultAdmin.setPaternalSurname("Mostacero");
                defaultAdmin.setMaternalSurname("Cieza");
                LocalDate birth = LocalDate.of(1980, 1, 1);
                defaultAdmin.setBirthdate(birth);
                defaultAdmin.setDni("00000000");
                defaultAdmin.setEmail("admin@admin.com");
                defaultAdmin.setPhoneNumber("123456789");
                defaultAdmin.setCodeNumber("+51");
                defaultAdmin.setCountry("Peru");
                defaultAdmin.setRegion("La Libertad");
                defaultAdmin.setMotivation("Luisda Luisda Luisda Luisda Luisda Luisda");
                defaultAdmin.setEstimatedHours(EstimatedHours.PLUS_TEN);
                defaultAdmin.setEnabled(true);
                defaultAdmin.setFirstLogin(true);

                userRepository.save(defaultAdmin);
            }
        };
    }
}
