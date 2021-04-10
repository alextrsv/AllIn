package alex.exceptions;

import java.util.NoSuchElementException;

public class NoSuchMesengerOwnedException extends NoSuchElementException {
    public NoSuchMesengerOwnedException(int messId, String userToken){
        super("messenger '" + messId + "' is NOT  owned by user '" + userToken + "'");
    }
}
