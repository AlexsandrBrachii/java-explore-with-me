package ru.practicum.service;


import ru.practicum.input_dto.InputHitDto;
import ru.practicum.output_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(InputHitDto inputHitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

