package ua.quiz.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlingController implements ErrorController {
    private final static String ERROR_PATH = "/error";

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value= HttpStatus.BAD_REQUEST)
    public String handling() {
        return "error-page";
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }
}
