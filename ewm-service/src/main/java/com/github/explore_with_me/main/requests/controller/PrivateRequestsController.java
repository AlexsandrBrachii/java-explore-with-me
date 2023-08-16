package com.github.explore_with_me.main.requests.controller;

import com.github.explore_with_me.main.requests.dto.ParticipationRequestDto;
import com.github.explore_with_me.main.requests.service.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestsController {

    private final RequestService requestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ParticipationRequestDto createRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequestByRequester(@PathVariable Long userId,
                                                            @PathVariable Long requestId) {
        return requestService.cancelRequestByRequester(userId, requestId);
    }

    @GetMapping()
    public List<ParticipationRequestDto> getUserRequest(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }
}

