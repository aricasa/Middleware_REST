package it.polimi.rest.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface Request<R extends Response> {

    default String json() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    default HttpEntity jsonEntity() {
        return new StringEntity(json(), ContentType.APPLICATION_JSON);
    }

    HttpResponse rawResponse(String baseUrl) throws IOException;

    default R parseJson(HttpResponse response, Class<R> clazz) throws IOException {
        HttpEntity entity = response.getEntity();
        String body = entity == null ? "{}" : EntityUtils.toString(entity, StandardCharsets.UTF_8);
        Gson gson = new Gson();
        return gson.fromJson(body, clazz);
    }

    R response(String baseUrl) throws IOException;

}
