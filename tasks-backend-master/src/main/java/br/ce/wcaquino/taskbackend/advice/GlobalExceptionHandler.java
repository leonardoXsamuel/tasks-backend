package br.ce.wcaquino.taskbackend.advice;

import br.ce.wcaquino.taskbackend.advice.exceptions.TaskNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorJsonReturn> HandlerTaskNotFoundException(TaskNotFoundException ex, HttpServletRequest hsr) {
        ErrorJsonReturn errorJsonReturn = new ErrorJsonReturn(
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                LocalDateTime.now(),
                ex.getMessage(),
                hsr.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorJsonReturn);
    }

}
