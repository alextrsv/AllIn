package alex.exceptions;

import java.util.NoSuchElementException;

public class NoSuchCategoryException extends NoSuchElementException {
    public NoSuchCategoryException(int id){
        super("the category with the \"" + id + "\"  id does not exist");
    }
}
