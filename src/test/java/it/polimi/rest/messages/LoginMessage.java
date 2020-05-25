package it.polimi.rest.messages;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class LoginMessage {

    private LoginMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final RootMessage.Response rootLinks;
        private final String username;
        private final String password;

        public Request(RootMessage.Response rootLinks, String username, String password) {
            this.rootLinks = rootLinks;
            this.username = username;
            this.password = password;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            HttpUriRequest request = RequestBuilder
                    .post(baseUrl + rootLinks.sessionLink().url)
                    .build();

            HttpClientBuilder clientBuilder = HttpClientBuilder.create();

            if (username != null && password != null) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

                clientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            return clientBuilder.build().execute(request);
        }

        @Override
        public Response response(String baseUrl) throws IOException {
            HttpResponse response = rawResponse(baseUrl);
            return parseJson(response, Response.class);
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String error;

        private Response() {

        }

    }

}
