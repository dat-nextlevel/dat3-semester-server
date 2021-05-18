package dtos.chat;

import entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class MessageDTO {
    private Map<User, String> messages = new HashMap<>();
    private User user1;
    private User user2;

    public MessageDTO(User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }
}
