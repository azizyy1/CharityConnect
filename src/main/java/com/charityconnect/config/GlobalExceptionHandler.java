package com.charityconnect.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public String handleMethodNotSupported(HttpRequestMethodNotSupportedException exception,
                                           HttpServletRequest request,
                                           Model model) {
        model.addAttribute("errorMessage", "This method is not allowed for this path.");
        model.addAttribute("path", request.getRequestURI());
        return "error/400"; // On peut réutiliser la page 400 ou en faire une 405
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error/400";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException exception,
                                 HttpServletRequest request,
                                 Model model) {
        model.addAttribute("errorMessage", "The requested page was not found.");
        model.addAttribute("path", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception exception,
                                         HttpServletRequest request,
                                         Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");
        model.addAttribute("path", request.getRequestURI());
        return "error/500";
    }
}
