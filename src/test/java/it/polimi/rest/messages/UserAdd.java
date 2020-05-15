package it.polimi.rest.messages;

import com.google.gson.annotations.Expose;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class UserAdd {

    private UserAdd() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        @Expose
        public final String username;

        @Expose
        public final String password;

        public Request(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            HttpUriRequest request = RequestBuilder
                    .post(baseUrl + "/users")
                    .setEntity(jsonEntity())
                    .build();

            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }

        @Override
        public Response response(String baseUrl) throws IOException {
            HttpResponse response = rawResponse(baseUrl);
            return parseJson(response, Response.class);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String username;

        private Response() {

        }

    }

}
