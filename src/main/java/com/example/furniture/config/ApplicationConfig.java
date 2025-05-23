package com.example.furniture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@EnableAsync
@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class ApplicationConfig {
}
