import controllers.AssetsFinder;
import org.junit.Test;

import static play.test.Helpers.*;

import play.test.WithApplication;
import play.twirl.api.Content;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ViewTest extends WithApplication {
    AssetsFinder assetsFinder = provideApplication().injector().instanceOf(AssetsFinder.class);

    @Test
    public void renderProfile() {
        Content html = views.html.profile.render("message", "username",
                "score", "highScore", "gamesPlayed", assetsFinder);

        assertEquals("text/html", html.contentType());
        assertTrue(contentAsString(html).contains("username"));
        assertTrue(contentAsString(html).contains("score"));
        assertTrue(contentAsString(html).contains("highScore"));
        assertTrue(contentAsString(html).contains("gamesPlayed"));
    }

    @Test
    public void renderMainpage() {
        Content html = views.html.mainpage.render("message", "username", assetsFinder);

        assertEquals("text/html", html.contentType());
        assertTrue(contentAsString(html).contains("username"));
    }

    @Test
    public void renderFriend() {
        Content html = views.html.friend.render("message", "username",
                "score", "highScore", "gamesPlayed", assetsFinder);

        assertEquals("text/html", html.contentType());
        assertTrue(contentAsString(html).contains("username"));
        assertTrue(contentAsString(html).contains("score"));
        assertTrue(contentAsString(html).contains("highScore"));
        assertTrue(contentAsString(html).contains("gamesPlayed"));
    }

    @Test
    public void renderQuiz() {
        Content html = views.html.quiz.render( "username", assetsFinder);

        assertEquals("text/html", html.contentType());
        assertTrue(contentAsString(html).contains("username"));
    }
}
