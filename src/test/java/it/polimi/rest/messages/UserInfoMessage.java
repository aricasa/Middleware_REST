package it.polimi.rest.messages;

import it.polimi.rest.models.Link;
import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;

public class UserInfoMessage {

    private UserInfoMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final RootMessage.Response rootLinks;
        private final TokenId token;
        private final String username;

        public Request(RootMessage.Response rootLinks, TokenId token, String username) {
            this.rootLinks = rootLinks;
            this.token = token;
            this.username = username;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(rootLinks.usersLink() + "/" + username);

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
        private Map<String, Link> _links;

        private Response() {

        }

        public Link selfLink() {
            return _links.get("self");
        }

        public Link imagesLink() {
            return _links.get("images");
        }

        public Link oAuth2ClientsLink() {
            return _links.get("oauth2_clients");
        }

    }

}
