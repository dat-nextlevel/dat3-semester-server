package dtos;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DawaDTO {
    private String id;
    private int status;
    private String etage;
    private String vejkode;
    private String vejnavn;
    private String adresseringsvejnavn;
    private String husnr;
    private String postnr;
    private String postnrnavn;
    private double x;
    private double y;
    private String href;
    private String betegnelse;
}
