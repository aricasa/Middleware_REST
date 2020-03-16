package it.polimi.rest.communication.messages;

import it.polimi.rest.communication.HttpStatus;
import it.polimi.rest.models.ImageMetadata;

public class ImageCreationMessage extends ImageDetailsMessage {

    public ImageCreationMessage(ImageMetadata metadata) {
        super(metadata);
    }

    @Override
    public int code() {
        return HttpStatus.CREATED;
    }

}
