package com.ibm.event_sync.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    // @ExceptionHandler({ EventNotFoundException.class })
    // protected ResponseEntity<Object> handleNotFound(
    //   Exception ex, WebRequest request) {
    //     return handleExceptionInternal(ex, "Book not found", 
    //       new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    // }

    // @ExceptionHandler({ BookIdMismatchException.class, 
    //   ConstraintViolationException.class, 
    //   DataIntegrityViolationException.class })
    // public ResponseEntity<Object> handleBadRequest(
    //   Exception ex, WebRequest request) {
    //     return handleExceptionInternal(ex, ex.getLocalizedMessage(), 
    //       new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    // }
}

