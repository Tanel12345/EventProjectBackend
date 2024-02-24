package ee.MinuTood.Quest.registration.system.domain.event.entities;

import ee.MinuTood.Quest.registration.system.domain.event.Event;
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

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private String legalName;
    private String registrationCode;
    private int numberOfAttendees;
    private String paymentMethod;

    // Getterid, setterid ja muud meetodid
}
