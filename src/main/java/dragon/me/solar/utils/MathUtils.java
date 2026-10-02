package dragon.me.solar.utils;

import dragon.me.solar.Solar;

public final class MathUtils {

    public static double expectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - rating) / 400.0));
    }

    public static int calculateNewRating(int rating, int opponentRating, double actualScore) {
        double expected = 1.0 / (1.0 + Math.pow(10.0, (opponentRating - rating) / 400.0));

        return (int) Math.round(
                rating + Solar.configManager.settingsRecord().ratingSettings().kFactor() * (actualScore - expected));
    }

    public static int calculateNewRating(int playerRating, int teamRating, int opponentTeamRating, double actualScore) {
        double expectedScore = expectedScore(teamRating, opponentTeamRating);

        return (int) Math.round(playerRating
                + Solar.configManager.settingsRecord().ratingSettings().kFactor() * (actualScore - expectedScore));
    }
}
