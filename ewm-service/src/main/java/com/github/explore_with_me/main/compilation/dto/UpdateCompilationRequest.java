package com.github.explore_with_me.main.compilation.dto;

import java.util.Set;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UpdateCompilationRequest {

    private final Set<Long> events;

    private final Boolean pinned;

    @Size(message = "Название может содержать от 1 до 50 символов", min = 1, max = 50)
    private final String title;
}
