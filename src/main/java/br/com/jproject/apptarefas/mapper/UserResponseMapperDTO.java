package br.com.jproject.apptarefas.mapper;


import br.com.jproject.apptarefas.dto.response.UserResponseDTO;
import br.com.jproject.apptarefas.entities.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface UserResponseMapperDTO {
    UserResponseDTO userToUserResponseDTO(User entity);
    User userResponseDTOToUser(UserResponseDTO entity);
}
