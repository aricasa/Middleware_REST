package polimi.mw.imageServer;

//Abstractly represents an image kept in the online storage space

public class Image {

    private String id;
    private String title;

    public Image(String title)
    {this.title=title;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title=title;}

    public String getId() {return id;}
    public void setId(String id) {this.id=id;}

}
