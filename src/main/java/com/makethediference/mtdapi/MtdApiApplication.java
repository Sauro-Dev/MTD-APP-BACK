package com.makethediference.mtdapi;

import com.makethediference.mtdapi.domain.entity.Admin;
import com.makethediference.mtdapi.domain.entity.EstimatedHours;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.infra.repository.UserRepository;
import com.makethediference.mtdapi.service.cloudflare.d1.D1Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

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
                Admin defaultAdmin = Admin.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .name("Luis")
                        .paternalSurname("Mostacero")
                        .maternalSurname("Cieza")
                        .birthdate(LocalDate.of(1980, 1, 1))
                        .dni("00000000")
                        .email("admin@admin.com")
                        .phoneNumber("123456789")
                        .codeNumber("+51")
                        .country("Peru")
                        .region("La Libertad")
                        .motivation("Luisda Luisda Luisda Luisda Luisda Luisda")
                        .estimatedHours(EstimatedHours.PLUS_TEN)
                        .enabled(true)
                        .firstLogin(true)
                        .build();

                userRepository.save(defaultAdmin);
            }
        };
    }
    @Bean
    public CommandLineRunner initD1Database(D1Service d1Service, PasswordEncoder passwordEncoder) {
        return args -> {
            // SQL para crear la tabla de usuarios si no existe
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                    "userId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT, " +
                    "name TEXT, " +
                    "paternalSurname TEXT, " +
                    "maternalSurname TEXT, " +
                    "dni TEXT UNIQUE, " +
                    "email TEXT UNIQUE, " +
                    "age INTEGER, " +
                    "birthdate TEXT, " +
                    "phoneNumber TEXT UNIQUE, " +
                    "codeNumber TEXT, " +
                    "country TEXT, " +
                    "region TEXT, " +
                    "motivation TEXT, " +
                    "estimatedHours TEXT, " +
                    "enabled BOOLEAN, " +
                    "firstLogin BOOLEAN" +
                    ")";

            // Ejecutar el comando SQL para crear la tabla
            d1Service.executeQuery(createTableSQL, List.of());

            // Insertar el usuario admin si no existe
            boolean adminExists = !d1Service.queryForList("SELECT 1 FROM users WHERE email = ?", Boolean.class, List.of("admin@admin.com")).isEmpty();
            if (!adminExists) {
                String insertAdminSQL = "INSERT INTO users (username, password, role, name, paternalSurname, maternalSurname, dni, email, phoneNumber, codeNumber, country, region, motivation, estimatedHours, enabled, firstLogin) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                d1Service.executeQuery(insertAdminSQL, List.of(
                        "admin",
                        passwordEncoder.encode("admin123"),
                        "ADMIN",
                        "Luis",
                        "Mostacero",
                        "Cieza",
                        "00000000",
                        "admin@admin.com",
                        "123456789",
                        "+51",
                        "Peru",
                        "La Libertad",
                        "Luisda Luisda Luisda Luisda Luisda Luisda",
                        "PLUS_TEN",
                        true,
                        true
                ));
            }
        };
    }
}
