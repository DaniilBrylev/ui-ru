package com.example.learningplatform.exception;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Profile("dev")
@RestControllerAdvice
public class DevExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void handle(Exception ex) throws Exception {
        ex.printStackTrace(); // лог в консоль
        throw ex;             // даём Spring вернуть stacktrace в HTTP
    }
}
