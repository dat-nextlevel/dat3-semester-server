package dtos.chat;

import dtos.user.ProfileDTO;
import entities.chat.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class MessageDTO {
    private ProfileDTO author;
    private long createdAt;
    private String content;

    public MessageDTO(Message message){
        this.author = new ProfileDTO(message.getAuthor());
        this.createdAt = message.getCreatedAt().getTime();
        this.content = message.getContent();
    }
}
