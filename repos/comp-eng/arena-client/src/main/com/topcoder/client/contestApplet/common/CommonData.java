/*
* Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.client.contestApplet.common;

//import com.topcoder.client.contestApplet.ContestApplet;

import java.lang.reflect.Field;
import java.util.Properties;

import com.topcoder.client.contestant.RoomModel;
import com.topcoder.client.contestant.RoundModel;

/*
* CommonData.java
*
* Created on July 10, 2000, 4:08 PM
*/

/**
 * This class will hold the common data used to build the applet components (menus/tables/buttons).
 *
 * <p>
 * Changes in version 1.1 (SRM Arena Update For Python Enabling - BUGR-9310):
 * <ol>
 *      <li>Update {@link #TopCoderAllows} field to enable Python.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Move Arena Applet Version info into Configuration File v1.0):
 * <ol>
 *      <li>Add {@link #VERSIONS} field.</li>
 *      <li>Add {@link #VERSION_PROP_FILE} field.</li>
 *      <li>Add {@link #getVersionProperties()} method.</li>
 *      <li>Update {@link #CURRENT_VERSION} field.</li>
 *      <li>Update {@link #UPDATE_DATE} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Python3 Support):
 * <ol>
 *      <li>Added {@link #PYTHON3} field.</li>
 *      <li>Added {@link #allowsPython3(String)} method.</li>
 *      <li>Update {@link #OracleAllows}, {@link #TopCoderAllows}, {@link #TopCoderHSAllows},  {@link #GoogleAllows},
 *       {@link #VerizonAllows}, {@link #SunAllows}, {@link #SunPracticeAllows}, {@link #SunOnsiteAllows}, {@link #SunOnsiteFinalsAllows},
 *       {@link #NvidiaAllows}, {@link #CSFBAllows}, {@link #DblClkAllows}, {@link #VeriSignAllows} fields.</li>
 * </ol>
 * </p>
 * @author  aroman, savon_cn, liuliquan
 * @version 1.3
 */
public final class CommonData {
    /**
     * Language mappings for the CodingFrame
     *
     * NOTE: the ordering of the types is important for the search and replace
     */
    public static final String[][] dataTypeMap = {
        {"int[]", "vector<int>", "int[]"},
        {"long[]", "vector<long>", "long[]"},
        {"double[]", "vector<double>", "double[]"},
        //{ "String[]", "vector<char*>" },
        {"String[]", "vector<string>", "string[]"},
        {"long", "long long", "long"},
        {"boolean", "bool", "bool"},
        //{ "String", "char*" } };
        {"String", "string", "string"}};

    /**
     * Contest table header information (Important for determining the amount of columns in the table
     * when it is created, and the column titles if they are to be displayed)
     */
    //public static final String [] contestInfoHeader = { "key", "value" };
    //public static final String [] roundHeader = { "Description", "Start Date" };
    //public static final String [] groupHeader = { "Level" };
    //public static final String [] contestHeader = { "Contest Name" };
    public static final String[] matrixHeader = {"0", "1"};
    //public static final String [] resultsHeader = { "Place", "Handle", "Status", "Time", "Submissions", "Failures" };
    public static final String[] contestantHeader = {"R", "Handle"};
    public static final String[] teamContestantHeader = {"R", "Team Name"};
    public static final String[] userHeader = {"R", "Handle"};
    public static final String[] registrantsHeader = {"R", "Handle", "Country"};
    public static final String[] hsRegistrantsHeader = {"R", "Handle", "Country", "School"};
    //public static final String [] paramHeader = { "Position", "Data Type", "Data Value" };
    public static final String[] historyHeader = {"Summary", "Points"};
    //public static final String [] infoHeader = { "Stat", "Value" };
//    public static final String [] roomInfoHeader = ;
    //public static final String [] teamInfoHeader = { "Captain", "Team", "Available", "Members", "Status" };
    public static final String[] teamInfoHeader = {"Team", "Captain", "Available", "Members", "Status"};
    /**
     * The version properties
     * @since 1.2
     */
    public static final Properties VERSIONS = getVersionProperties();
    /**
     * The version property file name
     * @since 1.2
     */
    private static final String VERSION_PROP_FILE = "version.properties";
    /**
     * Default table information (changes on room updates/loads)
     */
    /*
    */

    // Sponsor image url's
    private static final String BASE = getImageBaseURL();
    private static final String SPONSORLOGIN = BASE + "AppletLogin.png";
    private static final String SPONSORLOBBY = BASE + "AppletLobby.png";
    private static final String SPONSORCODINGFRAME = BASE + "AppletCodingFrame.png";
    private static final String SPONSORSCOREBOARD = BASE + "AppletScoreBoard.png";
    private static final String SPONSORWATCHROOM = BASE + "AppletWatchRoom.png";
    private static final String ROUND_SUFFIX = "&rd=";
    private static final boolean useRoundSuffix = getUseRoundSuffix();

    public static String getSponsorLoginImageAddr(int companyID) {
        return SPONSORLOGIN + companyID;
    }

    private static boolean getUseRoundSuffix() {
        try {
            return Boolean.valueOf(System.getProperty("com.topcoder.client.images.useRoundSuffix", "true")).booleanValue();
        } catch (Exception e) {
            return true;
        }
    }

    private static String getImageBaseURL() {
        return System.getProperty("com.topcoder.client.images.baseURL" , "http://community.topcoder.com/contest/sponsor/");
    }

    public static String getSponsorLobbyImageAddr(int companyID) {
        return SPONSORLOBBY;
    }

    public static String getSponsorCodingFrameImageAddr(int companyID) {
        return SPONSORCODINGFRAME;
    }

    public static String getSponsorScoreBoardImageAddr(int companyID) {
        return SPONSORSCOREBOARD;
    }

