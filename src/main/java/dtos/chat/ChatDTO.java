package dtos.chat;

import dtos.user.ProfileDTO;
import entities.chat.Chat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ChatDTO {
    private Long id;
    private List<MessageDTO> messages;
    private List<ProfileDTO> participants = new ArrayList<>();

    public ChatDTO(Chat chat){
        this.id = chat.getId();
        this.messages = chat.getMessages().stream().map(MessageDTO::new).collect(Collectors.toList());
        this.participants.add(new ProfileDTO(chat.getUser1()));
        this.participants.add(new ProfileDTO(chat.getUser2()));
    }
}