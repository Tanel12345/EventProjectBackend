package ee.MinuTood.Quest.registration.system.domain.event.repositories;

import ee.MinuTood.Quest.registration.system.domain.event.entities.IndividualAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * IndividualAttendeeRepository interface. Eraisikust osaleja hoidla interface
 *
 * @author Tanel Sepp
 */

@Repository
public interface IndividualAttendeeRepository extends JpaRepository<IndividualAttendee, Long> {
    // You can add custom query methods if needed
}