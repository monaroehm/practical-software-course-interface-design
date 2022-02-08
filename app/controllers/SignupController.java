package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import models.UserFactory;
import views.html.*;

import javax.inject.Inject;


/**
 * The Signup controller.
 */
public class SignupController extends Controller {
    private final AssetsFinder assetsFinder;
    private final UserFactory userFactory;

    /**
     * Instantiates a new Signup controller.
     *
     * @param assetsFinder the assets finder
     * @param userFactory  the user factory
     */
    @Inject
    public SignupController(AssetsFinder assetsFinder, UserFactory userFactory) {
        this.assetsFinder = assetsFinder;
        this.userFactory = userFactory;
    }

    /**
     * renders the signup.html.
     *
     * @return the result
     */
    public Result signup() {
        return ok(
                signup.render(
                        "signup",
                        assetsFinder
                ));
    }

    /**
     * Validate signup request.
     *
     * @param request the request
     * @return ok result
     */
    public Result validateSignup(Http.Request request) {
        String username = request.body().asJson().get("username").asText();
        String password = request.body().asJson().get("password").asText();

        UserFactory.User user = userFactory.create(username, password);
        if (user == null) {
            return badRequest();
        } else {
            return ok();
        }
    }
}
