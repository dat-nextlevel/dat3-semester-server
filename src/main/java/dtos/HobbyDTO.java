package dtos;

import entities.Hobby;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HobbyDTO {
    private String name;
    private String category;

    public HobbyDTO(Hobby hobby) {
        this.name = hobby.getName();
        this.category = hobby.getCategory();
    }
}
