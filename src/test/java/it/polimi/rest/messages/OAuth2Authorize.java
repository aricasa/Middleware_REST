package it.polimi.rest.messages;

import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class OAuth2Authorize {

    private OAuth2Authorize() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        public final OAuth2Client.Id clientId;
        public final String callback;
        public final Collection<Scope> scopes;
        public final String state;

        public Request(OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) {
            this.clientId = clientId;
            this.callback = callback;
            this.scopes = scopes;
            this.state = state;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.get(baseUrl + "/oauth2/authorize");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");

            if (clientId != null) {
                builder.addParameter("client_id", clientId.toString());
            }

            if (callback != null && !callback.isEmpty()) {
                builder.addParameter("redirect_uri", callback);
            }

            if (scopes != null && !scopes.isEmpty()) {
                builder.addParameter("scope", scopes.stream().map(Object::toString).collect(Collectors.joining(" ")));
            }

            if (state != null && !state.isEmpty()) {
                builder.addParameter("state", state);
            }

            HttpUriRequest request = builder.build();
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

        private Response() {

        }

    }

}
