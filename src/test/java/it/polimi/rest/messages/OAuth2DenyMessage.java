package it.polimi.rest.messages;

import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.scope.Scope;
import it.polimi.rest.utils.RequestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class OAuth2DenyMessage {

    private OAuth2DenyMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final OAuth2Client.Id clientId;
        private final String callback;
        private final Collection<Scope> scopes;
        private final String state;

        public Request(OAuth2Client.Id clientId, String callback, Collection<Scope> scopes, String state) {
            this.clientId = clientId;
            this.callback = callback;
            this.scopes = scopes;
            this.state = state;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.post(baseUrl + "/oauth2/deny");
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

            String url = response.getFirstHeader("Location").getValue();
            Map<String, String> params = RequestUtils.bodyParams(url.substring(callback.length() + 1));

            return new OAuth2DenyMessage.Response(url.split("\\?")[0], params.get("error"));
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public final String redirectionURI;
        public final String error;

        public Response(String redirectionURI, String error) {
            this.redirectionURI = redirectionURI;
            this.error = error;
        }

    }

}
