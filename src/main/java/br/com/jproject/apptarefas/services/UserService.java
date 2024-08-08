package br.com.jproject.apptarefas.services;

import br.com.jproject.apptarefas.core.exceptions.exception.UserNotFoundException;
import br.com.jproject.apptarefas.dto.request.UserRequestDTO;
import br.com.jproject.apptarefas.dto.response.UserResponseDTO;
import br.com.jproject.apptarefas.entities.User;
import br.com.jproject.apptarefas.enums.RolesValuesEnum;
import br.com.jproject.apptarefas.mapper.UserResponseMapperDTO;
import br.com.jproject.apptarefas.repository.RoleRepository;
import br.com.jproject.apptarefas.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserResponseMapperDTO userResponseMapperDTO;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, UserResponseMapperDTO userResponseMapperDTO, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userResponseMapperDTO = userResponseMapperDTO;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public Mono<Object> createUser(UserRequestDTO userRequestDTO) {
        return userRepository.findByEmail(userRequestDTO.getEmail())
                .flatMap(existingUser -> Mono.error(new Exception("Não é possível cadastrar usuário com mesmo email")))
                .switchIfEmpty(Mono.defer(() -> builderNewUser(userRequestDTO)
                        .flatMap(userRepository::save)
                        .map(userResponseMapperDTO::userToUserResponseDTO)));
    }

    public Flux<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .map(userResponseMapperDTO::userToUserResponseDTO);
    }

    public Mono<UserResponseDTO> getUserById(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(userResponseMapperDTO::userToUserResponseDTO);
    }

    public Mono<UserResponseDTO> updateUser(UUID id, UserRequestDTO userDetails) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> {
                    setDataUser(userDetails, user);
                    return userRepository.save(user)
                            .map(userResponseMapperDTO::userToUserResponseDTO);
                });
    }

    public Mono<Void> deleteUser(UUID id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .flatMap(user -> userRepository.delete(user));
    }

    private Mono<User> builderNewUser(UserRequestDTO userRequestDTO) {
        return roleRepository.findByName(RolesValuesEnum.BASIC.name())
                .map(roleBasicUser -> {
                    User user = new User();
                    user.setDatacadastro(new Date());
                    user.setRoles(Set.of(roleBasicUser));
                    setDataUser(userRequestDTO, user);
                    return user;
                });
    }

    private void setDataUser(UserRequestDTO userRequestDTO, User user) {
        user.setName(userRequestDTO.getName());
        user.setEmail(userRequestDTO.getEmail());
        if (userRequestDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        }
    }
}