    public static String getSponsorWatchRoomImageAddr(int companyID) {
        return SPONSORWATCHROOM;
    }

    public static String getSponsorLoginImageAddr(String companyName) {
        return SPONSORLOGIN;
    }

    public static String getSponsorLobbyImageAddr(String companyName, RoomModel model) {
        return SPONSORLOBBY;
    }

    public static String getSponsorCodingFrameImageAddr(String companyName, RoomModel model) {
        return SPONSORCODINGFRAME;
    }

    public static String getSponsorWatchRoomImageAddr(String companyName, RoomModel model) {
        return SPONSORWATCHROOM;
    }

    public static String getSponsorScoreBoardImageAddr(String companyName, RoundModel model) {
        return SPONSORSCOREBOARD;
    }

    private static String buildRoundSuffix(RoomModel model) {
        if (model!= null) {
            return buildRoundSuffix(model.getRoundModel());
        }
        return "";
    }

    private static String buildRoundSuffix(RoundModel model) {
        if (model != null && useRoundSuffix) {
            return ROUND_SUFFIX+model.getRoundID();
        }
        return "";
    }

    //FIXME this is really ugly, we must refactor it
    private static final String[] companyNames = {"Oracle","TopCoder","Google","TopCoderHS", "Nvidia", "CSFB", "Verizon", "DblClk","VeriSign","TCO05Sponsor","TCO07Sponsor", "TCCC07Sponsor"};
    private static final int[] companyIDs = {26,1, 1865, 41, 38, 44, 1747, 2998, 17942 /*11629*/, 4, 5, 6};
    public static int companyNameToID(String name){
        for(int i = 0; i<companyNames.length;i++){
            if(companyNames[i].equalsIgnoreCase(name))return companyIDs[i];
        }
        return -1;
    }

    public static String companyIDToName(int id) {
        for (int i = 0; i < companyIDs.length; i++) {
            if (companyIDs[i] == id) return companyNames[i];
        }
        return null;
    }

    
    public static final boolean[] OracleAllows = {true,false,false,false,false,false};
    public static final boolean[] TopCoderAllows = {true,true,true,true,true,true};
    public static final boolean[] TopCoderHSAllows = {true,true,true,true,true,true};
    public static final boolean[] GoogleAllows = {true,true,true,true,true,true};
    public static final boolean[] VerizonAllows = {true,true,true,true,true,true};
    public static final boolean[] SunAllows = {true,false,false,false,false,false};
    public static final boolean[] SunPracticeAllows = {true,false,false,false,false,false};
    public static final boolean[] SunOnsiteAllows = {true,false,false,false,false,false};
    public static final boolean[] SunOnsiteFinalsAllows = {true,false,false,false,false,false};
    public static final boolean[] NvidiaAllows = {true,true,true,true,true,true};
    public static final boolean[] CSFBAllows = {true,true,true,true,true,true};
    public static final boolean[] DblClkAllows = {true,true,true,true,true,true};
    public static final boolean[] VeriSignAllows = {true,true,true,true,true,true};

    private static final int JAVA = 0;
    private static final int CPP = 1;
    private static final int CS = 2;
    private static final int VB = 3;
    private static final int PYTHON = 4;
    private static final int PYTHON3 = 5;

    private static boolean allows(String companyName, int index) {
        try {
            Field field = Class.forName("com.topcoder.client.contestApplet.common.CommonData").getField(companyName + "Allows");
            boolean[] allows = (boolean[]) field.get(null);
            return allows[index];
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static boolean allowsJava(String companyName) {
        return allows(companyName, JAVA);
    }

    public static boolean allowsCPP(String companyName) {
        return allows(companyName, CPP);
    }

    public static boolean allowsCS(String companyName) {
        return allows(companyName, CS);
    }

    public static boolean allowsVB(String companyName) {
        return allows(companyName, VB);
    }
    
    public static boolean allowsPython(String companyName) {
        return allows(companyName, PYTHON);
    }

    public static boolean allowsPython3(String companyName) {
        return allows(companyName, PYTHON3);
    }
    /**
     * The current version of applet
     */
    public static final String CURRENT_VERSION = VERSIONS.getProperty("currentVersion", "N/A");
    /**
     * The latest updated applet version
     */
    public static final String UPDATE_DATE = VERSIONS.getProperty("updateDate","N/A");
    
    public static int[] parseVersion(String version) {
        String[] r = version.split("[\\.-]");
        int[] ret = new int[r.length];
        for(int i = 0; i < r.length; i++) {
            ret[i] = Integer.parseInt(r[i]);
        }
        return ret;
    }

    public static boolean isVersionCurrent(String newVersion) {
        return isVersionCurrent(parseVersion(CURRENT_VERSION), parseVersion(newVersion));
    }

    public static boolean isVersionCurrent(int[] currentVersion, int[] newVersion) {
        for(int i = 0; i < currentVersion.length; i++) {
            if(i >= newVersion.length)
                return true;
            
            if(currentVersion[i] > newVersion[i])
                return true;
            if(currentVersion[i] < newVersion[i])
                return false;
        }
        if(currentVersion.length < newVersion.length)
            return false;
        
        return true;
    }

    public static boolean showSystemTestsPerCoder(String companyName) {
        return "Showdown".equals(companyName); 
    }
    /**
     * Get the arena applet version properties
     * @return the properties entity.
     * @since 1.2
     */
    private static Properties getVersionProperties() {
        Properties prop = new Properties();
        try {
            prop.load(CommonData.class.getClassLoader().getResourceAsStream(VERSION_PROP_FILE));
        } catch (Exception e) {
            System.err.println("Error loading " + VERSION_PROP_FILE);
            e.printStackTrace();
        }
        return prop;
    }
}
