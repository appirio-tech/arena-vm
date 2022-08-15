package com.topcoder.netCommon.mpsqas;

/**
 * Constants related to problem difficulty.
 */
public class DifficultyConstants {

    /**Difficulty level ids*/
    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;
    public static final int[] DIFFICULTY_IDS = {1, 2, 3};
    public static final String[] DIFFICULTY_NAMES =
            {"Easy", "Medium", "Hard"};

    /**Division ids*/
    public static final int DIVISION_ONE = 1;
    public static final int DIVISION_TWO = 2;
    public static final int[] DIVISION_IDS = {1, 2};
    public static final String[] DIVISION_NAMES = {"Division 1", "Division 2"};

    /**
     * Returns the status level name associated with the specified
     * status id.
     *
     * @param statusId The status level id.
     */
    public static HiddenValue getDivisionName(int divId) {
        for (int i = 0; i < DIVISION_IDS.length; i++) {
            if (DIVISION_IDS[i] == divId) {
                return new HiddenValue(DIVISION_NAMES[i], divId);
            }
        }
        return new HiddenValue("Unknown", divId);
    }

    /**
     * Returns the difficulty level name associated with the specified
     * difficulty id.
     *
     * @param difficultyId The difficulty level id.
     */
    public static HiddenValue getDifficultyName(int difficultyId) {
        for (int i = 0; i < DIFFICULTY_IDS.length; i++) {
            if (DIFFICULTY_IDS[i] == difficultyId) {
                return new HiddenValue(DIFFICULTY_NAMES[i], difficultyId);
            }
        }
        return new HiddenValue("Unknown", difficultyId);
    }
}
