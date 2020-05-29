package it.polimi.rest.messages;

import com.google.gson.annotations.Expose;
import it.polimi.rest.models.Link;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class UserAddMessage {

    private UserAddMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final RootMessage.Response rootLinks;

        @Expose
        public final String username;

        @Expose
        public final String password;

        public Request(RootMessage.Response rootLinks, String username, String password) {
            this.rootLinks = rootLinks;
            this.username = username;
            this.password = password;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            HttpUriRequest request = RequestBuilder
                    .post(rootLinks.usersLink().toString())
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
        private Map<String, Link> _links;

        private Response() {

        }

        public Link selfLink() {
            return _links.get("self");
        }

    }

}
