package polimi.mw.imageServer;

import com.google.gson.Gson;
import org.eclipse.jetty.util.preventers.GCThreadLeakPreventer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class ImageServerAPITest {

    ImageServerAPI imageServerAPI = new ImageServerAPI("storage",1000,1000);

    @Test
    public void addUser()
    {
        Gson gson = new Gson();
        User user1= new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        User user2= new User("abcd123", "Anna", "Maria" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        User user3= new User("abcd444", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        User user4= new User("bbbccc", "Anna", "Maria" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        User user5= new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        User user6= new User("9876543", "Anna", "Luca" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        assertEquals(imageServerAPI.users().size(),0);

        //addID tests

        imageServerAPI.addID(user1.getId(),user1);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.addID(user1.getId(),user1);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.addID(user2.getId(),user2);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.addID(user3.getId(),user3);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.addID(user4.getId(),user4);
        assertEquals(imageServerAPI.users().size(),2);

        //add tests

        imageServerAPI.add(user5);
        assertEquals(imageServerAPI.users().size(),2);

        imageServerAPI.add(user6);
        assertEquals(imageServerAPI.users().size(),3);

    }

    @Test
    public void removeUser()
    {
        imageServerAPI.clearUsers();

        User user1= new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        imageServerAPI.add(user1);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.remove("abcd123", "abc");
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.remove("ggggg", "abc");
        assertEquals(imageServerAPI.users().size(),1);

        User user7= new User("abababab", "lucia", "Lucy" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        imageServerAPI.addID("abababab",user7);
        assertEquals(imageServerAPI.users().size(),2);
        String token=user7.addToken(1000);

        imageServerAPI.remove("abababab", token);
        assertEquals(imageServerAPI.users().size(),1);

        imageServerAPI.addID("abababab",user7);
        imageServerAPI.remove("abababab", "fakeToken");
        assertEquals(imageServerAPI.users().size(),2);
    }

    @Test
    public void login() {

        imageServerAPI.clearUsers();

        User user1= new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        imageServerAPI.addID("abcd123",user1);
        String token = imageServerAPI.login(new User("abcd123", "Anna", "annina" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>()));
        assertNotEquals(token,null);
        assertNotEquals(imageServerAPI.user("abcd123",token),null);
        assertTrue(imageServerAPI.user("abcd123",token).hasToken(token));

        assertTrue(imageServerAPI.user("abcd123", "124")==null);
        assertTrue(imageServerAPI.user("abcd123", token)!=null);

        imageServerAPI.remove("abcd123",token);
        assertEquals(imageServerAPI.users().size(),0);
    }


    @Test
    public void uploadDownloadImage() {

        //upload

        Gson gson = new Gson();

        User user1= new User("987654", "Anna", "Lucia" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>());
        imageServerAPI.addID("987654",user1);

        String token = imageServerAPI.login(new User("987654", "Anna", "Lucia" , "rossi" , new ArrayList<Token>(), new ArrayList<Token>()));
        assertTrue(token!=null);
        String path="/Users/Arianna/Desktop/immagine.jpg";
        Image img = new Image(path);
        assertEquals(imageServerAPI.imagesOfUser("987654",token),null);
        assertFalse(imageServerAPI.addImage("9876543",img, "abc"));
        assertEquals(imageServerAPI.imagesOfUser("987654",token),null);
        assertTrue(imageServerAPI.addImage("987654",img, token));
        assertEquals(imageServerAPI.imagesOfUser("987654",token).size(),1);
        assertTrue(imageServerAPI.addImage("987654",img, token));
        assertEquals(imageServerAPI.imagesOfUser("987654",token).size(),2);

        Collection<Image> images = (Collection<Image>) imageServerAPI.imagesOfUser("987654",token);

        //download

        //assertTrue(imageServerAPI.image("987654",images.iterator().next().getId())!=null);
        //assertTrue(imageServerAPI.image("987654",images.iterator().next().getId()).getTitle().compareTo(path)==0);

    }



}