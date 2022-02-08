package models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Friends achievement.
 */
public class FriendsAchievement implements AchievementFactory.Achievement {
    private final String description;
    private final String name;
    private final int id;
    private final int goal;

    /**
     * Instantiates a new Friends achievement.
     *
     * @param rs the rs
     * @throws SQLException the sql exception
     */
    public FriendsAchievement(ResultSet rs) throws SQLException {
        this.description = rs.getString("Description");
        this.name = rs.getString("Name");
        this.id = rs.getInt("Achievement_id");
        this.goal = rs.getInt("Goal");
    }

    @Override
    public boolean isAchieved(int relevantData) {
        return relevantData > goal;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int getGoal() {
        return goal;
    }
}

