package ee.MinuTood.Quest.registration.system.domain.event.entities;

import ee.MinuTood.Quest.registration.system.domain.event.Event;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * Eraisikust osaleja üksus klass
 *
 * @author Tanel Sepp
 */
@Data
@EqualsAndHashCode(exclude = "event") // Exclude event from equals and hashCode to avoid circular references
@Entity
public class IndividualAttendee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String firstName;
    private String lastName;
    private String personalCode;
    private String paymentMethod;

    // Getterid, setterid ja muud meetodid
}
