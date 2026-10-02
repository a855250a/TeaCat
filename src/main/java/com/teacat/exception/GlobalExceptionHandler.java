package com.teacat.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.teacat.exception.PetNotFoundException;
import com.teacat.exception.ForbiddenException;
import com.teacat.service.AuthService;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->{
            errors.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        });

        return errors;

    }

    @ExceptionHandler(PetNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePetNotFoundException(
            PetNotFoundException ex) {
        Map<String, String> error = new HashMap<>();

        error.put("message", ex.getMessage());
        return error;
    }
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(
            ForbiddenException ex) {
        Map<String,String> error = new HashMap<>();

        error.put("message", ex.getMessage());

        return error;
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> handleBadRequest(IllegalArgumentException ex) {
        Map<String,String> error = new HashMap<>(); error.put("message", ex.getMessage()); return error;
    }

    @ExceptionHandler(AuthService.UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String,String> handleUnauthorized(AuthService.UnauthorizedException ex) {
        Map<String,String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return error;
    }

}

