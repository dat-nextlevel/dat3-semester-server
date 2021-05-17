package entities;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import lombok.ToString;

import org.mindrot.jbcrypt.BCrypt;

@ToString
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String latitude;
    private String longitude;
    private String addressId;
    private int radius;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_roles",
            joinColumns = { @JoinColumn(name = "fk_user_id") },
            inverseJoinColumns = { @JoinColumn(name = "fk_role") })
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_hobbies",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "hobby_name") })
    List<Hobby> hobbies;


    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = generateHashedPassword(password);
        this.hobbies = new ArrayList<>();
    }

    private String generateHashedPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = generateHashedPassword(password);
    }

    public boolean verifyPassword(String password) {
        return BCrypt.checkpw(password, this.password);
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void removeRole(Role role) {
        roles.remove(role);
    }

    public List<String> getRolesAsStrings() {
        return roles.isEmpty() ? null : roles.stream().map(Object::toString).collect(Collectors.toList());
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String x) {
        this.latitude = x;
    }

    public String getLongitude() {
        return longitude;
    }


    public void setLongitude(String y) {
        this.longitude = y;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void addHobby(Hobby hobby){
        hobbies.add(hobby);
    }

    public String getAddressId() {
        return addressId;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }

    public void removeAllHobbies() {
        //this.hobbies.forEach(this::removeHobby);
        // Avoiding concurrent exception...
        for (Iterator<Hobby> iterator = this.getHobbies().iterator(); iterator.hasNext();) {
            Hobby hobby = iterator.next();
            iterator.remove();
        }
    }
}
