package br.ce.wcaquino.taskbackend.advice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ErrorJsonReturn {

    private HttpStatus status;
    private String error;
    private String timestamp;
    private String mensagem;
    private String path;

    public ErrorJsonReturn(HttpStatus status, String error, LocalDateTime timestamp, String mensagem, String path) {
        this.status = status;
        this.error = status.getReasonPhrase();
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.mensagem = mensagem;
        this.path = path;
    }
}
