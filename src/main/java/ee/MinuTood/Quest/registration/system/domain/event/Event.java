package ee.MinuTood.Quest.registration.system.domain.event;

import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Event agregaat klass
 *
 * @author Tanel Sepp
 */

@Entity
@Data
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime time;
    @Embedded
    private LocationAddress locationAddress;
    @Column(length = 1000) // Change 1000 to your desired length
    private String additionalInfo;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<IndividualAttendee> individualAttendees = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<LegalAttendee> legalAttendees = new ArrayList<>();

    // Getterid, setterid ja muud meetodid

    public void addIndividualAttendee(IndividualAttendee individualAttendee) {
        // Lisa eraisikust osavõtja olemasolevale üritusele

        individualAttendees.add(individualAttendee);

    }

    public void addLegalAttendee(LegalAttendee legalAttendee) {
        // Lisa juriidilisest isikust osavõtja olemasolevale üritusele

        legalAttendees.add(legalAttendee);

    }
}
