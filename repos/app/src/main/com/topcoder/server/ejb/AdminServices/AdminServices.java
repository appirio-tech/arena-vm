package com.topcoder.server.ejb.AdminServices;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.ejb.EJBObject;

import com.topcoder.server.AdminListener.request.BackEndChangeRoundRequest;
import com.topcoder.server.AdminListener.request.BackEndLoginRequest;
import com.topcoder.server.AdminListener.request.BackEndRefreshAccessRequest;
import com.topcoder.server.AdminListener.request.BackEndRoundAccessRequest;
import com.topcoder.server.AdminListener.request.ConsolidateTestRequest;
import com.topcoder.server.AdminListener.request.CreateSystestsRequest;
import com.topcoder.server.AdminListener.request.InsertPracticeRoomRequest;
import com.topcoder.server.AdminListener.request.ObjectSearchRequest;
import com.topcoder.server.AdminListener.request.ObjectUpdateRequest;
import com.topcoder.server.AdminListener.request.RunRatingsRequest;
import com.topcoder.server.AdminListener.request.RunSeasonRatingsRequest;
import com.topcoder.server.AdminListener.request.SecurityCheck;
import com.topcoder.server.AdminListener.request.ServerReplySecurityCheck;
import com.topcoder.server.AdminListener.request.TextSearchRequest;
import com.topcoder.server.AdminListener.request.TextUpdateRequest;
import com.topcoder.server.AdminListener.request.UnsupportedRequestException;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.server.AdminListener.response.ChangeRoundResponse;
import com.topcoder.server.AdminListener.response.CommandResponse;
import com.topcoder.server.AdminListener.response.LoginResponse;
import com.topcoder.server.AdminListener.response.ObjectSearchResponse;
import com.topcoder.server.AdminListener.response.ObjectUpdateResponse;
import com.topcoder.server.AdminListener.response.RefreshAccessResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.AdminListener.response.TextColumnResponse;
import com.topcoder.server.AdminListener.response.TextSearchResponse;
import com.topcoder.server.AdminListener.response.TextUpdateResponse;
import com.topcoder.server.contest.AnswerData;
import com.topcoder.server.contest.ContestData;
import com.topcoder.server.contest.ImportantMessageData;
import com.topcoder.server.contest.RoundEventData;
import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.server.contest.QuestionData;
import com.topcoder.server.contest.RoundData;
import com.topcoder.server.contest.RoundRoomAssignment;
import com.topcoder.server.contest.RoundSegmentData;
import com.topcoder.server.contest.SurveyData;
import java.util.Map;



/**
 * The remote interface for the Admin Services EJB.  See the <tt>AdminServicesBean</tt>
 * class for API details.
 *
 * This interface was updated for AdminTool 2.0 see <tt>AdminServicesBean</tt>
 * class comment for details.
 * 
 * @see AdminServicesBean
 */

public interface AdminServices extends EJBObject {
	
	public int recalculateScore(int roundId, String handle) throws RemoteException;

    // Security items
    public boolean checkClientRequestAccess(SecurityCheck request) throws RemoteException, SQLException, UnsupportedRequestException;

    public List checkServerReplyAccess(ServerReplySecurityCheck request) throws RemoteException, Exception;

    public LoginResponse processLoginRequest(BackEndLoginRequest request) throws RemoteException, Exception;

    public RoundAccessResponse processRoundAccessRequest(BackEndRoundAccessRequest request) throws SQLException, RemoteException;

    public ChangeRoundResponse processChangeRoundRequest(BackEndChangeRoundRequest request) throws RemoteException;

    public RefreshAccessResponse processRefreshAccessRequest(BackEndRefreshAccessRequest request) throws RemoteException;

    // New contest-related items
    public CommandResponse createSystemTests(CreateSystestsRequest request) throws RemoteException, Exception;

    public CommandResponse consolidateTestCases(ConsolidateTestRequest request) throws RemoteException, Exception;

    public CommandResponse runRatings(RunRatingsRequest request) throws RemoteException, Exception;
    
    public CommandResponse runSeasonRatings(RunSeasonRatingsRequest request) throws RemoteException, Exception;

    public CommandResponse insertPracticeRooms(InsertPracticeRoomRequest request) throws RemoteException, SQLException;

    // Other miscellaneous new items
    public BlobColumnResponse getBlobColumnMetadata() throws RemoteException;

