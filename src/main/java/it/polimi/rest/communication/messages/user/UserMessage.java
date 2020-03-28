package it.polimi.rest.communication.messages.user;

import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.*;

public final class UserMessage {

    private UserMessage() {

    }

    public static Message details(User user) {
        return new Details(user);
    }

    public static Message creation(User user) {
        return new Creation(user);
    }

    public static Message deletion() {
        return new Deletion();
    }

    public static Message list(UsersList users) {
        return new List(users);
    }

}
