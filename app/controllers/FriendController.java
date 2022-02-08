package controllers;

import models.AchievementFactory;
import models.UserFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendController extends Controller {
    private final AssetsFinder assetsFinder;
    private final AchievementFactory achievementFactory;
    private final UserFactory userFactory;

    /**
     * Constructor of FriendController
     * @param assetsFinder
     * @param achievementFactory allows access to achievements
     * @param userFactory allows access to users
     */
    @Inject
    public FriendController(AssetsFinder assetsFinder, AchievementFactory achievementFactory, UserFactory userFactory) {
        this.assetsFinder = assetsFinder;
        this.achievementFactory = achievementFactory;
        this.userFactory = userFactory;
    }

    /**
     * passes on to friend's profile with important parameters of the friend
     * @param request session has to include user_id of user and the name of the friend
     * @return Status OK, if HTML page with all important parameters is shown
     */
    public Result friend(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        if (request.session().get("friend").isEmpty()) return redirect("/profile");

        String friendsName = request.session().get("friend").get();
        UserFactory.User user = userFactory.getUserByName(friendsName);
        String friendsname = user.getUsername();
        String score = Integer.toString(user.getScore());
        String highscore = Integer.toString(user.getHighscore());
        String gamesPlayed = Integer.toString(user.getGamesPlayed());

        return ok(
                friend.render(
                        "friend", friendsname, score, highscore, gamesPlayed,
                        assetsFinder
                ));
    }

    /**
     * gets achievements of friend to be shown in friend's profile
     * @param request session has to include user_id of user and the name of the friend
     * @return Status OK, if a Map with achievement_id as key and achievement as value gets passed on as Json
     */
    public Result getAchievements(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        if (request.session().get("friend").isEmpty()) return redirect("/profile");

        UserFactory.User friend = userFactory.getUserByName(request.session().get("friend").get());
        int id = friend.getUser_id();
        Map<Integer, AchievementFactory.Achievement> result = new HashMap<>();
        List<AchievementFactory.Achievement> achievementList = achievementFactory.getAllUserAchievements(id);
        for (AchievementFactory.Achievement a : achievementList
        ) {
            result.put(a.getID(), a);
        }
        return ok(Json.toJson(result));
    }

    /**
     * gets friends of friend to be shown in friend's profile
     * @param request session has to include user_id of user and the name of the friend
     * @return Status OK, if List of names of friend's friends as Strings gets passed on as Json
     */
    public Result getFriends(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        if (request.session().get("friend").isEmpty()) return redirect("/profile");

        UserFactory.User friend = userFactory.getUserByName(request.session().get("friend").get());
        List<String> friendsList = new ArrayList<>();
        for (UserFactory.User u : friend.getFriends()) {
            friendsList.add(u.getUsername());
        }
        return ok(Json.toJson(friendsList));
    }

    /**
     * gets picture_id of friend to be shown in friend's profile
     * @param request session has to include user_id of user and the name of the friend
     * @return Status OK, if friend's picture_id gets passed on as Json
     */
    public Result getPicture(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        if (request.session().get("friend").isEmpty()) return redirect("/profile");

        UserFactory.User friend = userFactory.getUserByName(request.session().get("friend").get());
        return ok(Json.toJson(friend.getPicture_id()));
    }
}
