package dtos.user;
import dtos.HobbyDTO;
import entities.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class ProfileDTO {
    
    private String username;
    private String displayName;
    private List<HobbyDTO> hobbies;

    public ProfileDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
    }
    
}
