package alex.exceptions;


import java.util.NoSuchElementException;

public class NoSuchUserException extends NoSuchElementException {
    public NoSuchUserException(String token){
        super("the user with the \"" + token + "\"  token does not exist");
    }
}
