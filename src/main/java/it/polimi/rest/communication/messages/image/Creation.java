package it.polimi.rest.communication.messages.image;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.ImageMetadata;

class Creation extends Details {

    public Creation(ImageMetadata metadata) {
        super(metadata);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}
