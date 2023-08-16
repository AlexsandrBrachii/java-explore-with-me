package com.github.explore_with_me.main.event.mapper;

import com.github.explore_with_me.main.category.model.Category;
import com.github.explore_with_me.main.event.dto.UpdateEventUserDto;
import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event updateEvent(Event eventForUpdate, Category categoryForUpdate, State eventStateForUpdate,
                             UpdateEventUserDto updateEventUserDto) {
        if (updateEventUserDto.getStateAction() == null) {
            eventStateForUpdate = eventForUpdate.getState();
        } else {
            if (updateEventUserDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                eventStateForUpdate = State.PUBLISHED;
            }
            if (updateEventUserDto.getStateAction().equals(State.REJECT_EVENT)) {
                eventStateForUpdate = State.CANCELED;
            }
        }
        eventForUpdate = Event.builder()
                .id(eventForUpdate.getId())
                .eventDate(updateEventUserDto.getEventDate() == null ? eventForUpdate.getEventDate()
                        : updateEventUserDto.getEventDate())
                .paid(updateEventUserDto.getPaid() == null ? eventForUpdate.isPaid() : updateEventUserDto.getPaid())
                .initiator(eventForUpdate.getInitiator())
                .location(updateEventUserDto.getLocation() == null ? eventForUpdate.getLocation()
                        : updateEventUserDto.getLocation())
                .participantLimit(
                        updateEventUserDto.getParticipantLimit() == null ? eventForUpdate.getParticipantLimit()
                                : updateEventUserDto.getParticipantLimit())
                .category(updateEventUserDto.getCategory() == null ? eventForUpdate.getCategory()
                        : categoryForUpdate)
                .createdOn(eventForUpdate.getCreatedOn())
                .annotation(updateEventUserDto.getAnnotation() == null ? eventForUpdate.getAnnotation()
                        : updateEventUserDto.getAnnotation())
                .title(updateEventUserDto.getTitle() == null ? eventForUpdate.getTitle()
                        : updateEventUserDto.getTitle())
                .description(updateEventUserDto.getDescription() == null ? eventForUpdate.getDescription()
                        : updateEventUserDto.getDescription())
                .state(eventStateForUpdate == null ? eventForUpdate.getState()
                        : eventStateForUpdate)
                .requestModeration(
                        updateEventUserDto.getRequestModeration() == null ? eventForUpdate.isRequestModeration()
                                : updateEventUserDto.getRequestModeration())
                .confirmedRequests(eventForUpdate.getConfirmedRequests())
                .views(eventForUpdate.getViews())
                .build();
        return eventForUpdate;
    }
}
