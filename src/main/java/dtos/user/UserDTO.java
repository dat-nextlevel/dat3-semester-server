package dtos.user;

import entities.Role;
import entities.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO {
    private String username;
    private List<String> roles;

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
    }
}
