package controllers;

import models.UserFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.util.HashMap;


public class HighscoreController extends Controller {
    private final AssetsFinder assetsFinder;
    private final UserFactory userFactory;

    /**
     * constructor of HighscoreController
     * @param assetsFinder
     * @param userFactory allows access to users
     */
    @Inject
    public HighscoreController(AssetsFinder assetsFinder, UserFactory userFactory) {
        this.assetsFinder = assetsFinder;
        this.userFactory = userFactory;
    }

    /**
     * passes on to leaderboard
     * @param request session has to include user_id of user
     * @return Status OK, if HTML page is shown
     */
    public Result highscore(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        return ok(
                highscore.render(
                        "highscore",
                        assetsFinder
                ));
    }

    /**
     * gets a Map with username as key and score as value of all users
     * @param request session has to include user_id of user
     * @return Status OK, if Map with username as key and score as value of all users gets passed on as Json
     */
    public Result getScoreboard(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        HashMap<String, String> userMap = new HashMap<>();

        for (UserFactory.User u : userFactory.getAllUsers()) {
            userMap.put(u.getUsername(), Integer.toString(u.getScore()));
        }

        return ok(Json.toJson(userMap));
    }

    /**
     * gets a Map with username as key and highscore as value of all users
     * @param request session has to include user_id of user
     * @return Status OK, if Map with username as key and highscore as value of all users gets passed on as Json
     */
    public Result getHighscoreboard(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        HashMap<String, String> userMap = new HashMap<>();

        for (UserFactory.User u : userFactory.getAllUsers()) {
            userMap.put(u.getUsername(), Integer.toString(u.getHighscore()));
        }
        return ok(Json.toJson(userMap));
    }

    /**
     * gets a Map with username as key and number of gamesPlayed as value of all users
     * @param request session has to include user_id of user
     * @return Status OK, if Map with username as key and number of gamesPlayed as value of all users gets passed on as Json
     */
    public Result getGamesPlayedboard(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        HashMap<String, String> userMap = new HashMap<>();

        for (UserFactory.User u : userFactory.getAllUsers()) {
            userMap.put(u.getUsername(), Integer.toString(u.getGamesPlayed()));
        }
        return ok(Json.toJson(userMap));
    }
}
