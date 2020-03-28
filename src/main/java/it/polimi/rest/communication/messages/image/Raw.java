package it.polimi.rest.communication.messages.image;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.Image;

import java.util.Optional;

class Raw implements Message {

    private final Image image;

    public Raw(Image image) {
        this.image = image;
    }

    @Override
    public int code() {
        return HttpStatus.OK;
    }

    @Override
    public String type() {
        return image.getMediaType();
    }

    @Override
    public Optional<Object> payload() {
        return Optional.ofNullable(image.data);
    }

}
