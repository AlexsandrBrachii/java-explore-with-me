package com.github.explore_with_me.main.event.mapper;

import com.github.explore_with_me.main.event.dto.EventOutDto;
import com.github.explore_with_me.main.event.dto.EventShortDto;
import com.github.explore_with_me.main.event.model.Event;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapstructMapper {

    EventOutDto eventToEventOutDto(Event event);

    List<EventShortDto> eventsToEventShortDtoList(List<Event> events);
}