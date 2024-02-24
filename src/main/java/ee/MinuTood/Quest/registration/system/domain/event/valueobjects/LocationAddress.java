package ee.MinuTood.Quest.registration.system.domain.event.valueobjects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Asukoha aadressi väärtusobjekt. (Ei oma unikaalset id d)
 *
 * @author Tanel Sepp
 */
@Embeddable

@Data
public class LocationAddress {

    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;



}