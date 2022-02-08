package controllers;

import play.libs.Json;
import models.AchievementFactory;
import models.ItemFactory;
import models.GameFactory;
import models.UserFactory;
import models.UserHasItemFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

/**
 * The Quiz controller.
 */
public class QuizController extends Controller {
    private final AssetsFinder assetsFinder;
    private final AchievementFactory achievementFactory;
    private final UserFactory userFactory;
    private final GameFactory gameFactory;
    private final UserHasItemFactory userHasItemFactory;
    private final ItemFactory itemFactory;

    /**
     * Instantiates a new Quiz controller.
     *
     * @param assetsFinder       the assets finder
     * @param achievementFactory the achievement factory
     * @param userFactory        the user factory
     * @param gameFactory        the game factory
     * @param userHasItemFactory the user has item factory
     * @param itemFactory        the item factory
     */
    @Inject
    public QuizController(AssetsFinder assetsFinder, AchievementFactory achievementFactory, UserFactory userFactory,
                          GameFactory gameFactory, UserHasItemFactory userHasItemFactory, ItemFactory itemFactory) {
        this.assetsFinder = assetsFinder;
        this.achievementFactory = achievementFactory;
        this.userFactory = userFactory;
        this.gameFactory = gameFactory;
        this.userHasItemFactory = userHasItemFactory;
        this.itemFactory = itemFactory;
    }

    /**
     * returns a List of all user highscores saved in the databases inside the body of an ok-Result.
     *
     * @param request the request
     * @return ok Result
     */
    public Result getHighscore(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        int highscore = userFactory.getUserById(request.session().get("uid").get()).getHighscore();
        return ok(Json.toJson(highscore));
    }

    /**
     * renders the quiz.html.
     *
     * @param request the request
     * @return ok result
     */
    public Result quiz(Http.Request request) {
        if (request.session().get("uid").isEmpty() | request.session().get("mode").isEmpty()) return redirect("/");

        String username = userFactory.getUserById(request.session().get("uid").get()).getUsername();
        return ok(
                quiz.render(
                        username, assetsFinder
                ));
    }

    /**
     * receives achieved score from the client and saves it into the database.
     *
     * @param request the request that includes the highscore
     * @return ok or bad result
     */
    public Result updateScore(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String uid = request.session().get("uid").get();
        UserFactory.User user = userFactory.getUserById(uid);

        if (user == null) {
            return badRequest();
        } else {
            int gameScore = Integer.parseInt(request.body().asJson().get("score").asText());

            if (gameScore > user.getHighscore()) {
                user.setHighscore(gameScore);
            }
            user.updateGamesPlayed();
            user.addScore(gameScore);
            return ok();
        }
    }

    /**
     * Gets new achievements.
     *
     * @param request the request
     * @return the new achievements
     */
    public Result getNewAchievements(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        Map<Integer, AchievementFactory.Achievement> result = new HashMap<>();
        boolean newScoreAchieved;
        boolean newHighscoreAchieved;
        boolean newGamesPlayedAchieved;

        int uid = Integer.parseInt(request.session().get("uid").get());
        UserFactory.User user = userFactory.getUserById(uid);

        List<AchievementFactory.Achievement> achievements = achievementFactory.getAllGamesRelevantAchievements();
        for (AchievementFactory.Achievement a : achievements
        ) {
            switch (a.getName()) {
                case "ScoreAchievement":
                    if (a.isAchieved(user.getScore())) {
                        newScoreAchieved = achievementFactory.userGetAchievementIfEmpty(uid, a.getID());
                        if (newScoreAchieved)
                            result.put(a.getID(), a);
                    }
                    break;
                case "HighscoreAchievement":
                    if (a.isAchieved(user.getHighscore())) {
                        newHighscoreAchieved = achievementFactory.userGetAchievementIfEmpty(uid, a.getID());
                        if (newHighscoreAchieved)
                            result.put(a.getID(), a);
                    }
                    break;
                case "GamesPlayedAchievement":
                    if (a.isAchieved(user.getGamesPlayed())) {
                        newGamesPlayedAchieved = achievementFactory.userGetAchievementIfEmpty(uid, a.getID());
                        if (newGamesPlayedAchieved)
                            result.put(a.getID(), a);
                    }
                    break;
            }
        }

        return ok(Json.toJson(result));
    }

    /**
     * Store game result.
     *
     * @param request the request
     * @return ok result
     */
// uid und timeStamp = eindeutiges Spiel
    public Result storeGame(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        int duration = Integer.parseInt(request.body().asJson().get("duration").asText());
        int score = Integer.parseInt(request.body().asJson().get("score").asText());
        int uid = Integer.parseInt(request.session().get("uid").get());
        gameFactory.createGame(timeStamp,
                duration,
                score,
                uid);
        return ok();
    }

    /**
     * Gets user_has_items for current user and returns it as json inside the result body.
     *
     * @param request the request
     * @return the user_has_items for current user as json inside result body
     */
    public Result getUserHasItemsForCurrentUser(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String user_id = request.session().get("uid").get();
        List<UserHasItemFactory.User_Has_Item> user_has_itemList = userHasItemFactory.getUserHasItemForUser(user_id);
        return ok(Json.toJson(user_has_itemList));
    }

    /**
     * Update items owned result.
     *
     * @param request the request
     * @return ok result
     */
    public Result updateItemsOwned(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String user_id = request.session().get("uid").get();
        int boostItemsOwned = request.body().asJson().get("boostItemsOwned").asInt();
        int skipItemsOwned = request.body().asJson().get("skipItemsOwned").asInt();
        List<UserHasItemFactory.User_Has_Item> user_has_itemList = userHasItemFactory.getUserHasItemForUser(user_id);
        for (UserHasItemFactory.User_Has_Item user_has_item : user_has_itemList) {
            switch (user_has_item.getItem_name()) {
                case "Schub":
                    user_has_item.setAmount(boostItemsOwned);
                    user_has_item.save();
                    break;
                case "Überspringen":
                    user_has_item.setAmount(skipItemsOwned);
                    user_has_item.save();
                    break;
            }
        }
        return ok();
    }

    /**
     * Earn new random item. It's saved in the database and the name is returned inside the result body.
     *
     * @param request the request
     * @return ok result
     */
    public Result earnNewRandomItem(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String userId = request.session().get("uid").get();
        List<UserHasItemFactory.User_Has_Item> user_has_itemList = userHasItemFactory.getUserHasItemForUser(Integer.parseInt(userId));
        List<ItemFactory.Item> itemList = itemFactory.getAllItems();
        int random = (int) (Math.floor(Math.random() * itemList.size()));
        int randomItemId = itemList.get(random).getItem_id();
        String randomItemName = "";

        for (ItemFactory.Item item : itemList) {
            if (randomItemId == item.getItem_id()) {
                randomItemName = item.getName();
            }
        }

        for (UserHasItemFactory.User_Has_Item user_has_item : user_has_itemList) {
            if (user_has_item.getItem_id() == randomItemId) {
                user_has_item.setAmount(user_has_item.getAmount() + 1);
                user_has_item.save();
                return ok(Json.toJson(new String[]{randomItemName}));
            }
        }
        // if user_has_item connection does not exist between logged in user and randomItemId yet
        userHasItemFactory.create(randomItemId, Integer.parseInt(userId), randomItemName, 1);

        return ok(Json.toJson(new String[]{randomItemName}));
    }

    /**
     * Gets the GameMode from the session and returns it inside the result body.
     *
     * @param request the request
     * @return ok result with mode
     */
    public Result getMode(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        request.session().get("mode").get();

        return ok(request.session().get("mode").get());
    }
}
