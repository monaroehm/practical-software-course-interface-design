package controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mysql.cj.x.protobuf.Mysqlx;
import controllers.AssetsFinder;
import controllers.QuizController;
import models.*;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;

public class QuizControllerTest extends WithApplication {
    AssetsFinder assetsFinder = provideApplication().injector().instanceOf(AssetsFinder.class);
    AchievementFactory achievementFactory = provideApplication().injector().instanceOf(AchievementFactory.class);
    UserFactory userFactory = provideApplication().injector().instanceOf(UserFactory.class);
    GameFactory gameFactory = provideApplication().injector().instanceOf(GameFactory.class);
    UserHasItemFactory userHasItemFactory = provideApplication().injector().instanceOf(UserHasItemFactory.class);
    ItemFactory itemFactory = provideApplication().injector().instanceOf(ItemFactory.class);
    QuizController quizController;

    @Before
    public void provideQuizController(){
        quizController = new QuizController(assetsFinder, achievementFactory, userFactory, gameFactory, userHasItemFactory, itemFactory);
    }
    @Test
    public void testQuiz(){
        UserFactory.User user = userFactory.create("Test", "Test");

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id())).session("mode", "Test");
        Result result = quizController.quiz(requestBuilder.build());

        assertEquals(OK, result.status());

        user.delete();
    }

    @Test
    public void testGetHighscore(){
        UserFactory.User user = userFactory.create("Test", "Test");
        user.setHighscore(500);
        user.save();

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id()));
        Result result = quizController.getHighscore(requestBuilder.build());

        assertEquals(OK, result.status());

        user.delete();
    }
    @Test
    public void testUpdateScore(){
        UserFactory.User user = userFactory.create("Test", "Test");
        ObjectNode jsonNodes = Json.newObject();
        jsonNodes.put("score", "500");

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id())).bodyJson(jsonNodes);
        Result result = quizController.updateScore(requestBuilder.build());

        assertEquals(OK, result.status());
        assertEquals(500, userFactory.getUserById(user.getUser_id()).getScore());
        assertEquals(500, userFactory.getUserById(user.getUser_id()).getHighscore());
        assertEquals(1, userFactory.getUserById(user.getUser_id()).getGamesPlayed());

        user.delete();
    }
    @Test
    public void testGetNewAchievements(){
        UserFactory.User user = userFactory.create("Test", "Test");
        user.setScore(501);
        user.setHighscore(501);
        user.save();

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id()));
        Result result = quizController.getNewAchievements(requestBuilder.build());

        assertEquals(OK, result.status());
        assertFalse(achievementFactory.getAllUserAchievements(user.getUser_id()).isEmpty());

        user.delete();
    }

    @Test
    public void testGetUSerHasItemsForCurrentUser(){
        UserFactory.User user = userFactory.create("Test", "Test");
        userHasItemFactory.create(17, user.getUser_id(), "Schub", 10);

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id()));
        Result result = quizController.getUserHasItemsForCurrentUser(requestBuilder.build());

        assertEquals(OK, result.status());

        user.delete();
    }

    @Test
    public void testUpdateItemsOwned(){
        UserFactory.User user = userFactory.create("Test", "Test");
        userHasItemFactory.create(17, user.getUser_id(), "Schub", 11);
        userHasItemFactory.create(18, user.getUser_id(), "Überspringen", 11);

        ObjectNode jsonNodes = Json.newObject();
        jsonNodes.put("boostItemsOwned", "10");
        jsonNodes.put("skipItemsOwned", "10");

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id())).bodyJson(jsonNodes);
        Result result = quizController.updateItemsOwned(requestBuilder.build());

        assertEquals(OK, result.status());
        assertEquals(10, userHasItemFactory.getUserHasItemForUser(user.getUser_id()).get(1).getAmount());

        user.delete();
    }
    @Test
    public void testEarnNewRandomItem(){
        UserFactory.User user = userFactory.create("Test", "Test");
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", String.valueOf(user.getUser_id()));
        Result result = quizController.earnNewRandomItem(requestBuilder.build());

        assertEquals(OK, result.status());
        assertFalse(userHasItemFactory.getUserHasItemForUser(user.getUser_id()).isEmpty());

        user.delete();
    }
    @Test
    public void testGetMode(){
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", "Test").session("mode", "Test");
        Result result = quizController.getMode(requestBuilder.build());

        assertEquals(OK, result.status());

    }
}
