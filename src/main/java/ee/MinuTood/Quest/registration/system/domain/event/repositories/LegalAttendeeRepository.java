package ee.MinuTood.Quest.registration.system.domain.event.repositories;

import ee.MinuTood.Quest.registration.system.domain.event.entities.LegalAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * LegalAttendeeRepository interface. Ettevõttest osaleja hoidla interface
 *
 * @author Tanel Sepp
 */
@Repository
public interface LegalAttendeeRepository extends JpaRepository<LegalAttendee, Long> {
    // You can add custom query methods if needed
}
