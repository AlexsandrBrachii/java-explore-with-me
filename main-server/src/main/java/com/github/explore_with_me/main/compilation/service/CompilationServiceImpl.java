package com.github.explore_with_me.main.compilation.service;

import java.util.List;
import java.util.stream.Collectors;

import com.github.explore_with_me.main.compilation.dto.CompilationDto;
import com.github.explore_with_me.main.compilation.dto.NewCompilationDto;
import com.github.explore_with_me.main.compilation.dto.UpdateCompilationRequest;
import com.github.explore_with_me.main.compilation.mapper.CompilationMapper;
import com.github.explore_with_me.main.compilation.model.Compilation;
import com.github.explore_with_me.main.compilation.repository.CompilationRepository;
import com.github.explore_with_me.main.event.mapper.EventMapstructMapper;
import com.github.explore_with_me.main.event.model.Event;
import com.github.explore_with_me.main.event.repository.EventRepository;
import com.github.explore_with_me.main.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final CompilationMapper compilationMapper;

    private final EventMapstructMapper eventMapstructMapper;

    @Transactional
    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> selectedEvents = eventRepository.findAllById(
                newCompilationDto.getEvents());
        Compilation compilation = compilationMapper.dtoToCompilation(newCompilationDto);
        compilationMapper.setEventsToCompilation(compilation, selectedEvents);
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = compilationMapper.compilationtoCompilationDto(compilation);
        compilationDto.setEvents(eventMapstructMapper.eventsToEventShortDtoList(selectedEvents));
        log.info(String.format("Создана подборка событий=%s с id= %s", compilationDto.getTitle(), compilation.getId()));
        return compilationDto;
    }

    @Transactional
    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id= %s не найдена", compId)));
        compilationRepository.delete(compilation);
        log.info("Подборка с id= " + compId + " удалена");
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationForUpdate = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id= %s не найдена", compId)));
        if (updateCompilationRequest.getPinned() != null) {
            compilationForUpdate.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationForUpdate.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {

            List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilationForUpdate.setEvent(events);
        }
        CompilationDto compilationDto = compilationMapper.compilationtoCompilationDto(
                compilationRepository.save(compilationForUpdate));
        compilationDto.setEvents(eventMapstructMapper.eventsToEventShortDtoList(compilationForUpdate.getEvent()));
        log.info("Подборка событий с id= " + compId + " обновлена");
        return compilationDto;
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id= %s не найдена", compId)));
        CompilationDto compilationDto = compilationMapper.compilationtoCompilationDto(compilation);
        compilationDto.setEvents(eventMapstructMapper.eventsToEventShortDtoList(compilation.getEvent()));
        log.info("Получена подборка событий с id= " + compId);
        return compilationDto;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size,
                size);
        List<CompilationDto> pagedCompilations = compilationRepository.findAllByPinned(pinned, pageRequest)
                .stream()
                .map(compilationMapper::compilationtoCompilationDto)
                .collect(Collectors.toList());
        log.info("Получен список подборок событий= " + pagedCompilations);
        return pagedCompilations;
    }
}
