package ru.bank.branchatmservice.handler;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс для представления ответа об ошибке.
 * Этот класс используется для формирования стандартного ответа об ошибке, содержащего сообщение
 * и временную метку, когда ошибка произошла.
 */
public record ErrorResponseDto(

        String uri,
        String status,
        String message,
        String timestamp
) {
    public ErrorResponseDto(String status, String message) {
        this(
                ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString(),
                status,
                message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}