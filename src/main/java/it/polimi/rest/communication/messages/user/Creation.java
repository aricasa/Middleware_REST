package it.polimi.rest.communication.messages.user;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.User;

class Creation extends Details {

    public Creation(User user) {
        super(user);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}
