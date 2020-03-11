package it.polimi.rest.serialization;

import com.google.gson.*;
import it.polimi.rest.responses.Link;
import it.polimi.rest.responses.Response;

import java.lang.reflect.Type;
import java.util.*;

public class ResponseJsonSerializer implements JsonSerializer<Response> {

    private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public JsonElement serialize(Response src, Type typeOfSrc, JsonSerializationContext context) {
        Optional<Object> payload = Optional.ofNullable(src.payload);

        JsonObject json;

        if (payload.isPresent()) {
            json = gson.toJsonTree(payload.get()).getAsJsonObject();
        } else {
            json = new JsonObject();
        }

        // Links
        Collection<Link> links = new ArrayList<>();

        Optional<String> self = src.self();
        self.ifPresent(selfUri -> links.add(new Link("self", selfUri)));
        links.addAll(src.links());

        if (links.size() != 0) {
            json.add("_links", gson.toJsonTree(links));
        }

        // Embedded
        Map<String, Object> embedded = src.embedded();

        if (embedded.size() != 0) {
            JsonElement element = gson.toJsonTree(embedded);
            json.add("_embedded", element);
        }

        return json;
    }

}
