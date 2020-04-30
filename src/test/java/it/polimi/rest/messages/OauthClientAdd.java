package it.polimi.rest.messages;

public class OauthClientAdd {

    private OauthClientAdd() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        public final String name;
        public final String callback;

        public Request(String name, String callback) {
            this.name = name;
            this.callback = callback;
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {
        public String name;
        public String callback;
        public String id;

        private Response() {

        }

    }

}
