package com.example.furniture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RegisterDto extends ProfileDto {

    @NotBlank(message = "Пароль отсутствует")
    @Pattern(
            regexp = "^[a-zA-Z0-9!?@#$%^&*()_+=-]{6,20}$",
            message = "Пароль должен быть от 6 до 20 знаков в длину и содержать латиницу, цифры или спец. символы: " +
                      "!?@#$%^&*()_+=-"
    )
    private String password;
}
