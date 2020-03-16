package it.polimi.rest.models;


import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;

import java.lang.reflect.Type;
import java.util.Objects;

@JsonAdapter(ImageId.Serializer.class)
public class ImageId {

    @Expose
    private final String id;

    public ImageId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageId imageId = (ImageId) o;
        return id.equals(imageId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

    public static class Serializer implements JsonSerializer<ImageId> {

        @Override
        public JsonElement serialize(ImageId src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.id);
        }

    }

}
