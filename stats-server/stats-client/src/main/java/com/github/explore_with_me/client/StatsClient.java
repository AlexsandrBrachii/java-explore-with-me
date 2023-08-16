package com.github.explore_with_me.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.github.explore_with_me.stats.input_dto.InputHitDto;
import com.github.explore_with_me.stats.output_dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class StatsClient {

    private static final String STATS_URL_DOCKER = "http://stats-server:9090";

    private final RestTemplate restTemplate;

    public StatsClient() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    public void saveHit(InputHitDto inputHitDto) {
        final String url = STATS_URL_DOCKER + "/hit";
        restTemplate.postForEntity(url, inputHitDto, Void.class);
    }

    public List<StatsDto> getStats(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime, String[] uris,
                                   boolean unique) {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = startLocalDateTime.format(outputFormatter);
        String end = endLocalDateTime.format(outputFormatter);
        final String url = STATS_URL_DOCKER + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
        ResponseEntity<List<StatsDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<>() {
                },
                Map.of("start", start, "end", end, "uris", uris, "unique", unique)
        );
        return response.getBody();
    }
}