package com.example.furniture.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {

    private Long id;
    private String img;
    private String name;
    private String brand;
    private BigDecimal price;
}
