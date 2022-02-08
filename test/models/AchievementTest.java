package models;

import org.junit.Test;
import play.test.WithApplication;

import static org.junit.Assert.*;

import java.util.List;

public class AchievementTest extends WithApplication {
    AchievementFactory achievementFactory = provideApplication().injector().instanceOf(AchievementFactory.class);
    UserFactory userFactory = provideApplication().injector().instanceOf(UserFactory.class);

    @Test
    public void testGetAchievementByID() {
        final int SCORE_ID = 2;
        final String SCORE_NAME = "ScoreAchievement";
        AchievementFactory.Achievement achievement = achievementFactory.getAchievementById(SCORE_ID);

        assertNotNull(achievement);
        assertEquals(SCORE_NAME, achievement.getName());
    }

    @Test
    public void testUnlock() {
        UserFactory.User user = userFactory.create("Test", "password");
        final int SCORE_ID = 2;
        achievementFactory.unlock(user.getUser_id(), SCORE_ID);
        List<AchievementFactory.Achievement> userAchievements = achievementFactory.getAllUserAchievements(user.getUser_id());

        assertFalse(userAchievements.isEmpty());

        user.delete();
    }

    @Test
    public void testUserGetAchievementIfEmpty() {
        UserFactory.User user = userFactory.create("Test", "password");
        final int SCORE_ID = 2;
        achievementFactory.userGetAchievementIfEmpty(user.getUser_id(), SCORE_ID);
        List<AchievementFactory.Achievement> userAchievements = achievementFactory.getAllUserAchievements(user.getUser_id());

        assertFalse(userAchievements.isEmpty());

        user.delete();
    }
}
