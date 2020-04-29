package it.polimi.rest.messages;

public class UserAdd {

    private UserAdd() {

    }

    public static class Request implements it.polimi.rest.messages.Request {

        public final String username;
        public final String password;

        public Request(String username, String password) {
            this.username = username;
            this.password = password;
        }

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String username;

        private Response() {

        }

    }

}
