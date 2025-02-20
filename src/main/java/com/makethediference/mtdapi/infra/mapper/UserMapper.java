package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.MyProfile;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.domain.dto.user.UpdateProfile;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.service.UserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class UserMapper {

    /**
     * Convierte un DTO de registro (RegisterUser) en la entidad User (subclase según el Role).
     * Se apoya en UserFactory para obtener la subclase correcta.
     *
     * @param registerUser DTO de entrada con los datos para crear al usuario
     * @param role         Rol del nuevo usuario
     * @return instancia concreta de User (Admin, Maker, Council o Coordinator)
     */
    public User toEntity(RegisterUser registerUser, Role role) {
        User user = UserFactory.createUser(role);

        fillCommonFields(
                user,
                registerUser.name(),
                registerUser.paternalSurname(),
                registerUser.maternalSurname(),
                registerUser.dni(),
                registerUser.email(),
                registerUser.birthdate(),
                registerUser.phoneNumber(),
                registerUser.country(),
                registerUser.region(),
                registerUser.motivation()
        );
        user.setRole(role);

        return user;
    }

    /**
     * Convierte la entidad User en un DTO ListUser para mostrar info resumida.
     */
    public ListUser toDto(User user) {
        return new ListUser(
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getDni(),
                user.getEmail(),
                user.getAge(),
                user.getBirthdate(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRegion(),
                user.getMotivation()
        );
    }

    /**
     * Convierte la entidad User en un DTO MyProfile.
     * (Puedes usar el mismo approach: o bien modelMapper, o mapeo manual)
     */
    public MyProfile toMyProfile(User user) {
        return new MyProfile(
                user.getUsername(),
                user.getRole(),
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getDni(),
                user.getEmail(),
                user.getAge(),
                user.getBirthdate(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRegion(),
                user.getMotivation()
        );
    }

    public void updateFromProfile(UpdateProfile dto, User user) {
        fillCommonFields(
                user,
                dto.name(),
                dto.paternalSurname(),
                dto.maternalSurname(),
                dto.dni(),
                dto.email(),
                dto.birthdate(),
                dto.phoneNumber(),
                dto.country(),
                dto.region(),
                dto.motivation()
        );
    }


    public UpdateProfile toUpdateProfile(User user) {
        return new UpdateProfile(
                user.getName(),
                user.getPaternalSurname(),
                user.getMaternalSurname(),
                user.getDni(),
                user.getEmail(),
                user.getBirthdate(),
                user.getPhoneNumber(),
                user.getCountry(),
                user.getRegion(),
                user.getMotivation()
        );
    }


    private void setBirthdateAndAge(User user, LocalDate birthdate) {
        user.setBirthdate(birthdate);
        if (birthdate != null) {
            int years = Period.between(birthdate, LocalDate.now()).getYears();
            user.setAge(Math.max(years, 0));
        } else {
            user.setAge(0);
        }
    }

    private void fillCommonFields(User user, String name, String paternal, String maternal, String dni, String email, LocalDate birthdate, String phoneNumber, String country, String region, String motivation) {
        user.setName(name);
        user.setPaternalSurname(paternal);
        user.setMaternalSurname(maternal);
        user.setDni(dni);
        user.setEmail(email);
        setBirthdateAndAge(user, birthdate);
        user.setPhoneNumber(phoneNumber);
        user.setCountry(country);
        user.setRegion(region);
        user.setMotivation(motivation);
    }
}
