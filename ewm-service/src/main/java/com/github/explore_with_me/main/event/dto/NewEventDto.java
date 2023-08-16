package com.github.explore_with_me.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.github.explore_with_me.main.event.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @Size(min = 20, max = 2000, message = "Минимальная длина аннотации 20 символов, максимальная 2000")
    @NotBlank(message = "Краткое описание события не может быть пустым")
    private String annotation;
    @NotNull(message = "Событие должно относиться к какой-либо категории")
    private Long category;
    @Size(min = 20, max = 7000, message = "Минимальная длина описания 20 символов, максимальная 7000")
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @NotNull(message = "Необходимо указать координаты события")
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120, message = "Минимальная длина заголовка 3 символа, максимальная 120")
    @NotBlank(message = "Заголовок не может быть пустым")
    private String title;
}

