package ru.bank.branchatmservice.handler;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bank.branchatmservice.exception.BranchNotFoundException;
import ru.bank.branchatmservice.exception.CityNotFoundException;
import ru.bank.branchatmservice.exception.NotFoundException;

import java.time.DateTimeException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ApiResponse(
            responseCode = "400",
            description = "Ошибка валидации данных",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponseDto.class)))
    @ExceptionHandler({
            ConstraintViolationException.class,
            BadRequestException.class,
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class,
            DataIntegrityViolationException.class,
            DateTimeException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequests(Exception e) {
        log.error("Bad Requests error: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponseDto("400 Bad Request", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format("%s: %s", fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return new ErrorResponseDto("400 Bad Request", errorMessage);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            EntityNotFoundException.class,
            NotFoundException.class,
            BranchNotFoundException.class,
            CityNotFoundException.class
    })
    public ErrorResponseDto handleBadRequestsException(Exception e) {
        log.error("Entity not found error: {}", e.getMessage(), e);
        return new ErrorResponseDto("404 NOT FOUND", e.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleEntityExistsException(EntityExistsException e) {
        log.warn(e.getMessage(), e);
        return new ErrorResponseDto("409 CONFLICT", e.getMessage());
    }
}