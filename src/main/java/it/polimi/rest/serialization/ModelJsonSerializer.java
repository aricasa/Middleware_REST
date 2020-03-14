package it.polimi.rest.serialization;

import com.google.gson.*;
import it.polimi.rest.models.Model;
import it.polimi.rest.models.Link;

import java.lang.reflect.Type;
import java.util.*;

public class ModelJsonSerializer implements JsonSerializer<Model> {

    private final boolean embed;
    private final Gson gsonNoModel;
    private final Gson gsonModelNoEmbed;

    public ModelJsonSerializer() {
        this(true);
    }

    public ModelJsonSerializer(boolean embed) {
        this.embed = embed;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeHierarchyAdapter(Calendar.class, new CalendarSerializer());
        this.gsonNoModel = gsonBuilder.create();
        gsonBuilder.registerTypeHierarchyAdapter(Model.class, embed ? new ModelJsonSerializer(false) : this);
        this.gsonModelNoEmbed = gsonBuilder.create();
    }

    @Override
    public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = gsonNoModel.toJsonTree(src).getAsJsonObject();

        // Links
        Map<String, Link> links = new HashMap<>();

        Optional<String> self = src.self();
        self.ifPresent(url -> links.put("self", new Link(url)));

        if (embed) {
            Optional.ofNullable(src.links()).ifPresent(links::putAll);
        }

        if (links.size() != 0) {
            json.add("_links", context.serialize(links));
        }

        // Embedded
        if (embed) {
            Optional.ofNullable(src.embedded()).ifPresent(embedded -> {
                if (embedded.size() != 0) {
                    JsonElement element = gsonModelNoEmbed.toJsonTree(embedded);
                    json.add("_embedded", element);
                }
            });
        }

        return json;
    }

}
