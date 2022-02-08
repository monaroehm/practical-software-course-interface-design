package controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AssetsFinder;
import controllers.SignupController;
import models.AchievementFactory;
import models.UserFactory;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.*;
import static org.junit.Assert.*;



public class SignupControllerTest extends WithApplication {
    AssetsFinder assetsFinder = provideApplication().injector().instanceOf(AssetsFinder.class);
    UserFactory userFactory = provideApplication().injector().instanceOf(UserFactory.class);
    SignupController signupController;

    @Before
    public void provideSignupController(){
        this.signupController = new SignupController(assetsFinder, userFactory);
    }

    @Test
    public void testSignup(){
        Result result = signupController.signup();
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
   @Test
    public void testValidateSignup(){
       ObjectNode jsonNodes = Json.newObject();
       jsonNodes.put("username", "Test");
       jsonNodes.put("password", "Test");

       Http.RequestBuilder requestBuilder = Helpers.fakeRequest().bodyJson(jsonNodes);
       Result result = signupController.validateSignup(requestBuilder.build());

       assertEquals(OK, result.status());
       assertNotNull(userFactory.getUserByName("Test"));

       userFactory.getUserByName("Test").delete();
   }
}
