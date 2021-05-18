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
    private String street_number;
    private String floor;
    private String floor_door;
    private String zip;
    private String city;
    private String latitude;
    private String longitude;
    private String full_name;
}
