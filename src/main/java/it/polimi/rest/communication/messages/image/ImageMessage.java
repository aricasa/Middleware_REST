package it.polimi.rest.communication.messages.image;

import it.polimi.rest.communication.messages.Message;
import it.polimi.rest.models.Image;
import it.polimi.rest.models.ImageMetadata;
import it.polimi.rest.models.ImagesList;

public final class ImageMessage {

    private ImageMessage() {

    }

    public static Message details(ImageMetadata image) {
        return new Details(image);
    }

    public static Message raw(Image image) {
        return new Raw(image);
    }

    public static Message creation(ImageMetadata image) {
        return new Creation(image);
    }

    public static Message deletion() {
        return new Deletion();
    }

    public static Message list(ImagesList images) {
        return new List(images);
    }

}
