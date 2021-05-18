package entities.chat;

import entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    private User author;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    private String content;

    public Message(User author, String content){
        this.author = author;
        this.content = content;
    }

}
