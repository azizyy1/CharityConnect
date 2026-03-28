package com.charityconnect.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception,
                                                 HttpServletRequest request,
                                                 Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("path", request.getRequestURI());
        return "error/500";
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
