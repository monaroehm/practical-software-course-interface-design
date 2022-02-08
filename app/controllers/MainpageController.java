package controllers;

import models.MessageFactory;
import models.ItemFactory;
import models.UserFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainpageController extends Controller {
    private final AssetsFinder assetsFinder;
    private final UserFactory userFactory;
    private final ItemFactory itemFactory;
    private final MessageFactory messageFactory;

    /**
     * constructor of MainpageController
     * @param assetsFinder
     * @param userFactory allows access to users
     * @param itemFactory allows access to items
     * @param messageFactory allows access to messages
     */
    @Inject
    public MainpageController(AssetsFinder assetsFinder, UserFactory userFactory, ItemFactory itemFactory, MessageFactory messageFactory) {
        this.assetsFinder = assetsFinder;
        this.userFactory = userFactory;
        this.itemFactory = itemFactory;
        this.messageFactory = messageFactory;
    }

    /**
     * passes on to garage
     * @param request has to include user_id
     * @return Status OK, of HTML page is shown with username
     */
    public Result mainpage(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String username = userFactory.getUserById(request.session().get("uid").get()).getUsername();

        return ok(
                mainpage.render(
                        "mainpage", username, assetsFinder
                ));
    }

    /**
     * adds mode to session for quiz
     * @param request has to have user_id in session and mode in body
     * @return Status OK and adds mode to session
     */
    public Result setMode(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String mode = request.body().asJson().get("mode").asText();
        request.session().adding("mode", mode);

        return ok().addingToSession(request, "mode", mode);
    }

    // getMode in QuizController

    /**
     * gets all items of user to be shown in inventar
     * @param request has to include user_id in session
     * @return Status OK, if List of items of user is passed on as Json
     */
    public Result getAllItems(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        List<ItemFactory.Item> itemList = itemFactory.getAllItems();
        return ok(Json.toJson(itemList));
    }

    /**
     * get all messages and passes list of messages as Strings on  to be shown in chat
     * @param request has to include user_id of user in session
     * @return Status OK, if List of messages as String gets passed on
     */

    // already defined in QuizController:
    // public Result getUserHasItemsForCurrentUser

    public Result getAllMessages(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        int id = Integer.parseInt(request.session().get("uid").get());
        String uId = request.session().get("uid").get();
        List<MessageFactory.Message> messageList = messageFactory.getAllMessages(id);
        List<String> display_messsage = new ArrayList<>();
        for (MessageFactory.Message m : messageList) {
            display_messsage.add(m.getMessage_text());
        }

        return ok(Json.toJson(display_messsage));
    }

    /**
     * svaes new message in DB
     * @param request has to include user_id in session and message_text and receiver name in body
     * @return Status OK if done correctly
     */
    public Result sendMessage(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        int uid = Integer.parseInt(request.session().get("uid").get());
        String text = request.body().asJson().get("message_text").asText();
        String receiver_name = (request.body().asJson().get("receiver_name").asText());
        UserFactory.User user = userFactory.getUserById(uid);
        UserFactory.User user_receiver = userFactory.getUserByName(receiver_name);

        messageFactory.create(timeStamp, user_receiver.getUser_id(), uid, text);
        return ok();
    }

    /**
     * gets messages of user and chosen friend to be shown in chat
     * @param request has to include user_id in session and friend's name in body
     * @return Status OK, if messages of user and chosen friend gets passed on as Json
     */
    public Result getChatHistory(Http.Request request){
        if (request.session().get("uid").isEmpty()) return redirect("/");

        int id = Integer.parseInt(request.session().get("uid").get());
        String receiver_name = (request.body().asJson().get("receiver_name").asText());
        UserFactory.User user_receiver = userFactory.getUserByName(receiver_name);
        List<MessageFactory.Message> messageList = messageFactory.get_User_User_Messages(id, user_receiver.getUser_id());

        HashMap<Integer, MessageFactory.Message> result = new HashMap<>();
        for (int i = 0; i < messageList.size(); i++) {
            MessageFactory.Message message = messageList.get(i);
            result.put(i, message);

        }
        return ok(Json.toJson(result));
    }

    /**
     * get user_id of user to know which user is the user_sender for positioning the messages in the chat
     * @param request has to include user_id in the session
     * @return Status OK, if user_id gets passed on as Json
     */
    public Result getUserId(Http.Request request) {
        if (request.session().get("uid").isEmpty()) return redirect("/");

        String uId = request.session().get("uid").get();
        return ok(Json.toJson(uId));
    }
}