    public ObjectUpdateResponse updateDBObject(ObjectUpdateRequest request) throws RemoteException;

    public ObjectSearchResponse runObjectSearch(ObjectSearchRequest request) throws RemoteException;

    public TextColumnResponse getTextColumnMetadata() throws RemoteException;

    public TextUpdateResponse updateDBText(TextUpdateRequest request) throws RemoteException;

    public TextSearchResponse runTextSearch(TextSearchRequest request) throws RemoteException;

    /**
     * @return all contests
     * @throws RemoteException
     * @throws SQLException
     */
    Collection getAllContests() throws RemoteException, SQLException;
    
    Collection getAllImportantMessages() throws RemoteException, SQLException;

    ContestData getContest(int contestID) throws RemoteException, SQLException;

    /**
     * Add a contest
     * @param contest
     * @throws RemoteException
     * @throws SQLException
     */
    void addContest(ContestData contest) throws RemoteException, SQLException;


    /**
     * Modify a contest
     * @param id
     * @param contest
     * @throws RemoteException
     * @throws SQLException
     */
    void modifyContest(int id, ContestData contest) throws RemoteException, SQLException;
    
    void addMessage(ImportantMessageData message) throws RemoteException, SQLException;
    void modifyMessage(int id, ImportantMessageData message) throws RemoteException, SQLException;


    /**
     * Delete a contest
     * @param id
     * @throws RemoteException
     * @throws SQLException
     */
    void deleteContest(int id) throws RemoteException, SQLException;

    /**
     * Gets new ID generated by specififed sequence. Calls <code>
     * DBMS.getSeqID(sequence)</code> method to accomplish the task.
     *
     * @param  sequence an ID of sequence that should be used to generate new
     *         ID
     * @return an int representing the ID that was generated by specified 
     *         sequence
     * @throws SQLException
     * @since  Admin Tool 2.0
     */
    public int getNewID(String sequence) throws RemoteException, SQLException;

    /**
     * Saves the details of round room assignment algorithm for specified
     * round to databse.
     *
     * @param  details a RoundRoomAssignment object containing the details
     *         of round room assignment algorithm for some round that needs
     * @throws IllegalArgumentException if given argument is null.
     * @throws SQLException
     * @since  Admin Tool 2.0
     */
    public void saveRoundRoomAssignment(RoundRoomAssignment details) throws
        RemoteException, SQLException;
    
    int getNumRounds(int contestID) throws RemoteException, SQLException;

    Collection getConflictingContests(int contestID) throws RemoteException, SQLException;

    Collection getRounds(int contestId) throws RemoteException, SQLException;

    RoundData getRound(int roundID) throws RemoteException, SQLException;

    Collection getRoundTypes() throws RemoteException, SQLException;
    
    Collection getSeasons() throws RemoteException, SQLException;
    
    Collection getRegions() throws RemoteException, SQLException;

    void addRound(RoundData round) throws RemoteException, SQLException;

    void modifyRound(int oldId, RoundData newRound) throws RemoteException, SQLException;

    void deleteRound(int id) throws RemoteException, SQLException;

    void setRoundSegments(RoundSegmentData segments) throws RemoteException, SQLException;

    void setRoundLanguages(RoundLanguageData languages) throws RemoteException, SQLException;
    /**
     * <p>
     * set round event data.
     * </p>
     * @param eventData
     *          the event data.
     * @throws RemoteException
     *          the ejb caused exception thrown
     * @throws SQLException
     *          the sql exception thrown
     */
    void setRoundEvents(RoundEventData eventData) throws RemoteException, SQLException;

    Collection getAssignedProblems(int roundID) throws RemoteException, SQLException;

    Collection getProblems() throws RemoteException, SQLException;

    Collection getRoundProblemComponents(int roundID) throws RemoteException, SQLException;

    Collection getRoundProblemComponents(int roundID, int problemID, int divisionID) throws RemoteException, SQLException;

    /**
     * Sets the components for a problem.  BE VERY CAREFUL WHEN USING THIS METHOD.  This method expects that you know
     * the database schema.  Namely, it expects that if you've passed in one component in a specific problem, you have
     * also passed in the rest of the components for that problem.  Failure to adhere to this rule will cause problems.
     * Serious ones.  This method does no verification that you've passed it valid data, because that would be too
     * inefficient.
     *
     * @param roundID
     * @param components All components for this round.  Every component for each problem MUST be present.
     * @throws RemoteException
     * @throws SQLException Not in the case of the above problem, though!
     */
    void setComponents(int roundID, Collection components) throws RemoteException, SQLException;

