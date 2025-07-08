package com.aston.currencyrateapplication.handler;

import com.aston.currencyrateapplication.exception.ParseValutesException;
import com.aston.currencyrateapplication.handler.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ParseValutesException.class)
    public ResponseEntity<ErrorResponse> handleParseValutesException(ParseValutesException ex) {
        ErrorResponse error = new ErrorResponse("Parse error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse error = new ErrorResponse("Internal error", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
