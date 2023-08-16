package com.github.explore_with_me.main.event.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.model.Event;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, PagingAndSortingRepository<Event, Long> {

    boolean existsByState(State state);

    List<Event> findAllByInitiatorId(Long id, Pageable pagination);

    @Query("select  e from Event as e "
            + " join fetch e.location as l "
            + " where e.id =?1 and e.initiator.id=?2")
    Event findEventByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query("select e from Event as e "
            + " join fetch  e.location as l"
            + " join fetch e.category as c "
            + "where e.id =?1")
    Optional<Event> findEventByIdWithCategoryAndLocation(Long eventId);

    @Query("select e from Event as e "
            + "join  fetch  e.initiator as i "
            + "join fetch e.location as l "
            + "join fetch e.category as c "
            + "where e.eventDate >= :rangeStart "
            + "and e.eventDate <= :rangeEnd "
            + "and (:users is null or e.initiator.id in :users) "
            + "and (:states is null or e.state in :states) "
            + "and (:categories is null or c.id in :categories)")
    List<Event> findEventsByEventParamAndPaginationParams(@Param("users") List<Long> users,
                                                          @Param("states") List<State> states,
                                                          @Param("categories") List<Long> categories, @Param("rangeStart") LocalDateTime rangeStart,
                                                          @Param("rangeEnd") LocalDateTime rangeEnd,
                                                          Pageable pageable);

    @Query("select e from Event as e " +
            "join fetch e.initiator as i " +
            "join fetch e.category as c " +
            "where ( :text is null or (lower(e.annotation) like lower(concat('%' , :text, '%')) " +
            "or lower(e.description) like lower(concat('%' , :text, '%')))) " +
            "and (:categories is null or c.id in :categories) " +
            "and e.paid = :paid " +
            "and e.eventDate >= :rangeStart " +
            "and e.eventDate <= :rangeEnd " +
            "and (e.participantLimit <= e.confirmedRequests or e.participantLimit = 0) " +
            "and e.state ='PUBLISHED' ")
    List<Event> getOnlyAvailableEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageRequest);

    @Query("select e from Event as e " +
            "join fetch e.initiator as i " +
            "join fetch e.category as c " +
            "where (:categories is null or c.id in :categories) "
            + "and (:paid is null or e.paid = :paid) "
            + "and  e.eventDate >= :rangeStart "
            + "and  e.eventDate <= :rangeEnd "
            + "and e.publishedOn is not null "
            + "and (:text is null or (lower(e.annotation) like lower(concat('%', :text, '%')) "
            + "or lower(e.description) like lower(concat('%', :text, '%')))) ")
    List<Event> getEvents(
            @Param("text") String text,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageRequest);
}

