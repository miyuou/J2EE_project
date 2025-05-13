package com.myapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(MaxUploadSizeExceededException exc) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.PAYLOAD_TOO_LARGE.value());
        modelAndView.addObject("error", "File Too Large");
        modelAndView.addObject("message", "The file size exceeds the maximum allowed size of 10MB.");
        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception exc) {
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", "An unexpected error occurred. Please try again later.");
        return modelAndView;
    }
} 