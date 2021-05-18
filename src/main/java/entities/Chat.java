package entities;

import java.util.HashMap;
import java.util.Map;

public class Chat {
    private Map<User, String> messages = new HashMap<>();
    private User user1;
    private User user2;

    public Chat (User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }
}
