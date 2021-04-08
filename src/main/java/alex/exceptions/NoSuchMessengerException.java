package alex.exceptions;

import java.util.NoSuchElementException;

public class NoSuchMessengerException extends NoSuchElementException {
    public NoSuchMessengerException(int id){
        super("the messenger with the \"" + id + "\"  id does not exist");
    }
}
