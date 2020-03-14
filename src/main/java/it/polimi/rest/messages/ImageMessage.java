package it.polimi.rest.messages;

import it.polimi.rest.models.Image;

import java.util.Optional;

public class ImageMessage implements Message {

    private final Image image;

    public ImageMessage(Image image) {
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
