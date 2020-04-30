package it.polimi.rest.messages;

public class UserLogin {

    private UserLogin() {

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String error;

        private Response() {

        }

    }

}
