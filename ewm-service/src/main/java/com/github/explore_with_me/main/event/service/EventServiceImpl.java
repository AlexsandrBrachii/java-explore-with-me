package com.github.explore_with_me.main.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.explore_with_me.client.StatsClient;
import com.github.explore_with_me.main.category.model.Category;
import com.github.explore_with_me.main.category.repository.CategoryRepository;
import com.github.explore_with_me.main.event.dto.*;
import com.github.explore_with_me.main.event.enumerated.Sorting;
import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.mapper.CommentMapper;
import com.github.explore_with_me.main.event.mapper.EventMapper;
import com.github.explore_with_me.main.event.mapper.EventMapstructMapper;
import com.github.explore_with_me.main.event.model.Comment;
import com.github.explore_with_me.main.event.model.Event;
import com.github.explore_with_me.main.event.model.Location;
import com.github.explore_with_me.main.event.repository.CommentRepository;
import com.github.explore_with_me.main.event.repository.EventRepository;
import com.github.explore_with_me.main.event.repository.LocationRepository;
import com.github.explore_with_me.main.exception.model.BadRequestException;
import com.github.explore_with_me.main.exception.model.ConflictException;
import com.github.explore_with_me.main.exception.model.NotFoundException;
import com.github.explore_with_me.main.requests.dto.ParticipationRequestDto;
import com.github.explore_with_me.main.requests.mapper.RequestMapper;
import com.github.explore_with_me.main.requests.model.Request;
import com.github.explore_with_me.main.requests.repository.RequestRepository;
import com.github.explore_with_me.main.requests.status.Status;
import com.github.explore_with_me.main.user.model.User;
import com.github.explore_with_me.main.user.repository.UserRepository;
import com.github.explore_with_me.stats.output_dto.StatsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;
    private final EventMapstructMapper eventMapstructMapper;
    private final EventMapper eventMapperImpl;
    private final RequestMapper requestMapper;
    private final CommentMapper commentMapper;
    private final StatsClient statsClient;

    @Transactional
    @Override
    public EventOutDto createEvent(NewEventDto newEventDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c id " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(" Категория с " + newEventDto.getCategory() + " не найдена"));
        Location eventLocation = locationRepository.save(newEventDto.getLocation());
        Event newEvent = Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .createdOn(LocalDateTime.now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(eventLocation)
                .paid(newEventDto.getPaid() != null && newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit() == null ? 0 : newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .confirmedRequests(0L)
                .views(0L)
                .build();
        newEvent = eventRepository.save(newEvent);
        log.info("Создано новое событие= " + newEvent.getTitle());
        return eventMapstructMapper.eventToEventOutDto(newEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        List<Event> allInitiatorEvents = eventRepository.findAllByInitiatorId(userId, pagination);
        List<EventShortDto> userEventsShortDtoList = eventMapstructMapper.eventsToEventShortDtoList(allInitiatorEvents);
        log.info("Пользователь с id= " + userId + " получил список своих событий= " + userEventsShortDtoList);
        return userEventsShortDtoList;
    }

    @Override
    public EventOutDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findEventByIdAndInitiatorId(eventId, userId);
        if (event == null) {
            throw new NotFoundException("Событие с id= " + eventId + " не найдено");
        }
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Пользователь с id= " + userId + " получил своё событие с id= " + eventId);
        return eventOutDto;
    }

    @Transactional
    @Override
    public EventOutDto patchEvent(Long userId, Long eventId, UpdateEventUserDto updateEventUserDto) {
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не должно быть опубликовано");
        }
        if (!event.getState().equals(State.PENDING) && !event.getState().equals(State.CANCELED)) {
            throw new ConflictException(
                    "Редактировать можно только событие в статусе pending (рассмотрение) и в статусе (canceled) отменено");
        }
        Category categoryForUpdate;
        if (updateEventUserDto.getCategory() != null) {
            categoryForUpdate = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id= " + updateEventUserDto.getCategory()));
        } else {
            categoryForUpdate = event.getCategory();
        }
        State eventStateForUpdate = Optional.ofNullable(updateEventUserDto.getStateAction())
                .filter(state -> state.equals(State.REJECT_EVENT) || state.equals(State.CANCEL_REVIEW))
                .map(state -> State.CANCELED)
                .orElse(State.PENDING);
        event = eventMapperImpl.updateEvent(event, categoryForUpdate, eventStateForUpdate, updateEventUserDto);
        event = eventRepository.save(event);
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info(" событие с id= " + eventId + " обновлено= " + event);
        return eventOutDto;
    }

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        List<ParticipationRequestDto> eventParticipationRequests = requestRepository.findAllRequestForEvent(userId,
                eventId);
        log.info("Получен список запросов на участие в событии с id= " + eventId + "\n Список запросов= "
                + eventParticipationRequests);
        return eventParticipationRequests;
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId,
                                                               EventRequestStatusUpdateRequest statusUpdateRequest) {
        List<Request> requests = requestRepository.findAllByIdInAndEventInitiatorIdAndEventId(
                        statusUpdateRequest.getRequestIds(), userId,
                        eventId).stream()
                .peek(request -> {
                    if (request.getStatus() == Status.CONFIRMED && statusUpdateRequest.getStatus() == Status.REJECTED) {
                        throw new ConflictException("Вы не можете отменить принятую заявку");
                    }
                    request.setStatus(statusUpdateRequest.getStatus());
                })
                .collect(Collectors.toList());
        List<ParticipationRequestDto> participationRequests = requests.stream()
                .peek(request -> request.setStatus(statusUpdateRequest.getStatus()))
                .map(requestMapper::requestToParticipationRequestDto)
                .collect(Collectors.toList());
        EventRequestStatusUpdateResult updatedRequestsStatuses;
        if (statusUpdateRequest.getStatus().equals(Status.CONFIRMED)) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
            if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new ConflictException(
                        "Вы не можете добавить новых участников." + "\n" + event.getConfirmedRequests() + " из "
                                + event.getParticipantLimit() + " запланированных");
            }
            event.setConfirmedRequests(event.getConfirmedRequests() + statusUpdateRequest.getRequestIds().size());
            eventRepository.save(event);
            requestRepository.saveAll(requests);
            updatedRequestsStatuses = new EventRequestStatusUpdateResult(participationRequests, null);
        } else {
            updatedRequestsStatuses = new EventRequestStatusUpdateResult(null, participationRequests);
            requestRepository.saveAll(requests);
        }
        return updatedRequestsStatuses;
    }

    @Transactional
    @Override
    public EventOutDto publishOrCancelEvent(Long eventId, UpdateEventUserDto updateEventUserDto) {
        LocalDateTime publicationDate = LocalDateTime.now();
        if (updateEventUserDto.getEventDate() != null && updateEventUserDto.getEventDate()
                .isBefore(publicationDate.plusHours(1))
        ) {
            throw new BadRequestException(
                    "дата начала изменяемого события должна быть не ранее чем за час от даты публикации");
        }
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState().equals(State.PUBLISHED) && updateEventUserDto.getStateAction()
                .equals(State.REJECT_EVENT)) {
            throw new ConflictException("событие можно отклонить, только если оно еще не опубликовано");
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException(
                    "событие можно публиковать, только если оно в состоянии ожидания публикации (PENDING)");
        }
        if (updateEventUserDto.getStateAction() == null) {
            updateEventUserDto.setStateAction(event.getState());
        }
        if (updateEventUserDto.getStateAction().equals(State.REJECT_EVENT)) {
            updateEventUserDto.setStateAction(State.CANCELED);
        }
        if (updateEventUserDto.getStateAction().equals(State.PUBLISH_EVENT)) {
            updateEventUserDto.setStateAction(State.PUBLISHED);
        }
        Category categoryForUpdate = null;
        if (updateEventUserDto.getCategory() != null) {
            categoryForUpdate = categoryRepository.findById(updateEventUserDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id= " + updateEventUserDto.getCategory()));
        }
        event = eventMapperImpl.updateEvent(event, categoryForUpdate, updateEventUserDto.getStateAction(),
                updateEventUserDto);
        event.setPublishedOn(publicationDate);
        event = eventRepository.save(event);
        EventOutDto eventOutDto = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Событии= " + event.getTitle() + " присвоен статус= " + event.getState());
        return eventOutDto;
    }

    @Override
    public List<EventOutDto> findEventsByAdmin(List<Long> users, List<State> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        PageRequest pagination = PageRequest.of(from / size,
                size);
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart == null || rangeEnd == null) {
            start = LocalDateTime.now();
            end = start.plusYears(1);
        } else {
            start = rangeStart;
            end = rangeEnd;
        }
        List<EventOutDto> eventsByEventParamAndPaginationParams = eventRepository
                .findEventsByEventParamAndPaginationParams(
                        users,
                        states, categories, start,
                        end, pagination).stream()
                .map(eventMapstructMapper::eventToEventOutDto)
                .collect(Collectors.toList());
        log.info("Получен список событий, подходящих под условия= " + eventsByEventParamAndPaginationParams);
        return eventsByEventParamAndPaginationParams;
    }

    @Transactional
    @Override
    public EventOutDto getEvent(Long eventId, String[] uris) {
        Event event = eventRepository.findEventByIdWithCategoryAndLocation(
                eventId).orElseThrow(() -> new NotFoundException("Событие с id= " + eventId + " не найдено"));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие с id= " + eventId + " не найдено");
        }
        long eventStats = getViews(LocalDateTime
                .of(1990, 1, 1, 1, 1), LocalDateTime.now(), uris, true);
        event.setViews(eventStats);
        event = eventRepository.save(event);
        EventOutDto eventOut = eventMapstructMapper.eventToEventOutDto(event);
        log.info("Событие с id= " + eventId + " просмотрено");
        return eventOut;
    }

    @Override
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, boolean onlyAvailable, Sorting sort, int from, int size) {
        PageRequest pageRequest;
        LocalDateTime start;
        LocalDateTime end;
        Sort sorting;
        if (rangeStart == null || rangeEnd == null) {
            start = LocalDateTime.now();
            end = start.plusYears(1);
        } else {
            start = rangeStart;
            end = rangeEnd;
        }
        if (sort == null || sort.equals(Sorting.EVENT_DATE)) {
            sorting = Sort.by("eventDate").descending();
        } else {
            sorting = Sort.by("views").descending();
        }
        pageRequest = PageRequest.of(from / size, size, sorting);
        List<Event> eventsList;
        if (onlyAvailable) {
            eventsList = eventRepository.getOnlyAvailableEvents(text,
                    categories, paid,
                    start, end,
                    pageRequest);
        } else {
            eventsList = eventRepository.getEvents(text,
                    categories, paid,
                    start, end,
                    pageRequest);
        }
        if (eventsList.isEmpty() || !eventRepository.existsByState(State.PUBLISHED)) {
            throw new BadRequestException("Подходящие события не найдены");
        }
        List<EventShortDto> shortEventDtos = eventMapstructMapper.eventsToEventShortDtoList(eventsList);
        log.info("События получены по публичному эндпоинту= " + eventsList);
        return shortEventDtos;
    }

    private long getViews(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        List<StatsDto> eventStats = statsClient.getStats(start, end, uris, unique);
        return eventStats.get(0).getHits();
    }

    @Transactional
    @Override
    public CommentDto createComment(InputCommentDto inputCommentDto, Long authorId, Long eventId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id= " + authorId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %s не найдено", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %s не найдено", eventId));
        }
        Comment newComment = new Comment(null, inputCommentDto.getText(), event, user, LocalDateTime.now());
        newComment = commentRepository.save(newComment);
        log.info(String.format("Пользователь с id= %s добавил новый комментарий= %s", authorId, newComment));
        return commentMapper.commentToCommentDto(newComment);
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId) {
        List<Comment> eventComments = commentRepository.findAllByEventId(eventId);
        log.info("Получены комментарии ивента с id= " + eventId);
        return commentMapper.commentToCommentDto(eventComments);
    }

    @Transactional
    @Override
    public CommentDto changeComment(InputCommentDto inputCommentDto, Long authorId, Long eventId, Long commentId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Пользователь с id= " + authorId + " не найден");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id= %s не найдено", eventId)));
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException(String.format("Событие с id= %s не найдено", eventId));
        }
        Comment commentToChange = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id= %s не найден", commentId)));
        if (!commentToChange.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Вы не можете изменить чужой комментарий");
        }
        if (LocalDateTime.now().isAfter(commentToChange.getCreated().plusDays(1))) {
            throw new BadRequestException("Вы не можете изменять комментарии, которые оставлены более 24 часов назад");
        }
        commentToChange.setText(inputCommentDto.getText());
        commentToChange = commentRepository.save(commentToChange);
        log.info(String.format("Комментарий с id= %s изменён", commentId));
        return commentMapper.commentToCommentDto(commentToChange);
    }

    @Override
    public void removeByCommentIdAndAuthorId(Long commentId, Long authorId) {
        if (!userRepository.existsById(authorId)) {
            throw new NotFoundException("Пользователь с id= " + authorId + " не найден");
        }
        Comment commentToChange = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с id= %s не найден", commentId)));
        if (!commentToChange.getAuthor().getId().equals(authorId)) {
            throw new BadRequestException("Вы не можете удалить чужой комментарий");
        }
        commentRepository.deleteById(commentId);
        log.info(String.format("Комментарий с id= %s удалён", commentId));
    }

    @Override
    public void removeCommentById(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info(String.format("Комментарий с id= %s удалён", commentId));
    }
}

