package com.topcoder.server.AdminListener;

import java.util.HashMap;

import com.topcoder.security.policy.GenericPermission;
import com.topcoder.security.policy.TCPermission;

/**
 * This class was modified for AdminTool 2.0 by adding new methods to handle
 * the conversion of function ids to TCPermission objects. A private map
 * is used to store the permission string to function id mapping.
 *
 * <p>A new constant REQUEST_NEW_ROUND_ID added to represent newly defined
 * GetNewIDRequest.
 * <p>A new constant REQUEST_SECURITY_MANAGEMENT added to represent newly defined
 * SecurityManagementRequest.
 * <p>A new constant GROUP_PRINCIPALS added to represent newly defined
 * GetPrincipalsRequest.
 * <p>A new constant ROLE_PRINCIPALS added to represent newly defined
 * GetPrincipalsRequest.
 *
 * @author TCDESIGNER
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class AdminConstants {

    public static final int RESTART_TESTERS_IMMEDIATELY = 1;
    public static final int RESTART_TESTERS_NORMAL = 2;
    public static final int RESTART_TESTERS_REFERENCE = 3;

    // Anonymous (no certificates) using Diffie-Hellman key exchange to
    // negotiate a shared secret key, RC4 for data encryption, and MD5
    // for the hash function
    public static final String SSL_CIPHER = "SSL_DH_anon_WITH_RC4_128_MD5";
    public static final int BACK_END_CONNECTION_ID = 3;
    public static final int CONTEST_LISTENER_CONNECTION_ID = 5;
    public static final int FIRST_CLIENT_CONNECTION_ID = 10;
    public static final int ADMIN_LISTENER_DEFAULT_PORT = 6000;

    public static final int RECIPIENT_ALL = -100;

    // User status
    public static final String INACTIVE_USER_STATUS = "I";

    // TC staff group ID in group_user table
    public static final int TC_STAFF_GROUP = 13;

    // How long to wait for blob column response
    public static final int BLOB_COLUMN_TIMEOUT = 20;
    public static final int TEXT_COLUMN_TIMEOUT = 20;

    // Practice room constants
    public static final int PRACTICE_ROUND_START_ID = 1000;
    public static final int PRACTICE_ROUND_END_ID = 1999;
    public static final String PRACTICE_CONTEST_STATUS = "A";
    public static final String PRACTICE_ROUND_NAME = "Practice";
    public static final String PRACTICE_ROUND_STATUS = "A";

    // Constants for request types.  Used in security check.
    // These need to be kept in sync with the values in the DB tables
    // monitor_function_type_lu and monitor_function.
    // See refreshSecurityTables.sql in this directory.

    // Function types
    public static final int ROUND_INDEPENDENT_FUNCTION = 2;

    // File commands
    public static final int REQUEST_BLOB_SEARCH = 5;
    public static final int REQUEST_OBJECT_LOAD = 10;
    public static final int REQUEST_TEXT_LOAD = 15;
    public static final int REQUEST_TEXT_SEARCH = 20;

    // Run contest commands
    public static final int REQUEST_DISABLE_ROUND = 200;
    public static final int REQUEST_ENABLE_ROUND = 205;
    public static final int REQUEST_REFRESH_ROUND = 210;
    public static final int REQUEST_RESTORE_ROUND = 215;

    // Before contest
    public static final int REQUEST_REGISTER_USER = 400;
    public static final int REQUEST_UNREGISTER_USER = 405;
    public static final int REQUEST_REFRESH_PROBLEMS = 410;
    public static final int REQUEST_REFRESH_REGISTRATION = 415;
    public static final int REQUEST_REFRESH_ROOM = 420;
    public static final int REQUEST_REFRESH_ALL_ROOMS = 421;
    public static final int REQUEST_REFRESH_ROOM_LISTS = 425;
    public static final int REQUEST_ASSIGN_ROOMS = 430;
//    public static final int REQUEST_SET_SPECTATOR_ROOM = 435;

    public static final int REQUEST_FORWARD_ROUND = 440;
    public static final int REQUEST_SHOW_SPEC_RESULTS = 445;

    // During contest
    public static final int REQUEST_ADD_TIME = 600;
    public static final int REQUEST_ADVANCE_CONTEST_PHASE = 610;
    public static final int REQUEST_REFRESH_BROADCASTS = 620;
    public static final int REQUEST_CREATE_SYSTEM_TESTS = 625;
    public static final int REQUEST_CONSOLIDATE_TEST_CASES = 630;
    public static final int REQUEST_CLEAR_TEST_CASES = 635;
    public static final int REQUEST_SYSTEM_TEST = 640;
    public static final int REQUEST_CANCEL_SYSTEM_TEST_CASE = 645;

    // After contest
    public static final int REQUEST_END_CONTEST = 800;
    public static final int REQUEST_ALLOCATE_PRIZES = 805;
    public static final int REQUEST_RUN_RATINGS = 810;
    public static final int REQUEST_INSERT_PRACTICE_ROOM = 815;
    public static final int REQUEST_ANNOUNCE_ADVANCING_CODERS = 816;
    public static final int REQUEST_ADVANCE_WL_CODERS = 817;

    public static final int REQUEST_CLEAR_CACHE = 818;
    public static final int REQUEST_GENERATE_TEMPLATE = 819;

    /**
     * Backup tables request id
     * @see com.topcoder.server.AdminListener.request.BackupTablesRequest
     */
    public static final int REQUEST_BACKUP_TABLES = 21703;

    /**
     * Restore tables request id
     * @see com.topcoder.server.AdminListener.request.RestoreTablesRequest
     */
    public static final int REQUEST_RESTORE_TABLES = 21705;


    // Contest server commands
    public static final int REQUEST_DISCONNECT_CLIENT = 1000;
    public static final int REQUEST_LOAD_ROUND = 1005;
    public static final int REQUEST_UNLOAD_ROUND = 1006;
    public static final int REQUEST_SHUTDOWN = 1010;
    public static final int REQUEST_GARBAGE_COLLECTION = 1015;
    public static final int REQUEST_START_REPLAY_LISTENER = 1020;
    public static final int REQUEST_START_REPLAY_RECEIVER = 1025;
    public static final int REQUEST_FORWARDING = 1027;
    public static final int REQUEST_ADMIN_FORWARDING = 1028;
    public static final int REQUEST_SPEC_APP_START_ROTATE = 1030;
    public static final int REQUEST_SPEC_APP_STOP_ROTATE = 1035;
    public static final int REQUEST_SPEC_APP_SHOW_ROOM = 1040;
    public static final int REQUEST_RESTART_EVENT_TOPIC_LISTENER = 1045;

    // Cached object commands
    public static final int REQUEST_CACHED_CODER = 1200;
    public static final int REQUEST_CACHED_CODER_PROBLEM = 1205;
    public static final int REQUEST_CACHED_PROBLEM = 1210;
    public static final int REQUEST_CACHED_REGISTRATION = 1215;
    public static final int REQUEST_CACHED_ROOM = 1220;
    public static final int REQUEST_CACHED_ROUND = 1225;
    public static final int REQUEST_CACHED_USER = 1230;

    // Actions commands
    public static final int REQUEST_BAN_IP = 1400;
    public static final int REQUEST_GRANT_ADMIN_AUTHORITY = 1405;
    public static final int REQUEST_REVOKE_ADMIN_AUTHORITY = 1410;
    public static final int REQUEST_SET_USER_STATUS = 1415;
    public static final int REQUEST_CONTEST_MANAGEMENT = 1420;
    public static final int REQUEST_MODERATED_CHAT = 1425;
    public static final int REQUEST_LOGGING = 1430;
    public static final int REQUEST_CLEAR_PRACTICE_ROOMS = 1450;
    public static final int REQUEST_RECALCULATE_SCORE = 1440;
    public static final int REQUEST_IMPORTANT_MESSAGES = 1460;
    public static final int REQUEST_BOOT_USER = 1465;

    // Broadcast commands
    public static final int REQUEST_SEND_GLOBAL_BROADCAST = 1600;
    public static final int REQUEST_SEND_COMPONENT_BROADCAST = 1605;
    public static final int REQUEST_SEND_ROUND_BROADCAST = 1610;

    // Functions not initiated by monitor client
    public static final int REQUEST_CHAT_VIEW = 2000;

    /**
     * The constant representing <code>SetRoundTermsRequest</code>
     *
     * @since Admin Tool 2.0
     */
    public static final int REQUEST_SET_ROUND_TERMS = 21658;

    /**
     * An int constant representing the request to restart the compilers.
     *
     * @since Admin Tool 2.0
     */
    public final static int REQUEST_RESTART_COMPILERS = 21700;

    /**
     * An int constant representing the request to restart the testers.
     *
     * @since Admin Tool 2.0
     */
    public final static int REQUEST_RESTART_TESTERS = 21701;

    /**
     * An int constant representing the request to restart both the compilers
     * and testers.
     *
     * @since Admin Tool 2.0
     */
    public final static int REQUEST_RESTART_ALL = 21702;

    /**
     * Possible choices for tables to backup
     * @see com.topcoder.server.AdminListener.request.BackupTablesRequest
     */
    public static final String[] TABLES_TO_BACKUP = {
        //"rating",
        "challenge",
        "component_state",
        "compilation",
        "submission",
        "room_result",
        //"system_test_result",
        "tc_algo_rating",
        "tchs_algo_rating",
        "mm_algo_rating",
    };


    /**
     * Warehouse load aggregate request id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadAggregate
     */
    public static final int REQUEST_WAREHOUSE_LOAD_AGGREGATE = 21650;

    /**
     * Warehouse load coders request id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadCoders
     */
    public static final int REQUEST_WAREHOUSE_LOAD_CODER = 21651;

    /**
     * Warehouse load empty request id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadEmpty
     */
    public static final int REQUEST_WAREHOUSE_LOAD_EMPTY = 21652;

    /**
     * Warehouse load rank request id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadRank
     */
    public static final int REQUEST_WAREHOUSE_LOAD_RANK = 21653;

    /**
     * Warehouse load requests request id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadRequests
     */
    public static final int REQUEST_WAREHOUSE_LOAD_REQUESTS = 21654;

    /**
     * Warehouse load aggregate round id
     * @see com.topcoder.server.AdminListener.request.WarehouseLoadRequest
     * @see com.topcoder.utilities.dwload.TCLoadRound
     */
    public static final int REQUEST_WAREHOUSE_LOAD_ROUND = 21655;

    /**
     * The constant representing <code>GetNewIDRequest</code>
     *
     * @since Admin Tool 2.0
     */
    public static final int REQUEST_NEW_ID = 21657;

    /**
     * The constant representing <code>SecurityManagementRequest</code>
     *
     * @since Admin Tool 2.0
     */
    public static final int REQUEST_SECURITY_MANAGEMENT = 21800;

    /**
     * An int constant representing the collection of GroupPrincipals.
     *
     * @since Admin Tool 2.0
     */
    public final static int GROUP_PRINCIPALS = 1;

    /**
     * An int constant representing the collection of RolePrincipals.
     *
     * @since Admin Tool 2.0
     */
    public final static int ROLE_PRINCIPALS = 2;

    /**
     * This is the perfix used for permissions that are mapped from function id's
     * @since AdminTool 2.0
     */
    private final static String ROUND_PERMISSION_PREFIX = "com.topcoder.client.contestMonitor";

    /**
     * This HashMap holds the mapping from the old security schema function id's
     * to the new TCPermissions contained in the new security schema.
     */
    private static HashMap functionPermissionMap = null;

    public static boolean isContestListener(int connectionId) {
        return connectionId == CONTEST_LISTENER_CONNECTION_ID;
    }

    public static boolean isBackEnd(int connectionId) {
        return connectionId == BACK_END_CONNECTION_ID;
    }

    public static boolean isClient(int connectionId) {
        return connectionId >= FIRST_CLIENT_CONNECTION_ID;
    }

    /**
     * Gets the TCPermission corresponding to request specified by given
     * integer constant.
     * @param requestId requestId an int representing one of the REQUEST_*
     * constants containing within AdminConstants class.
     * @return a TCPermission corresponding to specified request. Namely
     * returns a GenericPermission constructed with a name in form of
     * "com.topcoder.client.contestMonitor." + name of corresponding static
     * variable from AdminConstants class. For example : if a
     * REQUEST_SET_ROUND_TERMS is passed the the returned GenericPermission
     * contains a name " com.topcoder.client.contestMonitor.REQUEST_SET_ROUND_TERMS".
     * Returns null if given int does not correspond to any of REQUEST_* static
     * variables of AdminConstants class.
     */
    public static TCPermission getPermission(int requestId ) {
        if( functionPermissionMap == null )
            initializeFunctionPermissionMap();
        Integer key = new Integer(requestId);
        String name = "";
        if( functionPermissionMap.containsKey(key) == false )
            return null;
        else
            name = (String)functionPermissionMap.get(key);
        return new GenericPermission(name);
    }

    /**
     * Checks whether the permission granted to user corresponds to non-round
     * function or not. To do so gets the name of permission and checks whether
     * this name begins with "com.topcoder.client.contestMonitor.". If yes then
     * gets the rest of the name of permission and finds the integer constant
     * that corresponds to this name. If such constant is found then returns
     * true otherwise returns false.
     *
     * @param permission a TCPermission representing the permisison granted to user.
     * @return true if given permission corresponds to a non-round specific function;
     *  false otherwise.
     * @throws IllegalArgumentException if given TCPermission is null.
     * @since Admin Tool 2.0
     */
    public static boolean isNonRoundSpecificPermission(TCPermission permission) {
        if( permission == null )
            throw new IllegalArgumentException("permission cannot be null");
        String name = permission.getName();
        if( functionPermissionMap == null )
            initializeFunctionPermissionMap();
        if( name.startsWith(ROUND_PERMISSION_PREFIX) == false)
            return false;
        // return if the the requested permission exists in the map
        return functionPermissionMap.containsValue(name);
    }

    /**
     * This method will fill the functionPermission map with a mapping from
     * the Integer function id's of the old security schema to the new
     * TCPermission object of the new security schema.
     *
     * @throws IllegalArgumentException if the hash map is already initialized
     * @see TCPermission
     */
    private static synchronized void initializeFunctionPermissionMap() {
        if( functionPermissionMap != null )
            throw new IllegalArgumentException("attempt to initialize more than once");
        functionPermissionMap = new HashMap();
        functionPermissionMap.put(new Integer(ROUND_INDEPENDENT_FUNCTION),
                ROUND_PERMISSION_PREFIX + ".ROUND_INDEPENDENT_FUNCTION");
        functionPermissionMap.put(new Integer(REQUEST_BLOB_SEARCH),
                ROUND_PERMISSION_PREFIX + ".REQUEST_BLOB_SEARCH");
        functionPermissionMap.put(new Integer(REQUEST_OBJECT_LOAD),
                ROUND_PERMISSION_PREFIX + ".REQUEST_OBJECT_LOAD");
        functionPermissionMap.put(new Integer(REQUEST_TEXT_LOAD),
                ROUND_PERMISSION_PREFIX + ".REQUEST_TEXT_LOAD");
        functionPermissionMap.put(new Integer(REQUEST_TEXT_SEARCH),
                ROUND_PERMISSION_PREFIX + ".REQUEST_TEXT_SEARCH");
        functionPermissionMap.put(new Integer(REQUEST_DISABLE_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_DISABLE_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_ENABLE_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ENABLE_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_RESTORE_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTORE_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_REGISTER_USER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REGISTER_USER");
        functionPermissionMap.put(new Integer(REQUEST_UNREGISTER_USER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_UNREGISTER_USER");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_PROBLEMS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_PROBLEMS");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_REGISTRATION),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_REGISTRATION");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_ROOM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_ROOM");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_ALL_ROOMS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_ALL_ROOMS");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_ROOM_LISTS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_ROOM_LISTS");
        functionPermissionMap.put(new Integer(REQUEST_ASSIGN_ROOMS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ASSIGN_ROOMS");
        functionPermissionMap.put(new Integer(REQUEST_FORWARD_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_FORWARD_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_SHOW_SPEC_RESULTS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SHOW_SPEC_RESULTS");
        functionPermissionMap.put(new Integer(REQUEST_ADD_TIME),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ADD_TIME");
        functionPermissionMap.put(new Integer(REQUEST_ADVANCE_CONTEST_PHASE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ADVANCE_CONTEST_PHASE");
        functionPermissionMap.put(new Integer(REQUEST_REFRESH_BROADCASTS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REFRESH_BROADCASTS");
        functionPermissionMap.put(new Integer(REQUEST_CREATE_SYSTEM_TESTS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CREATE_SYSTEM_TESTS");
        functionPermissionMap.put(new Integer(REQUEST_CONSOLIDATE_TEST_CASES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CONSOLIDATE_TEST_CASES");
        functionPermissionMap.put(new Integer(REQUEST_CLEAR_TEST_CASES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CLEAR_TEST_CASES");
        functionPermissionMap.put(new Integer(REQUEST_SYSTEM_TEST),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SYSTEM_TEST");
        functionPermissionMap.put(new Integer(REQUEST_CANCEL_SYSTEM_TEST_CASE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CANCEL_SYSTEM_TEST_CASE");
        functionPermissionMap.put(new Integer(REQUEST_END_CONTEST),
                ROUND_PERMISSION_PREFIX + ".REQUEST_END_CONTEST");
        functionPermissionMap.put(new Integer(REQUEST_GENERATE_TEMPLATE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_GENERATE_TEMPLATE");
        functionPermissionMap.put(new Integer(REQUEST_ALLOCATE_PRIZES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ALLOCATE_PRIZES");
        functionPermissionMap.put(new Integer(REQUEST_RUN_RATINGS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RUN_RATINGS");
        functionPermissionMap.put(new Integer(REQUEST_INSERT_PRACTICE_ROOM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_INSERT_PRACTICE_ROOM");
        functionPermissionMap.put(new Integer(REQUEST_ANNOUNCE_ADVANCING_CODERS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ANNOUNCE_ADVANCING_CODERS");
        functionPermissionMap.put(new Integer(REQUEST_ADVANCE_WL_CODERS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ADVANCE_WL_CODERS");
        functionPermissionMap.put(new Integer(REQUEST_DISCONNECT_CLIENT),
                ROUND_PERMISSION_PREFIX + ".REQUEST_DISCONNECT_CLIENT");
        functionPermissionMap.put(new Integer(REQUEST_LOAD_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_LOAD_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_UNLOAD_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_UNLOAD_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_SHUTDOWN),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SHUTDOWN");
        functionPermissionMap.put(new Integer(REQUEST_GARBAGE_COLLECTION),
                ROUND_PERMISSION_PREFIX + ".REQUEST_GARBAGE_COLLECTION");
        functionPermissionMap.put(new Integer(REQUEST_START_REPLAY_LISTENER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_START_REPLAY_LISTENER");
        functionPermissionMap.put(new Integer(REQUEST_START_REPLAY_RECEIVER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_START_REPLAY_RECEIVER");
        functionPermissionMap.put(new Integer(REQUEST_FORWARDING),
                ROUND_PERMISSION_PREFIX + ".REQUEST_FORWARDING");
        functionPermissionMap.put(new Integer(REQUEST_ADMIN_FORWARDING),
                ROUND_PERMISSION_PREFIX + ".REQUEST_ADMIN_FORWARDING");
        functionPermissionMap.put(new Integer(REQUEST_CLEAR_PRACTICE_ROOMS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CLEAR_PRACTICE_ROOMS");
        functionPermissionMap.put(new Integer(REQUEST_IMPORTANT_MESSAGES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_IMPORTANT_MESSAGES");
        functionPermissionMap.put(new Integer(REQUEST_SPEC_APP_START_ROTATE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SPEC_APP_START_ROTATE");
        functionPermissionMap.put(new Integer(REQUEST_SPEC_APP_STOP_ROTATE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SPEC_APP_STOP_ROTATE");
        functionPermissionMap.put(new Integer(REQUEST_SPEC_APP_SHOW_ROOM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SPEC_APP_SHOW_ROOM");
        functionPermissionMap.put(new Integer(REQUEST_RESTART_EVENT_TOPIC_LISTENER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTART_EVENT_TOPIC_LISTENER");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_CODER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_CODER");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_CODER_PROBLEM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_CODER_PROBLEM");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_PROBLEM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_PROBLEM");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_REGISTRATION),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_REGISTRATION");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_ROOM),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_ROOM");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_CACHED_USER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CACHED_USER");
        functionPermissionMap.put(new Integer(REQUEST_BAN_IP),
                ROUND_PERMISSION_PREFIX + ".REQUEST_BAN_IP");
        functionPermissionMap.put(new Integer(REQUEST_GRANT_ADMIN_AUTHORITY),
                ROUND_PERMISSION_PREFIX + ".REQUEST_GRANT_ADMIN_AUTHORITY");
        functionPermissionMap.put(new Integer(REQUEST_REVOKE_ADMIN_AUTHORITY),
                ROUND_PERMISSION_PREFIX + ".REQUEST_REVOKE_ADMIN_AUTHORITY");
        functionPermissionMap.put(new Integer(REQUEST_SET_USER_STATUS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SET_USER_STATUS");
        functionPermissionMap.put(new Integer(REQUEST_CONTEST_MANAGEMENT),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CONTEST_MANAGEMENT");
        functionPermissionMap.put(new Integer(REQUEST_MODERATED_CHAT),
                ROUND_PERMISSION_PREFIX + ".REQUEST_MODERATED_CHAT");
        functionPermissionMap.put(new Integer(REQUEST_LOGGING),
                ROUND_PERMISSION_PREFIX + ".REQUEST_LOGGING");
        functionPermissionMap.put(new Integer(REQUEST_SEND_GLOBAL_BROADCAST),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SEND_GLOBAL_BROADCAST");
        functionPermissionMap.put(new Integer(REQUEST_SEND_COMPONENT_BROADCAST),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SEND_COMPONENT_BROADCAST");
        functionPermissionMap.put(new Integer(REQUEST_SEND_ROUND_BROADCAST),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SEND_ROUND_BROADCAST");
        functionPermissionMap.put(new Integer(REQUEST_CHAT_VIEW),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CHAT_VIEW");
        functionPermissionMap.put(new Integer(REQUEST_NEW_ID),
                ROUND_PERMISSION_PREFIX + ".REQUEST_NEW_ID");
        functionPermissionMap.put(new Integer(REQUEST_SECURITY_MANAGEMENT),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SECURITY_MANAGEMENT");
        functionPermissionMap.put(new Integer(REQUEST_BACKUP_TABLES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_BACKUP_TABLES");
        functionPermissionMap.put(new Integer(REQUEST_RESTORE_TABLES),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTORE_TABLES");
        functionPermissionMap.put(new Integer(REQUEST_SET_ROUND_TERMS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_SET_ROUND_TERMS");
        functionPermissionMap.put(new Integer(REQUEST_RESTART_COMPILERS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTART_COMPILERS");
        functionPermissionMap.put(new Integer(REQUEST_RESTART_TESTERS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTART_TESTERS");
        functionPermissionMap.put(new Integer(REQUEST_RESTART_ALL),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RESTART_ALL");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_AGGREGATE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_AGGREGATE");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_CODER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_CODER");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_EMPTY),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_EMPTY");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_RANK),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_RANK");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_REQUESTS),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_REQUESTS");
        functionPermissionMap.put(new Integer(REQUEST_WAREHOUSE_LOAD_ROUND),
                ROUND_PERMISSION_PREFIX + ".REQUEST_WAREHOUSE_LOAD_ROUND");
        functionPermissionMap.put(new Integer(REQUEST_RECALCULATE_SCORE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_RECALCULATE_SCORE");
        functionPermissionMap.put(new Integer(REQUEST_CLEAR_CACHE),
                ROUND_PERMISSION_PREFIX + ".REQUEST_CLEAR_CACHE");
        functionPermissionMap.put(new Integer(REQUEST_BOOT_USER),
                ROUND_PERMISSION_PREFIX + ".REQUEST_BOOT_USER");
    }
}
