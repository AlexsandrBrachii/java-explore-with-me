package ru.practicum.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.input_dto.InputHitDto;
import ru.practicum.model.Hit;
import ru.practicum.output_dto.StatsDto;
import ru.practicum.repository.HitRepository;

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

        if (unique && uris != null) {
            stats = hitRepository.getUniqueStatsByUrisAndTimestamps(start, end, uris);
        }
        if (!unique && uris != null) {
            stats = hitRepository.getStatsByUrisAndTimestamps(start, end, uris);
        }
        if (!unique && uris == null) {
            stats = hitRepository.getAllStats(start, end);
        }
        if (unique && uris == null) {
            stats = hitRepository.getAllUniqueStats(start, end);
        }
        return stats;
    }
}
