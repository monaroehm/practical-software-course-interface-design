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

public class ProfileController extends Controller {
    private final AssetsFinder assetsFinder;
    private final AchievementFactory achievementFactory;
    private final UserFactory userFactory;

    /**
     * contructor of ProfileCOntroller
     * @param assetsFinder
     * @param achievementFactory allows access to achievements
     * @param userFactory allows access to users
     */
    @Inject
    public ProfileController(AssetsFinder assetsFinder, AchievementFactory achievementFactory, UserFactory userFactory) {
        this.assetsFinder = assetsFinder;
        this.achievementFactory = achievementFactory;
        this.userFactory = userFactory;
    }

    /**
     * passe's on to user's profile with important parameters
     * @param request has to include user_id of user
     * @return Status OK, if HTML page is shown with parameters username, score, highscore and gamesPlayed
     */
    public Result profile(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        UserFactory.User user = userFactory.getUserById(request.session().get("uid").get());
        String username = user.getUsername();
        String score = Integer.toString(user.getScore());
        String highscore = Integer.toString(user.getHighscore());
        String gamesPlayed = Integer.toString(user.getGamesPlayed());

        return ok(
                profile.render(
                        "profile", username, score, highscore, gamesPlayed,
                        assetsFinder
                ));
    }

    /**
     * gets new username and picture source of user to update user's profile. If the new name equals the old name, it wont get updated again in the DB.
     * If the new name equals a another name of a different users in the DB, it returns a badRequest.
     * @param request has to include the user_id in the session and the new username and new picture source in the body
     * @return Status Ok, if everything gets updated
     */
    public Result updateProfile(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String newUsername = request.body().asJson().get("username").asText();
        UserFactory.User user = userFactory.getUserById(request.session().get("uid").get());

        String src = request.body().asJson().get("picSrc").asText();
        user.setPicture_id(getPictureId(src));

        if (user.getUsername().equals(newUsername)) {
            return ok();
        }

        List<UserFactory.User> allUsers = userFactory.getAllUsers();
        for (UserFactory.User u : allUsers
        ) {
            if (u.getUsername().equals(newUsername))
                return badRequest();
        }
        user.setUsername(newUsername);
        return ok();
    }

    /**
     * gives the right picture_id to the given picture source to be saved in the DB
     * @param src picture source is given from method updateProfile
     * @return the right picture_id to the given picture source. Picture_id 7 is the default value of the pictures and shows a gray avatar.
     */
    public int getPictureId(String src) {
        switch (src) {
            case "http://localhost:9000/assets/images/Girl.jpg":
                return 1;
            case "http://localhost:9000/assets/images/Boy.jpg":
                return 2;
            case "http://localhost:9000/assets/images/Girl2.jpg":
                return 3;
            case "http://localhost:9000/assets/images/Boy2.jpg":
                return 4;
            default:
                break;
        }
        return 7;
    }

    /**
     * gets achievements from user to be shown in the profile
     * @param request has to include user_id of user in the session
     * @return Status OK, if Map with achievement_id as key and achievement achievement as value gets passed on as Json
     */
    public Result getAchievements(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        int id = Integer.parseInt(request.session().get("uid").get());
        Map<Integer, AchievementFactory.Achievement> result = new HashMap<>();
        List<AchievementFactory.Achievement> achievementList = achievementFactory.getAllUserAchievements(id);
        for (AchievementFactory.Achievement a : achievementList
        ) {
            result.put(a.getID(), a);
        }
        return ok(Json.toJson(result));
    }

    /**
     * if a new friend gets added, this method checks if you get a new FriendAchievement
     * @param request has to include user_id of user in the session
     * @return Status OK, if a Map with achievement_id as key and achievement as value of all new FriendAchievements of the user gets passed on as Json
     */
    public Result getNewFriendAchievement(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        Map<Integer, AchievementFactory.Achievement> result = new HashMap<>();
        String uId = request.session().get("uid").get();
        UserFactory.User user = userFactory.getUserById(uId);
        int uid = Integer.parseInt(uId);

        List<AchievementFactory.Achievement> achievements = achievementFactory.getAllFriendAchievements();
        for (AchievementFactory.Achievement a : achievements
        ) {
            if (a.isAchieved(user.getFriends().size())) {
                boolean newScoreAchieved = achievementFactory.userGetAchievementIfEmpty(uid, a.getID());
                if (newScoreAchieved)
                    result.put(a.getID(), a);
            }
        }
        return ok(Json.toJson(result));
    }

    /**
     * gets friends of user to be shown in profile
     * @param request session has to include user_id of user
     * @return Status OK, if List of names of user's friends as Strings gets passed on as Json
     */
    public Result getFriends(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String uId = request.session().get("uid").get();
        UserFactory.User user = userFactory.getUserById(uId);
        List<String> friendsList = new ArrayList<>();
        for (UserFactory.User u : user.getFriends()) {
            friendsList.add(u.getUsername());
        }
        return ok(Json.toJson(friendsList));
    }

    /**
     * validates new friend, if friend is a user in the DB, friend is not user itself and user has new friend not already as a friend
     * @param request has to include user_id of user in the session and friend's name in the body
     * @return Status Ok, if the Boolean true, when friend got added, or false, when friend got not added gets passed on as Json
     */
    public Result validateNewFriend(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        String uId = request.session().get("uid").get();
        String friendsName = request.body().asJson().get("friendsName").asText();
        UserFactory.User user = userFactory.getUserById(uId);
        UserFactory.User friend = userFactory.getUserByName(friendsName);

        if (friend == null)
            return ok(Json.toJson(false));

        if (user.getUser_id() == friend.getUser_id())
            return ok(Json.toJson(false));

        for (UserFactory.User u : user.getFriends()) {
            if (u.getUser_id() == friend.getUser_id()) {
                return ok(Json.toJson(false));
            }
        }
        user.addFriend(friend.getUser_id());
        return ok(Json.toJson(true));
    }

    /**
     * adds friend's name to session, when user wants to see friend's profile
     * @param request has to include user_id of the user in the session
     * @return Status OK and adds friend's name to the session
     */
    public Result showFriend(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        return ok().addingToSession(request, "friend", request.body().asJson().get("friendsName").asText());
    }

    /**
     * gets picture_id of user to be shown in profile
     * @param request session has to include user_id of user
     * @return Status OK, if user's picture_id gets passed on as Json
     */
    public Result getPicture(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");
        String uId = request.session().get("uid").get();
        UserFactory.User user = userFactory.getUserById(uId);
        return ok(Json.toJson(user.getPicture_id()));
    }
}
