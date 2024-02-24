package ee.MinuTood.Quest.registration.system.domain.event.repositories;

import ee.MinuTood.Quest.registration.system.domain.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EventRepository interface
 *
 * @author Tanel Sepp
 */

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Event findEventAggregateById(Long id);
    List<Event> findAll();
    Optional<Event> findById(Long Id);

}
