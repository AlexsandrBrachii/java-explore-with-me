package ru.practicum.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.output_dto.StatsDto;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.output_dto.StatsDto(h.app,h.uri, COUNT (h.ip)) "
            + "from Hit as h "
            + "where h.timestamp >= :start and h.timestamp <= :end and h.uri IN :uris "
            + "group by h.app, h.uri "
            + "order by COUNT (h.ip) desc ")
    List<StatsDto> getStatsByUrisAndTimestamps(@Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end,
                                               @Param("uris") List<String> uris);

    @Query("select new ru.practicum.output_dto.StatsDto(h.app,h.uri,  COUNT (distinct h.ip)) "
            + "from Hit as h "
            + "where h.timestamp >= :start and h.timestamp <= :end and h.uri IN :uris "
            + "group by h.app, h.uri "
            + "order by COUNT (distinct h.ip) desc ")
    List<StatsDto> getUniqueStatsByUrisAndTimestamps(@Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end,
                                                     @Param("uris") List<String> uris);

    @Query("select new ru.practicum.output_dto.StatsDto(h.app,h.uri,  COUNT (h.ip)) "
            + "from Hit as h "
            + "where h.timestamp >= :start and h.timestamp <= :end "
            + "group by h.app, h.uri "
            + "order by COUNT (h.ip) desc ")
    List<StatsDto> getAllStats(@Param("start") LocalDateTime start,
                               @Param("end") LocalDateTime end);

    @Query("select new ru.practicum.output_dto.StatsDto(h.app,h.uri,  COUNT (distinct h.ip)) "
            + "from Hit as h "
            + "where h.timestamp >= :start and h.timestamp <= :end "
            + "group by h.app, h.uri "
            + "order by COUNT (distinct h.ip) desc ")
    List<StatsDto> getAllUniqueStats(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end);
}

