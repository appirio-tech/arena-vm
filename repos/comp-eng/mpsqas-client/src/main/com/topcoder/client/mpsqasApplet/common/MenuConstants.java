package com.topcoder.client.mpsqasApplet.common;

/**
 * Class of constants related to the default menu model / structure.
 *
 * @author mitalub
 */
public class MenuConstants {

    public final static int FOYER = 1,
    EXIT = 2,
    MAIN_INDIVIDUAL_PROBLEM_ROOM = 3,
    MAIN_TEAM_PROBLEM_ROOM = 4,
    MAIN_LONG_PROBLEM_ROOM = 5,
    UPCOMING_CONTESTS = 6,
    PROBLEM_WRITER_APPLICATION = 7,
    PROBLEM_TESTER_APPLICATION = 8,
    PENDING_PROBLEMS = 9,
    PENDING_APPLICATIONS = 10,
    ALL_PROBLEMS = 11,
    USERS = 12,
    CONTENTS = 13,
    CHANGE_LOG = 14,
    ABOUT = 15,
    PENDING_TEAM_PROBLEMS = 16,
    ALL_TEAM_PROBLEMS = 17,
    PENDING_LONG_PROBLEMS = 18,
    ALL_LONG_PROBLEMS = 19;

    public static String[] MENU_HEADERS =
            {"Main", "Problems", "Calendar", "Applications", "Help"};

    public static String[][] MENU_ITEMS =
            {{"Foyer", "Exit"},
             {"Main Individual Problem Room", "Main Team Problem Room", "Main Long Problem Room"},
             {"Upcoming Contests"},
             {"Problem Writer Application", "Problem Tester Application"},
             {"Contents", "Change Log", "About"}};

    public static int[][] MENU_IDS =
            {{FOYER, EXIT},
             {MAIN_INDIVIDUAL_PROBLEM_ROOM, MAIN_TEAM_PROBLEM_ROOM, MAIN_LONG_PROBLEM_ROOM},
             {UPCOMING_CONTESTS},
             {PROBLEM_WRITER_APPLICATION, PROBLEM_TESTER_APPLICATION},
             {CONTENTS, CHANGE_LOG, ABOUT}};

    public static String[] A_MENU_HEADERS =
            {"Main", "Problems", "Calendar", "Applications", "Admin", "Help"};

    public static String[][] A_MENU_ITEMS =
            {{"Foyer", "Exit"},
             {"Main Individual Problem Room", "Main Team Problem Room", "Main Long Problem Room"},
             {"Upcoming Contests"},
             {"Problem Writer Application", "Problem Tester Application"},
             {"Pending Single Problems", "Pending Team Problems", "Pending Long Problems",
              "Pending Applications", "Users"},
             {"Contents", "Change Log", "About"}};

    public static int[][] A_MENU_IDS =
            {{FOYER, EXIT},
             {MAIN_INDIVIDUAL_PROBLEM_ROOM, MAIN_TEAM_PROBLEM_ROOM, MAIN_LONG_PROBLEM_ROOM},
             {UPCOMING_CONTESTS},
             {PROBLEM_WRITER_APPLICATION, PROBLEM_TESTER_APPLICATION},
             {PENDING_PROBLEMS, PENDING_TEAM_PROBLEMS, PENDING_LONG_PROBLEMS,
              PENDING_APPLICATIONS, USERS},
             {CONTENTS, CHANGE_LOG, ABOUT}};
}
