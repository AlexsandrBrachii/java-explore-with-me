package com.github.explore_with_me.main.event.controller;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;

import com.github.explore_with_me.main.event.dto.EventOutDto;
import com.github.explore_with_me.main.event.dto.UpdateEventUserDto;
import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventOutDto publishOrCancelEvent(@PathVariable Long eventId,
                                            @RequestBody @Valid UpdateEventUserDto updateEventUserDto) {
        return eventService.publishOrCancelEvent(eventId, updateEventUserDto);
    }

    @GetMapping
    public List<EventOutDto> findEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<State> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{ignoredEventId}/comments/{commentId}")
    public void removeCommentByAdmin(@PathVariable Long ignoredEventId, @PathVariable Long commentId) {
        eventService.removeCommentById(commentId);
    }
}
