package models;

import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Singleton
public class UserFactory {

    private Database db;

    @Inject
    UserFactory(Database db) {
        this.db = db;
    }

    /**
     * Authenticates a user with the given credentials
     *
     * @param username username from user input
     * @param password password from user input
     * @return Found user or null if user not found
     */
    public User authenticate(String username, String password) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE Username = ? AND Password = ?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Creates a new User with the given parameters
     * @param name name of the new user
     * @param password password of the new user
     * @return user
     */
    public User create(String name, String password) {
        return db.withConnection(conn -> {
            User user = null;
            String sql = "INSERT INTO `User` (Username, Score, Highscore, Password, GamesPlayed, Profilepicture_picture_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, name);
            stmt.setInt(2, 0);
            stmt.setInt(3, 0);
            stmt.setString(4, password);
            stmt.setInt(5, 0);
            stmt.setInt(6, 7);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int user_id = rs.getInt(1);
                user = new User(user_id, name, 0, 0, 0, 1);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Retrieves a user from database with given ID
     *
     * @param user_id user_id of user to find
     * @return User if found, else null
     */
    public User getUserById(int user_id) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE User_Id = ?");
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Gets the user with the name
     * @param name the name of the user
     * @return user
     */
    public User getUserByName(String name) {
        return db.withConnection(conn -> {
            User user = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User` WHERE Username = ?");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs);
            }
            stmt.close();
            return user;
        });
    }

    /**
     * Polymorphism method for getUserById(int)
     *
     * @param user_id String of user_id
     * @return User if found, else null
     */
    public User getUserById(String user_id) {
        return getUserById(Integer.parseInt(user_id));
    }

    /**
     * Gets a list of all users
     * @return list
     */
    public List<User> getAllUsers() {
        return db.withConnection(conn -> {
            List<User> users = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User`");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User(rs);
                users.add(user);
            }
            stmt.close();
            return users;
        });
    }

    public class User {
        private int user_id;
        private String username;
        private int score;
        private int highscore;
        private int gamesPlayed;
        private int picture_id;

        private User(int user_id, String username, int score, int highscore, int gamesPlayed, int picture_id) {
            this.user_id = user_id;
            this.username = username;
            this.score = score;
            this.highscore = highscore;
            this.gamesPlayed = gamesPlayed;
            this.picture_id = picture_id;

        }

        private User(ResultSet rs) throws SQLException {
            this.user_id = rs.getInt("User_Id");
            this.username = rs.getString("Username");
            this.score = rs.getInt("Score");
            this.highscore = rs.getInt("Highscore");
            this.gamesPlayed = rs.getInt("GamesPlayed");
            this.picture_id = rs.getInt("Profilepicture_picture_id");
        }

        /**
         * Updates the user if it already exists and creates it otherwise. Assumes an
         * autoincrement id column.
         */
        public void save() {
            db.withConnection(conn -> {
                String sql = "UPDATE `User` SET Username = ?, Score = ?, Highscore = ?, GamesPlayed = ?, Profilepicture_picture_id = ? WHERE User_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.username);
                stmt.setInt(2, this.score);
                stmt.setInt(3, this.highscore);
                stmt.setInt(4, this.gamesPlayed);
                stmt.setInt(5, this.picture_id);
                stmt.setInt(6, this.user_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * Delete the user from the database
         */
        public void delete() {
            db.withConnection(conn -> {
                String sql = "DELETE FROM `User` WHERE User_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, this.user_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * Adds a user the friends of the current user
         * @param friend_id id of the new friend
         */
        public void addFriend(int friend_id) {
            db.withConnection(conn -> {
                String sql = "INSERT INTO `User_has_User_as_Friend` (User_user_id, User_user_id1) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, this.user_id);
                stmt.setInt(2, friend_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * Gets a list of all friends to the current user and all the users which are friends with the current user
         * @return list
         */
        public List<User> getFriends() {
            return db.withConnection(conn -> {
                List<User> result = new ArrayList<>();
                String s = "SELECT * FROM User_has_User_as_Friend AS u_h_F INNER JOIN User AS u ON u.user_id = u_h_F.User_user_id OR u.user_id = u_h_F.User_user_id1 WHERE User_user_id = ? OR User_user_id1 = ?";
                PreparedStatement stmt = conn.prepareStatement(s);
                stmt.setInt(1, this.user_id);
                stmt.setInt(2, this.user_id);
                ResultSet rs = stmt.executeQuery();
                aussen:
                while (rs.next()) {
                    User user = new User(rs);
                    if (user.getUser_id() == this.user_id) {
                        continue;
                    } else {
                        for (int i = 0; i < result.size(); i++) {
                            User user1 = result.get(i);
                            if (user1.user_id == user.user_id) {
                                continue aussen;
                            }
                        }

                    }
                    result.add(user);
                }
                stmt.close();
                return result;
            });
        }


        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
            this.save();
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        /**
         * adds a new score to the current one
         * @param score score
         */
        public void addScore(int score) {
            this.score += score;
            this.save();
        }

        /**
         * increases the number of playedGames by one
         */
        public void updateGamesPlayed() {
            this.gamesPlayed++;
        }

        public int getGamesPlayed() {
            return gamesPlayed;
        }

        public int getHighscore() {
            return highscore;
        }

        public void setHighscore(int highscore) {
            this.highscore = highscore;
        }

        public int getPicture_id() {
            return picture_id;
        }

        public void setPicture_id(int picture_id) {
            this.picture_id = picture_id;
            this.save();
        }
    }


}