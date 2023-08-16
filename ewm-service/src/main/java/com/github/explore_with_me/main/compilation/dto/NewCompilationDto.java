package com.github.explore_with_me.main.compilation.dto;

import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class NewCompilationDto {

    private Set<Long> events = new HashSet<>();

    private boolean pinned;

    @Size(min = 1, max = 50)
    @NotBlank(message = "Название подборки не может быть пустым")
    private String title;
}
