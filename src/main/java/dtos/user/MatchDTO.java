package dtos.user;


import dtos.HobbyDTO;
import entities.User;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class MatchDTO {
    private String username;
    private List<HobbyDTO> hobbies;
    private long distance;

    public MatchDTO(User user, long distance){
        this.username = user.getUsername();
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
        this.distance = distance;
    }

}
