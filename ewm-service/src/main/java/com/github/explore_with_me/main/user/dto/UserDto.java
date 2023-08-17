package com.github.explore_with_me.main.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data()
public class UserDto {

    private Long id;
    @Email
    @NotBlank(message = "Поле: email. Error: не может быть пустым")
    @Size(min = 6, max = 254, message = "Минимальный размер email 6 символов, максимальный 254 символа")
    private String email;
    @NotBlank(message = "Поле: name. Error: не может быть пустым")
    @Size(min = 2, max = 250, message = "Минимальный размер email 2 символа, максимальный 254 символа")
    private String name;
}
