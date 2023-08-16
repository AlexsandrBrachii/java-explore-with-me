package com.github.explore_with_me.main.event.mapper;

import com.github.explore_with_me.main.category.model.Category;
import com.github.explore_with_me.main.event.dto.UpdateEventUserDto;
import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.model.Event;

public interface EventMapper {

    Event updateEvent(Event eventForUpdate, Category categoryForUpdate, State eventStateForUpdate,
                      UpdateEventUserDto updateEventUserDto);
}
