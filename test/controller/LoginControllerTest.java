package controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AssetsFinder;
import controllers.LoginController;
import models.UserFactory;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;

public class LoginControllerTest extends WithApplication {
    AssetsFinder assetsFinder = provideApplication().injector().instanceOf(AssetsFinder.class);
    UserFactory userFactory = provideApplication().injector().instanceOf(UserFactory.class);
    LoginController loginController;


    @Before
    public void provideLoginController(){
        this.loginController = new LoginController(assetsFinder, userFactory);
    }
    @Test
    public void testLogin(){
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest();
        Result result = loginController.login(requestBuilder.build());
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
    @Test
    public void testValidateLogin(){
        ObjectNode jsonNodes = Json.newObject();
        jsonNodes.put("username", "Test");
        jsonNodes.put("password", "Test");

        UserFactory.User user = userFactory.create("Test", "Test");

        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().bodyJson(jsonNodes);
        Result result = loginController.validateLogin(requestBuilder.build());

        assertEquals(OK, result.status());

        user.delete();
    }
    @Test
    public void testLogout(){
        Http.RequestBuilder requestBuilder = Helpers.fakeRequest().session("uid", "Test");
        Result result = loginController.logout(requestBuilder.build());

        assertEquals(SEE_OTHER, result.status());
    }
}
