package ee.MinuTood.Quest.registration.system.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import ee.MinuTood.Quest.registration.system.domain.event.valueobjects.LocationAddress;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Column(length = 1000) // Lubab tabelisse kuni 1000 täheühikut
    private String additionalInfo;
    @JsonIgnoreProperties({"individualAttendees"}) //ignoreerib antud fieldi ennem serialiseerimist uuesti jsoniks
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<IndividualAttendee> individualAttendees = new ArrayList<>();
    @JsonIgnoreProperties({"legalAttendees"}) //ignoreerib antud fieldi ennem serialiseerimist uuesti jsoniks
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<LegalAttendee> legalAttendees = new ArrayList<>();


    public void addIndividualAttendee(IndividualAttendee individualAttendee) {
        // Lisa eraisikust osavõtja olemasolevale üritusele

        individualAttendees.add(individualAttendee);

    }

    public void addLegalAttendee(LegalAttendee legalAttendee) {
        // Lisa juriidilisest isikust osavõtja olemasolevale üritusele

        legalAttendees.add(legalAttendee);

    }

    public boolean deleteIndividualAttendeeById(Long attendeeId) {
        for (IndividualAttendee attendee : individualAttendees) {
            if (attendee.getId().equals(attendeeId)) {
                attendee.setEvent(null); // Eemaldab ürituse viite osalejalt
                boolean removed = individualAttendees.remove(attendee);

                if (!individualAttendees.isEmpty()) {
                    System.out.println("First element ID: " + individualAttendees.get(0).getId());
                }
                return removed;
            }
        }
        return false;
    }


    public boolean deleteLegalAttendeeById(Long attendeeId) {
        for (LegalAttendee attendee : legalAttendees) {
            if (attendee.getId().equals(attendeeId)) {
                attendee.setEvent(null); // Eemaldab ürituse viite osalejalt
                boolean removed = legalAttendees.remove(attendee);

                if (!legalAttendees.isEmpty()) {
                    System.out.println("First element ID: " + legalAttendees.get(0).getId());
                }
                return removed;
            }
        }
        return false;

    }

    public boolean updateIndividualAttendee(IndividualAttendee updatedAttendee) {
        for (int i = 0; i < individualAttendees.size(); i++) {
            IndividualAttendee existingAttendee = individualAttendees.get(i);
            if (existingAttendee.getId().equals(updatedAttendee.getId())) {
                // Updateb fieldid osalejal kelle id vastab sisestatuga
                existingAttendee.setFirstName(updatedAttendee.getFirstName());
                existingAttendee.setLastName(updatedAttendee.getLastName());
                existingAttendee.setPersonalCode(updatedAttendee.getPersonalCode());
                existingAttendee.setPaymentMethod(updatedAttendee.getPaymentMethod());
                existingAttendee.setAdditionalInfo(updatedAttendee.getAdditionalInfo());


                return true;
            }
        }
        return false; // Attendeed ei leitud
    }

    public boolean updateLegalAttendee(LegalAttendee updatedAttendee) {
        for (int i = 0; i < legalAttendees.size(); i++) {
            LegalAttendee existingAttendee = legalAttendees.get(i);
            if (existingAttendee.getId().equals(updatedAttendee.getId())) {
                // Updateb fieldid osalejal kelle id vastab sisestatuga
                existingAttendee.setCompanyName(updatedAttendee.getCompanyName());
                existingAttendee.setRegistrationCode(updatedAttendee.getRegistrationCode());
                existingAttendee.setNumberOfParticipant(updatedAttendee.getNumberOfParticipant());
                existingAttendee.setPaymentMethod(updatedAttendee.getPaymentMethod());
                existingAttendee.setAdditionalInfo(updatedAttendee.getAdditionalInfo());


                return true;
            }
        }
        return false; // Attendeed ei leitud
    }

}
