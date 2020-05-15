package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import it.polimi.rest.models.oauth2.OAuth2Client;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class OAuth2ClientInfo {

    private OAuth2ClientInfo() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        private final TokenId token;
        private final String username;
        private final OAuth2Client.Id client;

        public Request(TokenId token, String username, OAuth2Client.Id client) {
            this.token = token;
            this.username = username;
            this.client = client;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder requestBuilder = RequestBuilder
                    .get(baseUrl + "/users/" + username + "/oauth2/clients/" + client)
                    .setEntity(jsonEntity());

            if (token != null) {
                requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer" + token);
            }

            HttpUriRequest request = requestBuilder.build();
            HttpClient client = HttpClientBuilder.create().build();

            return client.execute(request);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String name;
        public String id;
        public String secret;
        public String callback;

        private Response() {

        }

    }

}
