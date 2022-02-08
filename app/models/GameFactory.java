package models;

import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;

/**
 * The Game factory.
 */
@Singleton
public class GameFactory {

    private Database db;

    /**
     * Instantiates a new Game factory.
     *
     * @param database the database
     */
    @Inject
    GameFactory(Database database) {
        db = database;
    }

    /**
     * Create a game data.
     *
     * @param timeStamp the time stamp
     * @param duration  the duration
     * @param score     the score
     * @param uid       the uid
     */
    public void createGame(String timeStamp, int duration, int score, int uid) {
        db.withConnection(conn -> {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `played_Games` (time, duration, score, User_user_id) VALUES (?, ?, ?, ?)");
            stmt.setString(1, timeStamp);
            stmt.setInt(2, duration);
            stmt.setInt(3, score);
            stmt.setInt(4, uid);
            stmt.executeUpdate();
            stmt.close();
        });
    }
}
