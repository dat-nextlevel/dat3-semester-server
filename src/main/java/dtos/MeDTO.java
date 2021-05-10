package dtos;

import entities.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class MeDTO {
    private String username;
    private String email;
    private List<String> roles;
    private List<HobbyDTO> hobbies;

    public MeDTO(User user) {
        this.username = user.getUsername();
        this.email = "test@test.com";
        this.roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
    }
}
