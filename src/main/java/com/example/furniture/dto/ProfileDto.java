package com.example.furniture.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDto {

    @NotBlank(message = "Имя пользователя отсутствует")
    private String username;

    @NotBlank(message = "Фамилия отсутствует")
    private String lastname;

    @NotBlank(message = "Имя отсутствует")
    private String name;

    private String patronymic;

    @Email(message = "Email неверного формата")
    private String email;

    @Pattern(
            regexp = "^((8|(\\+)?7)[\\- ]?)(\\(?\\d{3}\\)?[\\- ]?\\d{3}[\\- ]?)\\d{2}[\\- ]?\\d{2}$",
            message = "Номер телефона неверного формата"
    )
    private String phone;

    @Min(value = 14, message = "Недопустимый возраст (минимальный возраст - 14 лет)")
    private Integer age;
}
