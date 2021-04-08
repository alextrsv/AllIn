package alex.dto;

import org.springframework.http.HttpStatus;

public class Response {

    private String message;
    private HttpStatus status;

    public Response() {
    }

    public Response(String message) {
        this.message = message;
    }

    public Response(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
