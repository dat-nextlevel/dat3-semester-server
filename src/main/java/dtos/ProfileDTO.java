package dtos;
import entities.User;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;


@Data
public class ProfileDTO {
    
    private String username;
    private String display_name;
    private List<HobbyDTO> hobbies;

    public ProfileDTO(User user) {
        this.username = user.getUsername();
        this.display_name = "todo change displayname";
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
    }
    
}
