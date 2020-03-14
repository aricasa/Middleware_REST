package it.polimi.rest.messages;

import it.polimi.rest.models.User;

public class UserCreationMessage extends UserDetailsMessage {

    public UserCreationMessage(User user) {
        super(user);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}
