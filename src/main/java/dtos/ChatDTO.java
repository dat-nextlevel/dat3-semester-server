package dtos;

import entities.User;

import java.util.HashMap;
import java.util.Map;

public class ChatDTO {
    private Map<User, String> messages = new HashMap<>();
    private User user1;
    private User user2;

    public ChatDTO (User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }
}
