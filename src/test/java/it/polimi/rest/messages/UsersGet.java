package it.polimi.rest.messages;

public class UsersGet {

    private UsersGet() {

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String count;
        public String _embedded;

        private Response() {

        }

    }

}
