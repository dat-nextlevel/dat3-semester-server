package entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Data
@NoArgsConstructor
@Entity
public class Hobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String name;

    private String category;

    @ManyToMany(mappedBy = "hobbies")
    private List<User> users;

    public Hobby(String name, String category) {
        this.name = name;
        this.category = category;
        this.users = new ArrayList<>();
    }
}