package com.github.explore_with_me.main.exception.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private String status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
