package com.makethediference.mtdapi.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "email", "dni", "phoneNumber"})
})
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El rol no puede ser nulo")
    private Role role;

    @Column(nullable = false)
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "El apellido no puede estar vacío")
    private String surname;

    @Column(unique = true, nullable = false, length = 8)
    @Pattern(regexp = "^[0-9]{8}$", message = "El DNI debe contener exactamente 8 dígitos numéricos")
    @NotBlank(message = "El DNI no puede estar vacío")
    private String dni;

    @Column(unique = true, nullable = false)
    @Email(message = "El formato del email es inválido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Column(nullable = false)
    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    @NotNull(message = "La fecha de nacimiento no puede estar vacía")
    private LocalDate birthday;

    @Column(nullable = false)
    private int age;

    @Column(unique = true, nullable = false, length = 9)
    @Pattern(regexp = "^[0-9]{9}$", message = "El número de teléfono debe contener exactamente 9 dígitos numéricos")
    @NotBlank(message = "El número de teléfono no puede estar vacío")
    private String phoneNumber;

    private String country;
    private String region;
    private String motivation;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean firstLogin = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Attendance> attendances;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Area area;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @PrePersist
    @PreUpdate
    public void calculateAge() {
        if (this.birthday != null) {
            this.age = Period.between(this.birthday, LocalDate.now()).getYears();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return userId != null && Objects.equals(userId, user.userId);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
