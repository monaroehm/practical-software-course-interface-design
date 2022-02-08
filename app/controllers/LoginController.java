package controllers;


import models.UserFactory;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class LoginController extends Controller {
    private final AssetsFinder assetsFinder;
    private final UserFactory userFactory;

    /**
     * contructor of LoginController
     * @param assetsFinder
     * @param userFactory allows access to users
     */
    @Inject
    public LoginController(AssetsFinder assetsFinder, UserFactory userFactory) {
        this.assetsFinder = assetsFinder;
        this.userFactory = userFactory;

    }

    /**
     * passes on to login page
     * @param request if user_id is still in session, user_id gets deleted in logout method
     * @return Status OK, if HTML page is shown
     */
    public Result login(Http.Request request) {
        logout(request);
        return ok(
                login.render(
                        "login",
                        assetsFinder
                ));
    }

    /**
     * checks if DB has a username with the given username or if the password matches with password in DB
     * @param request  body has to include username and password
     * @return Status OK, if done correctly and adds user_id to the session
     */
    public Result validateLogin(Http.Request request) {
        String username = request.body().asJson().get("username").asText();
        String password = request.body().asJson().get("password").asText();

        UserFactory.User user = userFactory.authenticate(username, password);
        if (user == null) {
            return badRequest();
        } else {
            Map<String, String> sessionMap = new HashMap<>();
            sessionMap.put("uid", user.getUser_id() + "");

            return ok().addingToSession(request, sessionMap);
        }
    }

    /**
     * deletes data of user in session and passes on to login
     * @param request has to include user_id
     * @return redirects to login with empty session
     */
    public Result logout(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        return redirect("/").withNewSession();
    }
}