    Collection getProblemStatusTypes() throws RemoteException, SQLException;

    Collection getDifficultyLevels() throws RemoteException, SQLException;

    Collection getDivisions() throws RemoteException, SQLException;

    Collection getSurveyStatusTypes() throws RemoteException, SQLException;

    void setSurvey(SurveyData survey) throws RemoteException, SQLException;

    Collection getQuestionTypes() throws RemoteException, SQLException;

    Collection getQuestionStyles() throws RemoteException, SQLException;

    Collection getQuestions(int roundID) throws RemoteException, SQLException;
    
    Collection getLanguages() throws RemoteException, SQLException;

    int addQuestion(int roundID, QuestionData question) throws RemoteException, SQLException;

    void modifyQuestion(QuestionData question) throws RemoteException, SQLException;

    void deleteQuestion(int questionID) throws RemoteException, SQLException;

    Collection getAnswers(int questionID) throws RemoteException, SQLException;

    int addAnswer(int questionID, AnswerData answer) throws RemoteException, SQLException;

    void modifyAnswer(AnswerData answer) throws RemoteException, SQLException;

    void deleteAnswer(int answerID) throws RemoteException, SQLException;

    public Collection getTestCases(int problemID) throws SQLException, RemoteException;

    /**
     * Persists the content of terms for specified round in database using
     * specified properties to evalute content of terms.
     *
     * @param roundID an ID of round to set terms for
     * @param params a Hashtable with property names and property values
     * @since Admin Tool 2.0
     */
    public void setRoundTerms(int roundID, Map params) throws SQLException, RemoteException;

    /**
     * Creates a backup copy of specified tables for specified round. Created
     * backup copy is added to existing backup copies of this and other
     * rounds.
     *
     * To do so obtains an ID for new backup copy using newly defined
     * BACKUP_SEQ sequence, inserts records into new "backup" and "backup tables"
     * tables and insert data from given tables into corresponding staging
     * tables.
     *
     * @param  roundID an ID of requested round
     * @param  tableNames a Set of String names of tables that should be
     *         backed up.
     * @param  comment a String comment associated with the backup
     * @throws IllegalArgumentException if given Set is null.
     * @return the id of newly created backup
     * @since  Admin Tool 2.0
     */
    public long backupTables(int roundID, Set tableNames, String comment) throws SQLException, RemoteException;

    /**
     * Gets the list of existing backup copies for specified round. If no backup
     * copies are available for specified round returns empty list.
     *
     * @param  roundID an ID of requested round.
     * @return a List of BackupCopy objects representing existing backup copies
     *         for specified round.
     * @throws SQLException if any SQL Error occurs or given round does not
     *         exist
     * @since  Admin Tool 2.0
     * @see    com.topcoder.server.common.BackupCopy
     */
    public List getBackupCopies(int roundID) throws RemoteException, SQLException;

    /**
     * Restores specified tables for some round from specified backup copy.
     *
     * @param  backupID an ID of requested backup copy to restore tables from
     * @param  tableNames a Set of String names of tables that should be
     *         restored.
     * @throws SQLException if any SQL Error occurs or backup copy with given
     *         id does not exist or round with ID specified to given backup
     *         copy does not exist
     * @throws IllegalArgumentException if given Set is null.
     * @since  Admin Tool 2.0
     */
    public void restoreTables(int backupID, Set tableNames) throws RemoteException, SQLException;


    /**
     * Performs the warehouse data load process specified by name of class
     * extending TCLoad class and a Hashtable containing neceessary
     * parameters.
     *
     * @param tcLoadClass a TCLoad extending class that should be used to
     * perform a warehouse data load
     * @param params a Hashtable mapping the String parameter names to String
     * values of parameters
     * @throws IllegalArgumentException if any parameter is null
     * @throws ClassNotFoundException if the class specified by tcLoadClass can not be found
     * @since Admin Tool 2.0
     */
    public void loadWarehouseData(String tcLoadClass, Map params, int type) throws RemoteException, ClassNotFoundException, SQLException;
    
    public void clearCache() throws RemoteException;
    
    public String generateTemplate(int roundID) throws RemoteException, SQLException;

    public CommandResponse setForumID(int roundID, int forumID) throws RemoteException, SQLException;
}
