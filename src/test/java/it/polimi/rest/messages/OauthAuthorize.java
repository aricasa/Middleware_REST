package it.polimi.rest.messages;

public class OauthAuthorize {

    private OauthAuthorize() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        public final String client_id;
        public final String state;
        public final String redirect_uri;
        public final String scope;

        public Request(String client, String redirect_uri, String scope, String state) {
            this.client_id = client;
            this.redirect_uri = redirect_uri;
            this.scope = scope;
            this.state = state;
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String username;

        private Response() {

        }

    }

}
