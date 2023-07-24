package com.github.explore_with_me.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.github.explore_with_me.stats.input_dto.InputHitDto;
import com.github.explore_with_me.stats.output_dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private static final String STATS_URL = "http://localhost:9090";
    private final RestTemplate restTemplate;

    public StatsClient() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    public void saveHit(InputHitDto inputHitDto) {
        final String url = STATS_URL + "/hit";
        restTemplate.postForEntity(url, inputHitDto, Void.class);
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        final String url = STATS_URL + "/stats?start={start}&end={end}&uris={uris}&unique={unique}";
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