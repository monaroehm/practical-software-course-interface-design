package models;

import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class MessageFactory {
    private Database db;

    @Inject
    MessageFactory(Database database) {
        db = database;
    }

    /**
     * Gets a list of all messages which were sent or received by the current user
     * @param user_id id of the user
     * @return list
     */
    public List<Message> getAllMessages(int user_id) {
        return db.withConnection(conn -> {
            List<Message> messagelist = new ArrayList<>();
            String s = "SELECT * FROM User_User_Message  INNER JOIN User AS u ON u.user_id = User_User_Message.user_id_sender OR u.user_id = User_User_Message.user_id_receiver WHERE u.user_id =? order by time asc";
            PreparedStatement stmt = conn.prepareStatement(s);
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            Message message = null;
            while (rs.next()) {
                message = new Message(rs);
                messagelist.add(message);
            }
            stmt.close();
            return messagelist;
        });
    }

    /**
     * Gets a list of all messages which were sent between two specific users
     *
     * @param user_id id of the current user
     * @param user_id_friend id of a specific user
     * @return list
     */
    public List<Message> get_User_User_Messages(int user_id, int user_id_friend) {
        return db.withConnection(conn -> {
            List<Message> messagelist = new ArrayList<>();
            String so = "SELECT * FROM (SELECT * FROM User_User_Message  INNER JOIN User AS u ON u.user_id = User_User_Message.user_id_sender OR u.user_id = User_User_Message.user_id_receiver WHERE u.user_id =? )tab INNER JOIN User AS us ON us.user_id = tab.user_id_sender OR us.user_id = tab.user_id_receiver WHERE us.user_id =? order by time asc ";
            PreparedStatement stmt = conn.prepareStatement(so);
            stmt.setInt(1, user_id);
            stmt.setInt(2, user_id_friend);
            ResultSet rs = stmt.executeQuery();
            Message message = null;
            while (rs.next()) {
                message = new Message(rs);
                messagelist.add(message);
            }
            stmt.close();
            return messagelist;
        });
    }

    public static class Message {
        private String time;
        private int user_id_sender;
        private int user_id_receiver;
        private String message_text;
        private int message_id;

        private Message(ResultSet rs) throws SQLException {
            this.time = rs.getString("time");
            this.user_id_sender = rs.getInt("user_id_sender");
            this.user_id_receiver = rs.getInt("user_id_receiver");
            this.message_text = rs.getString("message_text");
            this.message_id = rs.getInt("message_id");
        }

        public int getUser_id_sender() {
            return user_id_sender;
        }

        public int getUser_id_receiver() {
            return user_id_receiver;
        }

        public String getTime() {
            return time;
        }

        public String getMessage_text() {
            return message_text;
        }

        public int getMessage_id() {
            return message_id;
        }
    }

    /**
     * Creates a new message with the given parameters
     * @param timeStamp time
     * @param user_id_receiver id of the revceiver
     * @param user_id_sender id of the sender
     * @param message_text text of the message
     */
    public void create(String timeStamp, int user_id_receiver, int user_id_sender, String message_text) {
        db.withConnection(conn -> {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `User_User_Message`(time, message_text, user_id_sender,user_id_receiver) VALUES (?,?,?,?)");
            stmt.setString(1, timeStamp);
            stmt.setString(2, message_text);
            stmt.setInt(3, user_id_sender);
            stmt.setInt(4, user_id_receiver);
            stmt.executeUpdate();
            stmt.close();
        });
    }


}
