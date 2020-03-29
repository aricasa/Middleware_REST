package it.polimi.rest.adapters;

import com.google.gson.*;
import it.polimi.rest.models.Model;
import it.polimi.rest.models.Link;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Create a JSON and hypermedia compliant representation of a {@link Model}.
 *
 * See https://tools.ietf.org/html/draft-kelly-json-hal-06 for details
 * about JSON HAL (JSON Hypertext Application Language).
 *
 * See https://tools.ietf.org/html/rfc5988 for details about the linking standard.
 */
public class ModelJsonAdapter implements JsonSerializer<Model> {

    private final boolean embed;
    private final Gson gsonNoModel;
    private final Gson gsonModelNoEmbed;

    public ModelJsonAdapter() {
        this(true);
    }

    public ModelJsonAdapter(boolean embed) {
        this.embed = embed;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.registerTypeHierarchyAdapter(Calendar.class, new CalendarJsonAdapter());
        this.gsonNoModel = gsonBuilder.create();
        gsonBuilder.registerTypeHierarchyAdapter(Model.class, embed ? new ModelJsonAdapter(false) : this);
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
