package com.example.furniture.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RateDto {

    private String userId;
    private String rating;
    private String comment;
}
