package dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private String id;
    private String street;
    private String streetNumber;
    private String floor;
    private String floorDoor;
    private String zip;
    private String city;
    private String latitude;
    private String longitude;
    private String designation;
}
