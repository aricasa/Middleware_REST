package it.polimi.rest.messages;

import it.polimi.rest.models.oauth2.OAuth2Client;
import it.polimi.rest.models.oauth2.OAuth2RefreshToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

public class OAuth2RefreshTokenMessage {

    private OAuth2RefreshTokenMessage() {

    }

    public static class Request implements it.polimi.rest.messages.Request<Response> {

        private final OAuth2Client.Id clientId;
        private final OAuth2Client.Secret secret;
        private final String grantType;
        private final OAuth2RefreshToken.Id refreshToken;

        public Request(OAuth2Client.Id clientId, OAuth2Client.Secret secret, String grantType, OAuth2RefreshToken.Id refreshToken) {
            this.clientId = clientId;
            this.secret = secret;
            this.grantType = grantType;
            this.refreshToken = refreshToken;
        }

        @Override
        public HttpResponse rawResponse(String baseUrl) throws IOException {
            RequestBuilder builder = RequestBuilder.post(baseUrl + "/oauth2/token");
            builder.addHeader("Content-Type", "application/x-www-form-urlencoded");

            if (clientId != null) {
                builder.addParameter("client_id", clientId.toString());
            }

            if (secret != null) {
                builder.addParameter("client_secret", secret.toString());
            }

            if (grantType != null) {
                builder.addParameter("grant_type", grantType);
            }

            if (refreshToken != null) {
                builder.addParameter("refresh_token", refreshToken.toString());
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

        public String access_token;
        public String expires_in;
        public String refresh_token;

        private Response() {

        }

    }

}
