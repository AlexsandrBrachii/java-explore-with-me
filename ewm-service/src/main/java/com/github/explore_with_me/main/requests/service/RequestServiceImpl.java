package com.github.explore_with_me.main.requests.service;

import java.time.LocalDateTime;
import java.util.List;

import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.model.Event;
import com.github.explore_with_me.main.event.repository.EventRepository;
import com.github.explore_with_me.main.exception.model.ConflictException;
import com.github.explore_with_me.main.exception.model.NotFoundException;
import com.github.explore_with_me.main.requests.dto.ParticipationRequestDto;
import com.github.explore_with_me.main.requests.mapper.RequestMapper;
import com.github.explore_with_me.main.requests.model.Request;
import com.github.explore_with_me.main.requests.repository.RequestRepository;
import com.github.explore_with_me.main.requests.status.Status;
import com.github.explore_with_me.main.user.model.User;
import com.github.explore_with_me.main.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        Request request = new Request();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Вы не можете участвовать в неопубликованном событии");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ConflictException("К сожалению, все места заняты");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Вы не можете добавить запрос на участие в своём событии");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + userId + " не найден"));
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Вы уже участвуете в этом событии");
        }
        request.setEvent(event);
        request.setRequester(user);
        if (event.getParticipantLimit() == 0) {
            request.setStatus(Status.CONFIRMED);
        } else {
            request.setStatus(event.isRequestModeration() ? Status.PENDING : Status.CONFIRMED);
        }
        request.setCreated(LocalDateTime.now());
        request = requestRepository.save(request);
        if (request.getStatus() == Status.CONFIRMED) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }
        log.info("Пользователь с id= " + userId + " создал запрос на участие в событии= " + event);
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequestByRequester(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException("запрос на участие в событии с id= " + requestId + " не найдено"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Вы не оставляли запрос на участие в событии с id=" + requestId);
        }
        request.setStatus(Status.CANCELED);
        requestRepository.save(request);
        return requestMapper.requestToParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id= " + userId + " не найден");
        }
        List<Request> userRequests = requestRepository.findAllByRequesterId(userId);
        List<ParticipationRequestDto> userRequestDtoList = requestMapper.requestListToParticipationRequestDtoList(
                userRequests);
        log.info("Пользователь получил список своих запросов на участие в событии= " + userRequests);
        return userRequestDtoList;
    }
}
