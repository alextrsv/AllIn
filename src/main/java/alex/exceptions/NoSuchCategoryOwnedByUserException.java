package alex.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCategoryOwnedByUserException extends NoSuchElementException {
    public NoSuchCategoryOwnedByUserException(int id, String userToken){
        super("the category with the \"" + id + "\"  id does not owned by user" + userToken);
    }
}
