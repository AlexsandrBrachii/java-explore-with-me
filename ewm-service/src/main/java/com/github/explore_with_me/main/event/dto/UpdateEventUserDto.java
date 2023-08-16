package com.github.explore_with_me.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.validation.constraints.Size;

import com.github.explore_with_me.main.event.enumerated.State;
import com.github.explore_with_me.main.event.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserDto {

    @Size(min = 20, max = 2000, message = "Минимальная длина аннотации 20 символов, максимальная 2000")
    private String annotation;
    private Long category;
    @Size(min = 20, max = 7000, message = "Минимальная длина описания 20 символов, максимальная 7000")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private State stateAction;
    @Size(min = 3, max = 120, message = "Минимальная длина заголовка 3 символа, максимальная 120")
    private String title;
}
