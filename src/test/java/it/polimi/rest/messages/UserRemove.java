package it.polimi.rest.messages;

public class UserRemove {

    private UserRemove() {

    }

    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String username;

        private Response() {

        }

    }

}
