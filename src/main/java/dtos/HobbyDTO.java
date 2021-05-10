package dtos;

import entities.Hobby;
import lombok.Data;

@Data
public class HobbyDTO {
    private String name;
    private String category;

    public HobbyDTO(Hobby hobby) {
        this.name = hobby.getName();
        this.category = hobby.getCategory();
    }
}
