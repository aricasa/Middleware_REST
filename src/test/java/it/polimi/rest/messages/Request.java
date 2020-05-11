package it.polimi.rest.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

public interface Request {

    default String json() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    default HttpEntity jsonEntity() {
        return new StringEntity(json(), ContentType.APPLICATION_JSON);
    }

    HttpResponse run(String baseUrl) throws IOException;

}
