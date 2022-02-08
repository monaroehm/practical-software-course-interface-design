package models;

import play.db.Database;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Achievement factory.
 */
public class AchievementFactory {

    private Database db;

    /**
     * Instantiates a new Achievement factory.
     *
     * @param db the database
     */
    @Inject
    AchievementFactory(Database db) {
        this.db = db;
    }


    /**
     * Gets all user achievements as list.
     *
     * @param user_id the user id
     * @return all user achievements
     */
    public List<Achievement> getAllUserAchievements(int user_id) {
        return db.withConnection(conn -> {
            List<Achievement> achievements = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User_has_Achievement AS u_h_F JOIN Achievement AS achievement ON achievement.achievement_id = u_h_F.Achievement_achievement_id WHERE User_user_id = ?");
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            Achievement achievement = null;
            while (rs.next()) {
                achievement = createAchievement(rs);
                achievements.add(achievement);
            }
            stmt.close();
            return achievements;
        });
    }

    /**
     * Get an achievement by its id.
     *
     * @param achievement_id the achievement id
     * @return the achievement
     */
    public Achievement getAchievementById(int achievement_id){
        return db.withConnection(conn -> {
            Achievement achievement = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Achievement` WHERE Achievement_id = ?");
            stmt.setInt(1, achievement_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                achievement = createAchievement(rs);
            }
            stmt.close();
            return achievement;
        });
    }

    /**
     * Get all achievements that depend on data that's updated during the game.
     *
     * @return the achievement list
     */
    public List<Achievement> getAllGamesRelevantAchievements(){
         return db.withConnection(conn -> {
             List<Achievement> achievements = new ArrayList<>();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Achievement` WHERE NOT `Type` = ? ");
             stmt.setString(1, "friends");
             ResultSet rs = stmt.executeQuery();
             Achievement achievement = null;
             while (rs.next()) {
                 achievement = createAchievement(rs);
                 achievements.add(achievement);
             }
             stmt.close();
             return achievements;
         });
     }

    /**
     * Get achievements of type friend.
     *
     * @return the achievement list
     */
    public List<Achievement> getAllFriendAchievements(){
         return db.withConnection(conn -> {
             List<Achievement> achievements = new ArrayList<>();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Achievement` WHERE `Type` = ? ");
             stmt.setString(1, "friends");
             ResultSet rs = stmt.executeQuery();
             Achievement achievement = null;
             while (rs.next()) {
                 achievement = createAchievement(rs);
                 achievements.add(achievement);
             }
             stmt.close();
             return achievements;
         });
     }

    /**
     * Unlock an achievement for a user.
     *
     * @param user_id        the user id
     * @param achievement_id the achievement id
     * @return true
     */
    public boolean unlock(int user_id, int achievement_id) {
        return db.withConnection(conn -> {
            String sql = "INSERT INTO `User_has_Achievement` (User_user_id, Achievement_achievement_id) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user_id);
            stmt.setInt(2, achievement_id);
            stmt.executeUpdate();
            stmt.close();
            return true;
        });
    }

    /**
     * User unlocks the achievement if it is not already unlocked.
     *
     * @param user_id        the user id
     * @param achievement_id the achievement id
     * @return boolean
     */
    public boolean userGetAchievementIfEmpty(int user_id, int achievement_id) {
        List<Achievement> user_achievementList = getAllUserAchievements(user_id);
        for (Achievement currentAchievement : user_achievementList) {
            if (currentAchievement.getID() == achievement_id)
                return false;
        }
        return unlock(user_id, achievement_id);
    }

    /**
     * Create specific achievement based on the type.
     *
     * @param rs the result
     * @return the achievement
     * @throws SQLException the sql exception
     */
    public Achievement createAchievement(ResultSet rs) throws SQLException {
        switch (rs.getString("Type")) {
            case ("score"):
                return new ScoreAchievement(rs);
            case ("highscore"):
                return new HighscoreAchievement(rs);
            case ("gamesPlayed"):
                return new GamesPlayedAchievement(rs);
            case ("friends"):
                return new FriendsAchievement(rs);
            default:
                System.out.println("String ist kein Type!");
        }
        return null;
    }

    /**
     * The interface Achievement.
     */
    public interface Achievement {
        /**
         * Is achievement achieved boolean.
         *
         * @param relevantData the relevant data
         * @return the boolean
         */
        boolean isAchieved(int relevantData);

        /**
         * Gets achievement description.
         *
         * @return the description
         */
        String getDescription();

        /**
         * Gets achievement name.
         *
         * @return the name
         */
        String getName();

        /**
         * Gets achievement id.
         *
         * @return the id
         */
        int getID();

        /**
         * Gets achievement goal.
         *
         * @return the goal
         */
        int getGoal();
    }
}
