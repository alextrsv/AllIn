package alex.exceptions;

public class MessengerAlreadyOwnedException extends Exception{

    public MessengerAlreadyOwnedException(String messengerName, String userToken){
        super("messenger '" + messengerName + "' is already owned by user '" + userToken + "'");
    }
}
