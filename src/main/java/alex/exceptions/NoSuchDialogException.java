package alex.exceptions;

import java.util.NoSuchElementException;

public class NoSuchDialogException extends NoSuchElementException {
    public NoSuchDialogException(int id){
        super("the dialog with the '" + id + "' id does not exist");
    }
}
