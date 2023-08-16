package com.github.explore_with_me.main.requests.service;

import com.github.explore_with_me.main.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId);

    List<ParticipationRequestDto> getUserRequests(Long userId);
}