package com.carrotbat410.lol_team_balance_tool.exHandler;

import com.carrotbat410.lol_team_balance_tool.dto.response.ErrorResponseDTO;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.DataConflictException;
import com.carrotbat410.lol_team_balance_tool.exHandler.exception.RiotApiNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponseDTO beanValidationHandle(MethodArgumentNotValidException e) {

        FieldError error = ((FieldError) e.getBindingResult().getAllErrors().get(0));
//        String fieldName = error.getField();
        String message = error.getDefaultMessage();
        String code = error.getCode();

        return new ErrorResponseDTO(code, message);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponseDTO dataConflictException(DataConflictException e) {
        return new ErrorResponseDTO(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(RiotApiNotFoundException.class)
    public ErrorResponseDTO riotApiNotFoundException(RiotApiNotFoundException e) {
        return new ErrorResponseDTO("RIOT_API_ERROR", e.getMessage());
    }

}
