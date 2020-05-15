package it.polimi.rest.messages;

import it.polimi.rest.models.Link;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UserInfo {

    private UserInfo() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final TokenId token;
        private final String username;

        public Request(TokenId token, String username) {
            this.token = token;
            this.username = username;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + "/users/" + username);

            if (token != null) {
                builder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.toString());
            }

            HttpUriRequest request = builder.build();
            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }

        @Override
        public Response response(String baseUrl) throws IOException {
            return parseJson(rawResponse(baseUrl), Response.class);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String username;
        public Map<String, Link> _links;

        private Response() {

        }

        public Link imagesLink() {
            return _links.get("images");
        }

        public Link oAuth2ClientsLink() {
            return _links.get("oauth2_clients");
        }

        public Link selfLink() {
            return  _links.get("self");
        }

    }

}
