package com.makethediference.mtdapi.infra.mapper;

import com.makethediference.mtdapi.domain.dto.user.ListUser;
import com.makethediference.mtdapi.domain.dto.user.MyProfile;
import com.makethediference.mtdapi.domain.dto.user.RegisterUser;
import com.makethediference.mtdapi.domain.entity.Role;
import com.makethediference.mtdapi.domain.entity.User;
import com.makethediference.mtdapi.service.UserFactory;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

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
        user.setUsername(registerUser.username());
        user.setName(registerUser.name());
        user.setSurname(registerUser.surname());
        user.setDni(registerUser.dni());
        user.setEmail(registerUser.email());
        user.setAge(registerUser.age());
        user.setBirthdate(registerUser.birthdate());
        user.setPhoneNumber(registerUser.phoneNumber());
        user.setCountry(registerUser.country());
        user.setRegion(registerUser.region());
        user.setMotivation(registerUser.motivation());
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
                user.getSurname(),
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
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getName(),
                user.getSurname(),
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
     * Actualiza una entidad User (ya persistida) a partir de MyProfile.
     * Esto es útil para no sobrescribir campos sensibles (e.g. password).
     *
     * @param myProfileDto DTO con los campos editables del perfil
     * @param user         la entidad existente que se desea actualizar
     */
    public void updateFromProfile(MyProfile myProfileDto, User user) {
        user.setName(myProfileDto.name());
        user.setSurname(myProfileDto.surname());
        user.setDni(myProfileDto.dni());
        user.setEmail(myProfileDto.email());
        user.setAge(myProfileDto.age());
        user.setBirthdate(myProfileDto.birthdate());
        user.setPhoneNumber(myProfileDto.phoneNumber());
        user.setCountry(myProfileDto.country());
        user.setRegion(myProfileDto.region());
        user.setMotivation(myProfileDto.motivation());

    }
}
