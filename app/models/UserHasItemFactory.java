package models;

import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class UserHasItemFactory {

    private Database db;

    @Inject
    UserHasItemFactory(Database db) {
        this.db = db;
    }

    /**
     * Gets a list of all user items
     * @param user_id id of the user id
     * @return list
     */
    public List<User_Has_Item> getUserHasItemForUser(int user_id) {
        return db.withConnection(conn -> {
            List<User_Has_Item> userHasItems = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `User_has_Item` AS u_h_i JOIN `Item` AS i ON u_h_i.Item_item_id = i.item_id WHERE User_user_id = ?");
            stmt.setInt(1, user_id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User_Has_Item userHasItem = new User_Has_Item(rs);
                userHasItems.add(userHasItem);
            }
            stmt.close();
            return userHasItems;
        });
    }

    /**
     * Calls getUserHasItemForUser(int user_id)
     * @param user_id user id as stirng
     * @return list
     */
    public List<User_Has_Item> getUserHasItemForUser(String user_id) {
        return getUserHasItemForUser(Integer.parseInt(user_id));
    }

    /**
     * creates a new item of a user
     * @param item_id id of the item
     * @param user_id id of the user
     * @param itemName name of the item
     * @param amount amount of the item
     * @return User_Has_Item Objekt
     */
    public User_Has_Item create(int item_id, int user_id, String itemName, int amount) {
        return db.withConnection(conn -> {
            User_Has_Item user_has_item = null;
            String sql = "INSERT INTO `User_has_Item` (User_user_id, Item_item_id, anzahl) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, user_id);
            stmt.setInt(2, item_id);
            stmt.setInt(3, amount);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                user_has_item = new User_Has_Item(rs.getInt("User_user_id"), rs.getInt("Item_item_id"), itemName, amount);
            }
            stmt.close();
            return user_has_item;
        });
    }


    public class User_Has_Item {
        private int user_id;
        private int item_id;
        private String item_name;
        private int amount;

        private User_Has_Item(int user_id, int item_id, String item_name, int amount) {
            this.user_id = user_id;
            this.item_id = item_id;
            this.item_name = item_name;
            this.amount = amount;
        }

        private User_Has_Item(ResultSet rs) throws SQLException {
            this.user_id = rs.getInt("User_User_Id");
            this.item_id = rs.getInt("Item_Item_Id");
            this.item_name = rs.getString("name");
            this.amount = rs.getInt("anzahl");
        }

        /**
         * updates user_has_item
         */
        public void save() {
            db.withConnection(conn -> {
                String sql = "UPDATE `User_has_Item` SET anzahl = ? WHERE User_user_Id = ? AND Item_item_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, this.amount);
                stmt.setInt(2, this.user_id);
                stmt.setInt(3, this.item_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * deletes user_has_item
         */
        public void delete() {
            db.withConnection(conn -> {
                String sql = "DELETE FROM `User_has_Item` WHERE User_user_Id = ? AND Item_item_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, this.user_id);
                stmt.setInt(2, this.item_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getUser_id() {
            return user_id;
        }

        public int getItem_id() {
            return item_id;
        }

        public String getItem_name() {
            return item_name;
        }
    }
}
