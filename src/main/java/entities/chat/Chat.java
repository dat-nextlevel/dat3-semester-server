package entities.chat;

import entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<Message> messages = new ArrayList<>();

    @OneToOne
    private User user1;

    @OneToOne
    private User user2;

    public Chat (User user1, User user2){
        this.user1 = user1;
        this.user2 = user2;
    }

    public void addMessage(Message message){
        messages.add(message);
    }
}
