package dtos.user;

import dtos.DawaDTO;
import dtos.HobbyDTO;
import entities.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@ToString
public class PrivateUserDTO {
    private String username;
    private List<String> roles;
    private List<HobbyDTO> hobbies;
    private int radius;
    private String addressId;
    private String longitude;
    private String latitude;
    private AddressDTO address = new AddressDTO();

    public PrivateUserDTO(User user) {
        this.username = user.getUsername();
        this.roles = user.getRoles().stream().map(Object::toString).collect(Collectors.toList());
        this.hobbies = user.getHobbies().stream().map(HobbyDTO::new).collect(Collectors.toList());
        this.radius = user.getRadius();
        this.addressId = user.getAddressId();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
    }

    public PrivateUserDTO(User user, DawaDTO dawa) {
        this(user);

        // Is this super scuffed..?
        // Is there a better way to translate? Probably.
        // Yes...
        this.address.setId(dawa.getId());
        this.address.setStreet(dawa.getVejnavn());
        this.address.setStreet_number(dawa.getHusnr());
        this.address.setFloor(dawa.getEtage());
        this.address.setZip(dawa.getPostnr());
        this.address.setCity(dawa.getPostnrnavn());
        this.address.setLongitude(String.valueOf(dawa.getX()));
        this.address.setLatitude(String.valueOf(dawa.getY()));
        this.address.setFull_name(dawa.getBetegnelse());
    }
}
