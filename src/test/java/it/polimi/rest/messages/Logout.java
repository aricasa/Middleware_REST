package it.polimi.rest.messages;

import it.polimi.rest.models.TokenId;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class Logout {

    private Logout() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        private final TokenId token;
        private final String session;

        public Request(TokenId token, String session) {
            this.token = token;
            this.session = session;
        }

        @Override
        public HttpResponse run(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.delete(baseUrl + "/sessions/" + session);

            if (token != null) {
                builder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token.toString());
            }

            HttpUriRequest request = builder.build();
            HttpClient client = HttpClientBuilder.create().build();
            return client.execute(request);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        private Response() {

        }

    }

}