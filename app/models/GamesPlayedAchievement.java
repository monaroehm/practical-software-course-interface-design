package models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GamesPlayedAchievement implements AchievementFactory.Achievement {
    private final String description;
    private final String name;
    private final int id;
    private final int goal;

    public GamesPlayedAchievement(ResultSet rs) throws SQLException {
        this.description = rs.getString("Description");
        this.name = rs.getString("Name");
        this.id = rs.getInt("Achievement_id");
        this.goal = rs.getInt("Goal");
    }
    /**
     * checks if the goal the achievement is reached
     * @param relevantData number of played games
     * @return true, if the goal is reached else false
     */
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
