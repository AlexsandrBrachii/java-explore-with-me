package com.github.explore_with_me.stats.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.github.explore_with_me.stats.repository.HitRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.explore_with_me.stats.input_dto.InputHitDto;
import com.github.explore_with_me.stats.model.Hit;
import com.github.explore_with_me.stats.output_dto.StatsDto;

@Service
@AllArgsConstructor
public class StatsServiceImpl implements StatsService {

    private HitRepository hitRepository;

    @Transactional()
    @Override
    public void saveHit(InputHitDto inputHitDto) {
        Hit hit = Hit.builder()
                .uri(inputHitDto.getUri())
                .ip(inputHitDto.getIp())
                .app(inputHitDto.getApp())
                .timestamp(inputHitDto.getTimestamp())
                .build();
        hitRepository.save(hit);
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<StatsDto> stats = new ArrayList<>();

        if (unique) {
            if (uris == null) {
                stats = hitRepository.getAllUniqueStats(start, end);
            } else {
                stats = hitRepository.getUniqueStatsByUrisAndTimestamps(start, end, uris);
            }
        } else {
            if (uris == null) {
                stats = hitRepository.getAllStats(start, end);
            } else {
                stats = hitRepository.getStatsByUrisAndTimestamps(start, end, uris);
            }
        }
        return stats;
    }
}