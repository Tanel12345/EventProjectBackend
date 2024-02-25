package ee.MinuTood.Quest.registration.system.domain.event.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import ee.MinuTood.Quest.registration.system.domain.event.Event;
import ee.MinuTood.Quest.registration.system.userInterface.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * Ettevõttest osaleja üksus klass
 *
 * @author Tanel Sepp
 */
@Data
@EqualsAndHashCode(exclude = "event") // Exclude event from equals and hashCode to avoid circular references
@Entity
public class LegalAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonBackReference//ignoreerib antud fieldi ennem serialiseerimist uuesti jsoniks .Muidu tekitab infinit loobi. Ei tule lisada kui kasutame responceks eraldi dtod ilma event fieldita
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String companyName;
    private Long registrationCode;
    private Long numberOfParticipant;
    @Enumerated(EnumType.STRING) // Täpsustab kuidas enum andmbebaasi tuleks salvestada, stringina antud juhul
    private PaymentMethod paymentMethod;
    private String additionalInfo;

    // Getterid, setterid ja muud meetodid
}
