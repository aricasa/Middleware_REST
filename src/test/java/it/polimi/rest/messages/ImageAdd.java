package it.polimi.rest.messages;

public class ImageAdd {

    private ImageAdd() {

    }


    public static class Response implements it.polimi.rest.messages.Response {

        public String id;
        public String title;

        private Response() {

        }

    }

}
