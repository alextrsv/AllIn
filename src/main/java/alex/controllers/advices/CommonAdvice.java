package alex.controllers.advices;

import alex.dto.Response;
import alex.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonAdvice {


    @ExceptionHandler({NoSuchUserException.class, NoSuchMessengerException.class, NoSuchMesengerOwnedException.class,
            NoSuchCategoryException.class, NoSuchCategoryOwnedByUserException.class,NoSuchDialogException.class})
    public ResponseEntity<Response> handleNoSuchException(NoSuchElementException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MessengerAlreadyOwnedException.class)
    public ResponseEntity<Response> handleMessengerAlreadyOwnedException(MessengerAlreadyOwnedException e) {
        Response response = new Response(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
