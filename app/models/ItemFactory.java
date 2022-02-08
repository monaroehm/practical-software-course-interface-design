package models;

import play.db.Database;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemFactory {
    private Database db;

    @Inject
    ItemFactory(Database db) {
        this.db = db;
    }

    /**
     * Gets an item by its id
     * @param item_id the itemd id
     * @return item
     */
    public Item getItemById(int item_id) {
        return db.withConnection(conn -> {
            Item item = null;
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Item` WHERE Item_Id = ?");
            stmt.setInt(1, item_id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                item = new Item(rs);
            }
            stmt.close();
            return item;
        });
    }
    /**
     * Gets a list of all items
     * @return list
     */


    public List<Item> getAllItems() {
        return db.withConnection(conn -> {
            List<Item> items = new ArrayList<>();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `Item`");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Item item = new Item(rs);
                items.add(item);
            }
            stmt.close();
            return items;
        });
    }
    /**
     * calls getItemById(int item_id)
     * @param item_id the item id as String
     * @return item
     */
    public Item getItemById(String item_id) {
        return getItemById(Integer.parseInt(item_id));
    }

    public class Item {
        private int item_id;
        private String name;
        private String description;
        private String path;

        private Item(ResultSet rs) throws SQLException {
            this.item_id = rs.getInt("item_id");
            this.description = rs.getString("description");
            this.name = rs.getString("name");
            this.path = rs.getString("path");
        }

        /**
         * updates the item
         */
        public void save() {
            db.withConnection(conn -> {
                String sql = "UPDATE `Item` SET `Name` = ?, Description = ?, `Path` = ? WHERE Item_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, this.name);
                stmt.setString(2, this.description);
                stmt.setString(3, this.path);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * deletes the item
         */
        public void delete() {
            db.withConnection(conn -> {
                String sql = "DELETE FROM `Item` WHERE Item_Id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, this.item_id);
                stmt.executeUpdate();
                stmt.close();
            });
        }

        /**
         * Gets item id.
         *
         * @return the id
         */
        public int getItem_id() {
            return item_id;
        }

        /**
         * Sets a new item id
         * @param item_id new item id
         */
        public void setItem_id(int item_id) {
            this.item_id = item_id;
        }

        /**
         * Gets item name
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Sets a new name
         * @param name new name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Gets item description
         * @return item description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Changes the description of an item
         * @param description new description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Gets the path of the item icon
         * @return path
         */
        public String getPath() {
            return path;
        }

        /**
         * Changes the path of the item icon
         * @param path new path
         */
        public void setPath(String path) {
            this.path = path;
        }
    }
}
