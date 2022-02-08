package models;

import org.junit.Test;
import play.test.WithApplication;
import java.util.List;

import static org.junit.Assert.*;

public class UserTest extends WithApplication {
    UserFactory userFactory = provideApplication().injector().instanceOf(UserFactory.class);

    @Test
    public void testCreateUser() {
        String username = "TEST";
        String password = "password";

        UserFactory.User user = userFactory.create(username, password);
        UserFactory.User savedUser = userFactory.getUserById(user.getUser_id());
        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());

        user.delete();
    }

    @Test
    public void testSaveUser(){
        String username = "TEST";
        String password = "password";
        String newUsername = "Steffi";
        int newScore = 1234;

        UserFactory.User user = userFactory.create(username, password);
        user.setUsername(newUsername);
        user.setScore(newScore);
        user.save();

        assertEquals(newUsername, user.getUsername());
        assertEquals(newScore, user.getScore());

        user.delete();
    }
    @Test
    public void testDeleteUser(){
        String username = "TEST";
        String password = "password";

        UserFactory.User user = userFactory.create(username, password);
        user.delete();

        assertNull(userFactory.getUserById(user.getUser_id()));
    }

    @Test
    public void testAddFriend(){
        String username = "TEST";
        String password = "password";
        final int ADMIN_ID = 5;

        UserFactory.User user = userFactory.create(username, password);
        user.addFriend(ADMIN_ID);
        List<UserFactory.User> usersFriends = user.getFriends();

        assertFalse(usersFriends.isEmpty());

        user.delete();
    }
}
