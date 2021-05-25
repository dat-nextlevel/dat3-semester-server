package dtos.user;

import dtos.DawaDTO;
import dtos.HobbyDTO;
import entities.User;
import lombok.*;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@ToString
public class PrivateUserDTO {
    private String username;
    private String displayName;
    private List<String> roles;
    private List<HobbyDTO> hobbies;
    private int radius;
    private String addressId;
    private String longitude;
    private String latitude;
    private Date createdAt;
    private AddressDTO address = new AddressDTO();

    public PrivateUserDTO(User user) {
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
        this.radius = user.getRadius();
        this.addressId = user.getAddressId();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.createdAt = user.getCreated_At();
    }

    public PrivateUserDTO(User user, DawaDTO dawa) {
        this(user);

        // Is this super scuffed..?
        // Is there a better way to translate? Probably.
        // Yes...
        this.address.setId(dawa.getId());
        this.address.setStreet(dawa.getVejnavn());
        this.address.setStreetNumber(dawa.getHusnr());
        this.address.setFloor(dawa.getEtage());
        this.address.setZip(dawa.getPostnr());
        this.address.setCity(dawa.getPostnrnavn());
        this.address.setLongitude(String.valueOf(dawa.getX()));
        this.address.setLatitude(String.valueOf(dawa.getY()));
        this.address.setDesignation(dawa.getBetegnelse());
    }
}
