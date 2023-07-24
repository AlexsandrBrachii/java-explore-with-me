package com.github.explore_with_me.stats.service;


import com.github.explore_with_me.stats.input_dto.InputHitDto;
import com.github.explore_with_me.stats.output_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(InputHitDto inputHitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

