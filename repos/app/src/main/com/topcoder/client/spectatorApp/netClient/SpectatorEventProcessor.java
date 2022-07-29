/**
 * SpectatorEventProcessor.java Description: Thread that will take responses and
 * dispatch them to the appropriate listener.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.client.spectatorApp.netClient;

import java.awt.Image;
import java.awt.Toolkit;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.Category;

import com.topcoder.client.spectatorApp.CommonRoutines;
import com.topcoder.client.spectatorApp.Constants;
import com.topcoder.client.spectatorApp.SpectatorApp;
import com.topcoder.client.spectatorApp.controller.GUIController;
import com.topcoder.client.spectatorApp.event.AnnounceCoderEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDesignReviewBoardEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDesignReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDevelopmentReviewBoardEvent;
import com.topcoder.client.spectatorApp.event.AnnounceDevelopmentReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTCSCoderEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTCSWinnersEvent;
import com.topcoder.client.spectatorApp.event.AnnounceTableResultsEvent;
import com.topcoder.client.spectatorApp.event.AnnouncementListener;
import com.topcoder.client.spectatorApp.event.AnnouncementSupport;
import com.topcoder.client.spectatorApp.event.ComponentAppealEvent;
import com.topcoder.client.spectatorApp.event.ComponentContestConnectionListener;
import com.topcoder.client.spectatorApp.event.ComponentContestConnectionSupport;
import com.topcoder.client.spectatorApp.event.ComponentContestListener;
import com.topcoder.client.spectatorApp.event.ComponentContestSupport;
import com.topcoder.client.spectatorApp.event.ConnectionEvent;
import com.topcoder.client.spectatorApp.event.ConnectionListener;
import com.topcoder.client.spectatorApp.event.ConnectionSupport;
import com.topcoder.client.spectatorApp.event.ContestEvent;
import com.topcoder.client.spectatorApp.event.ContestListener;
import com.topcoder.client.spectatorApp.event.ContestSupport;
import com.topcoder.client.spectatorApp.event.DefineRoomEvent;
import com.topcoder.client.spectatorApp.event.LoginEvent;
import com.topcoder.client.spectatorApp.event.LoginListener;
import com.topcoder.client.spectatorApp.event.LoginSupport;
import com.topcoder.client.spectatorApp.event.LongProblemNotificationEvent;
import com.topcoder.client.spectatorApp.event.PhaseEvent;
import com.topcoder.client.spectatorApp.event.PhaseListener;
import com.topcoder.client.spectatorApp.event.PhaseSupport;
import com.topcoder.client.spectatorApp.event.ProblemListener;
import com.topcoder.client.spectatorApp.event.ProblemNotificationEvent;
import com.topcoder.client.spectatorApp.event.ProblemResultEvent;
import com.topcoder.client.spectatorApp.event.ProblemResultListener;
import com.topcoder.client.spectatorApp.event.ProblemResultSupport;
import com.topcoder.client.spectatorApp.event.ProblemSupport;
import com.topcoder.client.spectatorApp.event.RoomListener;
import com.topcoder.client.spectatorApp.event.RoomSupport;
import com.topcoder.client.spectatorApp.event.RoomWinnerEvent;
import com.topcoder.client.spectatorApp.event.RoundEvent;
import com.topcoder.client.spectatorApp.event.RoundListener;
import com.topcoder.client.spectatorApp.event.RoundSupport;
import com.topcoder.client.spectatorApp.event.ShowComponentEvent;
import com.topcoder.client.spectatorApp.event.ShowComponentResultsEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.event.ShowPlacementListener;
import com.topcoder.client.spectatorApp.event.ShowPlacementSupport;
import com.topcoder.client.spectatorApp.event.ShowRoomEvent;
import com.topcoder.client.spectatorApp.event.ShowScreenEvent;
import com.topcoder.client.spectatorApp.event.ShowScreenListener;
import com.topcoder.client.spectatorApp.event.ShowScreenSupport;
import com.topcoder.client.spectatorApp.event.ShowTCSPlacementEvent;
import com.topcoder.client.spectatorApp.event.SystemTestResultsEvent;
import com.topcoder.client.spectatorApp.event.SystemTestResultsListener;
import com.topcoder.client.spectatorApp.event.SystemTestResultsSupport;
import com.topcoder.client.spectatorApp.event.TeamEvent;
import com.topcoder.client.spectatorApp.event.TeamListener;
import com.topcoder.client.spectatorApp.event.TeamSupport;
import com.topcoder.client.spectatorApp.event.TimerEvent;
import com.topcoder.client.spectatorApp.event.TimerListener;
import com.topcoder.client.spectatorApp.event.TimerSupport;
import com.topcoder.client.spectatorApp.event.VoteEvent;
import com.topcoder.client.spectatorApp.event.VoteListener;
import com.topcoder.client.spectatorApp.event.VoteSupport;
import com.topcoder.client.spectatorApp.messages.AnnounceCoder;
import com.topcoder.client.spectatorApp.messages.AnnounceDesignReviewBoard;
import com.topcoder.client.spectatorApp.messages.AnnounceDesignReviewBoardResults;
import com.topcoder.client.spectatorApp.messages.AnnounceDevelopmentReviewBoard;
import com.topcoder.client.spectatorApp.messages.AnnounceDevelopmentReviewBoardResults;
import com.topcoder.client.spectatorApp.messages.AnnounceTCSCoder;
import com.topcoder.client.spectatorApp.messages.AnnounceTCSWinners;
import com.topcoder.client.spectatorApp.messages.AnnounceTableResults;
import com.topcoder.client.spectatorApp.messages.CoderStats;
import com.topcoder.client.spectatorApp.messages.ConnectionResponse;
import com.topcoder.client.spectatorApp.messages.DefineComponentContestConnection;
import com.topcoder.client.spectatorApp.messages.IgnorePhaseChange;
import com.topcoder.client.spectatorApp.messages.InvitationalStats;
import com.topcoder.client.spectatorApp.messages.RenderCommand;
import com.topcoder.client.spectatorApp.messages.ShowComponent;
import com.topcoder.client.spectatorApp.messages.ShowComponentResultsByComponentID;
import com.topcoder.client.spectatorApp.messages.ShowDesign;
import com.topcoder.client.spectatorApp.messages.ShowDevelopment;
import com.topcoder.client.spectatorApp.messages.ShowImage;
import com.topcoder.client.spectatorApp.messages.ShowInitial;
import com.topcoder.client.spectatorApp.messages.ShowNoHeaderScreen;
import com.topcoder.client.spectatorApp.messages.ShowPlacement;
import com.topcoder.client.spectatorApp.messages.ShowRound;
import com.topcoder.client.spectatorApp.messages.ShowScreen;
import com.topcoder.client.spectatorApp.messages.ShowStudio;
import com.topcoder.client.spectatorApp.messages.ShowStudioIndividiualResults;
import com.topcoder.client.spectatorApp.messages.ShowSystemTestResultsByCoder;
import com.topcoder.client.spectatorApp.messages.ShowSystemTestResultsByCoderAll;
import com.topcoder.client.spectatorApp.messages.ShowSystemTestResultsByProblem;
import com.topcoder.client.spectatorApp.messages.ShowTCSPlacement;
import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.request.LoginRequest;
import com.topcoder.netCommon.contestantMessages.response.ExchangeKeyResponse;
import com.topcoder.shared.netCommon.MessageEncryptionHandler;
import com.topcoder.shared.netCommon.messages.spectator.ComponentAppeal;
import com.topcoder.shared.netCommon.messages.spectator.ComponentScoreUpdate;
import com.topcoder.shared.netCommon.messages.spectator.ComponentTimeUpdate;
import com.topcoder.shared.netCommon.messages.spectator.DefineComponentContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineContest;
import com.topcoder.shared.netCommon.messages.spectator.DefineRoom;
import com.topcoder.shared.netCommon.messages.spectator.DefineRound;
import com.topcoder.shared.netCommon.messages.spectator.DefineWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.LongProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.PhaseChange;
import com.topcoder.shared.netCommon.messages.spectator.ProblemEvent;
import com.topcoder.shared.netCommon.messages.spectator.ProblemResult;
import com.topcoder.shared.netCommon.messages.spectator.RoomData;
import com.topcoder.shared.netCommon.messages.spectator.RoomWinner;
import com.topcoder.shared.netCommon.messages.spectator.ShowRoom;
import com.topcoder.shared.netCommon.messages.spectator.ShowWeakestLinkTeam;
import com.topcoder.shared.netCommon.messages.spectator.SpectatorLoginResult;
import com.topcoder.shared.netCommon.messages.spectator.TimerUpdate;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkElimination;
import com.topcoder.shared.netCommon.messages.spectator.WeakestLinkVote;

public class SpectatorEventProcessor implements EventProcessor {
	/** reference to the logging category */
	private static final Category cat = Category.getInstance(SpectatorEventProcessor.class.getName());

	/** Singleton instance of the thread */
	private static SpectatorEventProcessor spectatorEventProcessor;

	/** Handler for connection events */
	private ConnectionSupport connectionSpt = new ConnectionSupport();

	/** Handler for login events */
	private LoginSupport loginSpt = new LoginSupport();

	/** Handler for timer events */
	private TimerSupport timerSpt = new TimerSupport();

	/** Handler for phase events */
	private PhaseSupport phaseSpt = new PhaseSupport();

	/** Handler for room events */
	private RoomSupport roomSpt = new RoomSupport();

	/** Handler for problem events */
	private ProblemSupport problemSpt = new ProblemSupport();

	/** Handler for problem result events */
	private ProblemResultSupport problemResultSpt = new ProblemResultSupport();

	/** Handler for contest events */
	private ContestSupport contestSpt = new ContestSupport();

	/** Handler for round events */
	private RoundSupport roundSpt = new RoundSupport();

	/** Handler for team events */
	private TeamSupport teamSpt = new TeamSupport();

	/** Handler for component contest connection events */
	private ComponentContestConnectionSupport componentConnectionSpt = new ComponentContestConnectionSupport();

	/** Handler for component contest connection events */
	private ComponentContestSupport componentContestSpt = new ComponentContestSupport();

	/** Handler for voting events */
	private VoteSupport voteSpt = new VoteSupport();

	/** Handler for coder announcements */
	private AnnouncementSupport announceCoderSpt = new AnnouncementSupport();

	/** Handler for system test result events */
	private SystemTestResultsSupport systemTestResultsSpt = new SystemTestResultsSupport();

	/** Handler for show placement events */
	private ShowPlacementSupport showPlacementSupport = new ShowPlacementSupport();
	
	/** Handler for show screen events */
	private ShowScreenSupport showScreenSupport = new ShowScreenSupport();
        
        //this should be rewritten somehow, but I don't see a quick way to get current
        //contest status
        private boolean pending = false;
        
    private int[] placements = null;
    
    private boolean ignorePhase = false;

    private Key encryptKey;

	/**
	 * Private Constructor. The SpectatorEventProcessor implements a singleton
	 * pattern. Please use getInstance() to retrieve an instance of this class
	 */
	private SpectatorEventProcessor() {}

	/**
	 * Get's the instance of the dispatch thread.
	 */
	public final static synchronized SpectatorEventProcessor getInstance() {
		if (spectatorEventProcessor == null) spectatorEventProcessor = new SpectatorEventProcessor();
		return spectatorEventProcessor;
	}

	/**
	 * Dispatches the passed massage and triggers the appropriate events
	 * 
	 * @param message the message to process
	 */
	public final void processEvent(Object message) {
                //cat.info("GOT: " + message);
		// ------- ConnectionResponse
		if (message instanceof ConnectionResponse) {
			ConnectionResponse o = (ConnectionResponse) message;
			if (o.getReason() == null) {
				connectionSpt.fireConnectionMade(new ConnectionEvent(this, o.getRemoteIP(), o.getRemotePort()));
			} else {
				connectionSpt.fireConnectionLost(new ConnectionEvent(this, o.getRemoteIP(), o.getRemotePort(), o.getReason()));
			}
			// ------- SpectatorLoginResult
        } else if (message instanceof ExchangeKeyResponse) {
            String userid = SpectatorApp.getInstance().getuserid();
            String password = SpectatorApp.getInstance().getpassword();
            MessageEncryptionHandler handler = SpectatorApp.getInstance().getEncryptionHandler();
            handler.setReplyKey(((ExchangeKeyResponse) message).getKey());
            encryptKey = handler.getFinalKey();
            try {
                RequestThread.getInstance().queueMessage(new LoginRequest(userid, MessageEncryptionHandler.sealObject(password, encryptKey), ContestConstants.SPECTATOR_LOGIN));
            } catch (GeneralSecurityException e) {
                cat.error("Encrypting password failed", e);
            }
		} else if (message instanceof SpectatorLoginResult) {
			SpectatorLoginResult o = (SpectatorLoginResult) message;
            try {
                if (o.isSuccessful()) {
                    loginSpt.fireLoginSuccessful(new LoginEvent(this, o.getUserID(), (String) MessageEncryptionHandler.unsealObject(o.getPassword(), encryptKey)));
                } else {
                    loginSpt.fireLoginFailure(new LoginEvent(this, o.getUserID(), (String) MessageEncryptionHandler.unsealObject(o.getPassword(), encryptKey), o.getMessage()));
                }
            } catch (GeneralSecurityException e) {
                cat.error("Decrypting password failed", e);
            }
        } else if (message instanceof IgnorePhaseChange) {
            ignorePhase = ((IgnorePhaseChange) message).isIgnore();
			// ------- PhaseChange
		} else if (message instanceof PhaseChange) {
            if (!ignorePhase) {
                PhaseChange o = (PhaseChange) message;
                // Find out the current state of the paint
                boolean isPaintEnabled = GUIController.getInstance().isPaintEnabled();
			
                // Suppress display during a phase change
                GUIController.getInstance().enablePainting(false);
                try {
                    // Fire the phase event
                    switch (o.getPhaseID()) {
                    // case PhaseChange.STARTCONTEST : { phaseSpt.fireStartContest(new
                    // PhaseEvent(this, Constants.PHASE_STARTCONTEST,
                    // o.getTimeAllocated())); break; }
                    // case PhaseChange.REGISTRATION : { phaseSpt.fireRegistration(new
                    // PhaseEvent(this, Constants.PHASE_REGISTRATION,
                    // o.getTimeAllocated())); break; }
                    case ContestConstants.CODING_PHASE: {
                        phaseSpt.fireCoding(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.INTERMISSION_PHASE: {
                        phaseSpt.fireIntermission(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.CHALLENGE_PHASE: {
                        phaseSpt.fireChallenge(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.SYSTEM_TESTING_PHASE: {
                        phaseSpt.fireSystemTesting(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.VOTING_PHASE: {
                        phaseSpt.fireVoting(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.TIE_BREAKING_VOTING_PHASE: {
                        phaseSpt.fireVotingTie(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.CONTEST_COMPLETE_PHASE: {
                        phaseSpt.fireEndContest(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.COMPONENT_CONTEST_APPEALS: {
                        phaseSpt.fireComponentAppeals(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.COMPONENT_CONTEST_RESULTS: {
                        phaseSpt.fireComponentResults(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    case ContestConstants.COMPONENT_CONTEST_END: {
                        phaseSpt.fireComponentEndContest(new PhaseEvent(this, o.getTimeAllocated()));
                        break;
                    }
                    default: {
                        phaseSpt.fireUnknown(new PhaseEvent(this, o.getTimeAllocated()), o.getPhaseID());
                        break;
                    }
                    }
                    // Fire off a timer update
                    timerSpt.fireTimerUpdate(new TimerEvent(this, o.getTimeAllocated()));
                } finally {
                    if (isPaintEnabled) GUIController.getInstance().enablePainting(true);
                }
            }
			// ------- TimerUpdate
		} else if (message instanceof TimerUpdate) {
            if (!ignorePhase) {
                TimerUpdate o = (TimerUpdate) message;
                timerSpt.fireTimerUpdate(new TimerEvent(this, o.getTimeLeft()));
            }
			// ------- DefineRoom
		} else if (message instanceof DefineRoom) {
			DefineRoom o = (DefineRoom) message;
			RoomData da = o.getRoom();
			roomSpt.fireDefineRoom(new DefineRoomEvent(this, da.getRoomID(), da.getRoomType(), da.getRoomTitle(), da.getRoundID(), o.getAssignedCoders(), o.getAssignedProblems()));
			// ------- ShowRoom
		} else if (message instanceof ShowRoom) {
			ShowRoom o = (ShowRoom) message;
			roomSpt.fireShowRoom(new ShowRoomEvent(this, o.getRoom().getRoomID()));
			// ------- ProblemResult (must be BEFORE ProblemEvent since it inherits
			// from it)
		} else if (message instanceof ProblemResult) {
			ProblemResult o = (ProblemResult) message;
			ProblemResultEvent evt;
			if (o.getResult() == ProblemResult.SUCCESSFUL) {
				evt = new ProblemResultEvent(this, o.getRoom().getRoomID(), o.getSourceCoder(), o.getProblemWriter(), o.getProblemID(), o.getTimeLeft(), ProblemResultEvent.SUCCESSFUL, o.getResultValue());
			} else {
				evt = new ProblemResultEvent(this, o.getRoom().getRoomID(), o.getSourceCoder(), o.getProblemWriter(), o.getProblemID(), o.getTimeLeft(), ProblemResultEvent.FAILURE, o.getResultValue());
			}
			switch (o.getProblemEventType()) {
			case ProblemEvent.SUBMITTING: {
				problemResultSpt.fireSubmitted(evt);
				break;
			}
			case ProblemEvent.CHALLENGING: {
				problemResultSpt.fireChallenged(evt);
				break;
			}
			case ProblemEvent.SYSTEMTESTING: {
				problemResultSpt.fireSystemTested(evt);
				break;
			}
			default: {
				cat.warn("Unknown problem event: " + message);
				break;
			}
			}
			// ------- ProblemEvent
		} else if (message instanceof ProblemEvent) {
			ProblemEvent o = (ProblemEvent) message;
			ProblemNotificationEvent evt = new ProblemNotificationEvent(this, o.getRoom().getRoomID(), o.getSourceCoder(), o.getProblemWriter(), o.getProblemID(), o.getTimeLeft());
			switch (o.getProblemEventType()) {
			case ProblemEvent.OPENED: {
				problemSpt.fireOpened(evt);
				break;
			}
			case ProblemEvent.CLOSED: {
				problemSpt.fireClosed(evt);
				break;
			}
			case ProblemEvent.COMPILING: {
				problemSpt.fireCompiling(evt);
				break;
			}
			case ProblemEvent.TESTING: {
				problemSpt.fireTesting(evt);
				break;
			}
			case ProblemEvent.SUBMITTING: {
				problemSpt.fireSubmitting(evt);
				break;
			}
			case ProblemEvent.CHALLENGING: {
				problemSpt.fireChallenging(evt);
				break;
			}
			case ProblemEvent.SYSTEMTESTING: {
				problemSpt.fireSystemTesting(evt);
				break;
			}
			default: {
				cat.warn("Unknown problem event: " + message);
				break;
			}
			}
			// ------- AnnounceCoder
                } else if (message instanceof LongProblemEvent) {
                    LongProblemEvent o = (LongProblemEvent) message;
                    LongProblemNotificationEvent evt = new LongProblemNotificationEvent(this, o.getRoom().getRoomID(), o.getWriter(), o.getProblemID(), o.getSubmissionCount(), o.getSubmissionTime(), o.getExampleCount(), o.getExampleTime());
                    problemSpt.fireLongProblemInfo(evt);
		} else if (message instanceof AnnounceCoder) {
			AnnounceCoder o = (AnnounceCoder) message;
			// Get the information (load the image)
			CoderStats ss = o.getCoderStats();
			InvitationalStats is = o.getInvitationalStats();
			Image img = Toolkit.getDefaultToolkit().createImage(o.getImage());
			CommonRoutines.loadImagesFully(new Image[] { img });
			// Send the event
			AnnounceCoderEvent evt = new AnnounceCoderEvent(this, o.getRoundID(), o.getName(), o.getHandle(), img, o.getCollege(), o.getRating(), o.getRanking(), o.getSeed(), ss.getNumCompetitions(), ss
						.getNumSubmissions(), ss.getSubmissionPrct(), ss.getNumChallenges(), ss.getChallengePrct(), is.getNumCompetitions(), is.getNumSubmissions(), is.getSubmissionPrct(), is
						.getNumChallenges(), is.getChallengePrct());
			announceCoderSpt.fireAnnounceCoder(evt);
			// Update the current phase to announcment phase
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
			// ------- DefineContest
		} else if (message instanceof DefineContest) {
			// Fire the contest information event
			DefineContest o = (DefineContest) message;
			// Load the image
			Image imgLarge = null, imgSmall = null, imgSponsor = null;
			// Only load the images if they are ALL present
			imgLarge = o.getLogoLarge() == null ? null : Toolkit.getDefaultToolkit().createImage(o.getLogoLarge());
			imgSmall = o.getLogoSmall() == null ? null : Toolkit.getDefaultToolkit().createImage(o.getLogoSmall());
			imgSponsor = o.getSponsorLogo() == null ? null : Toolkit.getDefaultToolkit().createImage(o.getSponsorLogo());
			CommonRoutines.loadImagesFully(new Image[] { imgLarge, imgSmall, imgSponsor });
			ContestEvent evt = new ContestEvent(this, o.getContestID(), o.getContestName(), imgLarge, imgSmall, imgSponsor);
			contestSpt.fireDefineContest(evt);
			// ------- DefineRound
		} else if (message instanceof DefineRound) {
			// Fire the contest information event
			DefineRound o = (DefineRound) message;
			// Load the image
			RoundEvent evt = new RoundEvent(this, o.getRoundID(), o.getRoundType(), o.getRoundName(), o.getContestID());
			roundSpt.fireDefineRound(evt);
			// ------- RenderCommand
		} else if (message instanceof RenderCommand) {
			GUIController.getInstance().enablePainting(((RenderCommand) message).startRendering());
			// ------- ShowRound
		} else if (message instanceof ShowRound) {
			ShowRound o = (ShowRound) message;
			// Update the current phase to a ContestInfo phase
			phaseSpt.fireContestInfo(new PhaseEvent(this));
			
			// Fire the show round event
			RoundEvent evt = new RoundEvent(this, o.getRoundID());
			roundSpt.fireShowRound(evt);
			
		} else if (message instanceof ShowComponent) {
			ShowComponent o = (ShowComponent) message;
			// Update the current phase to a component appeals Info phase
			phaseSpt.fireComponentAppeals(new PhaseEvent(this));
			
			// Fire the show component event
			componentContestSpt.fireShowComponent(new ShowComponentEvent(this, o.getContestID(), o.getRoundID(), o.getComponentID()));

                } else if (message instanceof ComponentTimeUpdate) {
                        ComponentTimeUpdate o = (ComponentTimeUpdate) message;
                        cat.info("Got a timer update: " + o.getAppealsStartTime() + ":" + o.getAppealsEndTime());
                        //if we're after the end appeals, do nothing
                        if(o.getAppealsEndTime() <= 0) {
                          
                            
                        } else if (o.getAppealsStartTime() <= 0) { //during appeals phase
                            if(!pending) 
                                timerSpt.fireTimerUpdate(new TimerEvent(this, (int)o.getAppealsEndTime()));
                            else {
                                pending = false;
                                phaseSpt.fireComponentAppeals(new PhaseEvent(this, (int)o.getAppealsEndTime()));
                            }
                        } else { //before appeals, do nothing
                            pending = true;
                        }
			// ------- RoomWinner
		} else if (message instanceof RoomWinner) {
			RoomWinner o = (RoomWinner) message;
			// Fire the room winner event
			roomSpt.fireRoomWinner(new RoomWinnerEvent(this, o.getRoom().getRoomID(), o.getCoder().getHandle()));
			// ------- TeamMessages
		} else if (message instanceof DefineWeakestLinkTeam) {
			DefineWeakestLinkTeam o = (DefineWeakestLinkTeam) message;
			// Fire the define team event
			teamSpt.fireDefineTeam(new TeamEvent(this, o.getTeamID(), o.getTeamName(), o.getRoundID(), o.getCoderIDs()));
			// ------- Show Team messages
		} else if (message instanceof ShowWeakestLinkTeam) {
			ShowWeakestLinkTeam o = (ShowWeakestLinkTeam) message;
			// Fire the define team event
			teamSpt.fireShowTeam(new TeamEvent(this, o.getTeamID()));
			// ------- VotingMessage
		} else if (message instanceof WeakestLinkVote) {
			WeakestLinkVote o = (WeakestLinkVote) message;
			// Fire the voted for event
			voteSpt.fireVotedFor(new VoteEvent(this, o.getVoterID(), o.getVictimID()));
			// ------- VotedOutMessage
		} else if (message instanceof WeakestLinkElimination) {
			WeakestLinkElimination o = (WeakestLinkElimination) message;
			// Fire the voted out event
			voteSpt.fireVotedOut(new VoteEvent(this, o.getVictimID()));
			// ------- VotedOutMessage
		} else if (message instanceof ShowInitial) {
			// Fire the voted out event
			GUIController.getInstance().showInitialRenderer();
        } else if (message instanceof ShowImage) {
            GUIController.getInstance().showImage(((ShowImage) message).getImagePath());
        } else if (message instanceof ShowStudio) {
            ShowStudio o = (ShowStudio)message;
			GUIController.getInstance().showStudioRenderer(o.getComputerNames(),o.getPath(), o.getHandles(), o.getTitle());
                        
            int timeLeft = 60;
            // Set the deadline here
            String deadline = o.getTime();
            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
            Calendar fc = format.getCalendar();
            try {
                format.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, fc.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, fc.get(Calendar.MINUTE));
            c.set(Calendar.SECOND, fc.get(Calendar.SECOND));
            timeLeft = (int)((c.getTimeInMillis() - System.currentTimeMillis())/1000);
            if(timeLeft < 0)
                timeLeft = 0;
                       
            timerSpt.fireTimerUpdate(new TimerEvent(this, timeLeft));
        } else if (message instanceof ShowStudioIndividiualResults) {
            ShowStudioIndividiualResults o = (ShowStudioIndividiualResults)message;
            GUIController.getInstance().showStudioIndResultsRenderer(o.getCaption(), o.getImage());
        } else if (message instanceof ShowNoHeaderScreen) {
            ShowNoHeaderScreen o = (ShowNoHeaderScreen)message;
			GUIController.getInstance().showNoHeaderScreen(o.getComputerNames(),o.getPath(), o.getHandles(), o.getTitle());
                        
            int timeLeft = 60;
            // Set the deadline here
            String deadline = o.getTime();
            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
            Calendar fc = format.getCalendar();
            try {
                format.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, fc.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, fc.get(Calendar.MINUTE));
            c.set(Calendar.SECOND, fc.get(Calendar.SECOND));
            timeLeft = (int)((c.getTimeInMillis() - System.currentTimeMillis())/1000);
            if(timeLeft < 0)
                timeLeft = 0;
                       
            timerSpt.fireTimerUpdate(new TimerEvent(this, timeLeft));
        } else if (message instanceof ShowDesign) {
            ShowDesign o = (ShowDesign)message;
			GUIController.getInstance().showDesignRenderer(o.getComputerNames(),o.getPath(), o.getHandles(), o.getTitle());
                        
            int timeLeft = 60;
            // Set the deadline here
            String deadline = o.getTime();
            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
            Calendar fc = format.getCalendar();
            try {
                format.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, fc.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, fc.get(Calendar.MINUTE));
            c.set(Calendar.SECOND, fc.get(Calendar.SECOND));
            timeLeft = (int)((c.getTimeInMillis() - System.currentTimeMillis())/1000);
            if(timeLeft < 0)
                timeLeft = 0;
                       
            timerSpt.fireTimerUpdate(new TimerEvent(this, timeLeft));
        } else if (message instanceof ShowDevelopment) {
            ShowDevelopment o = (ShowDevelopment)message;
			GUIController.getInstance().showDevelopmentRenderer(o.getComputerNames(),o.getPath(), o.getHandles(), o.getTitle());
                        
            int timeLeft = 60;
            // Set the deadline here
            String deadline = o.getTime();
            DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.US);
            Calendar fc = format.getCalendar();
            try {
                format.parse(deadline);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, fc.get(Calendar.HOUR_OF_DAY));
            c.set(Calendar.MINUTE, fc.get(Calendar.MINUTE));
            c.set(Calendar.SECOND, fc.get(Calendar.SECOND));
            timeLeft = (int)((c.getTimeInMillis() - System.currentTimeMillis())/1000);
            if(timeLeft < 0)
                timeLeft = 0;
                       
            timerSpt.fireTimerUpdate(new TimerEvent(this, timeLeft));
		} else if (message instanceof AnnounceDesignReviewBoard) {
			AnnounceDesignReviewBoard o = (AnnounceDesignReviewBoard) message;
			// Create images out of the bytes
			byte[][] imageBytes = o.getImages();
			Image[] images = new Image[imageBytes.length];
			for (int x = 0; x < imageBytes.length; x++) {
				images[x] = Toolkit.getDefaultToolkit().createImage(imageBytes[x]);
			}
			CommonRoutines.loadImagesFully(images);
			// Send the event
			AnnounceDesignReviewBoardEvent evt = new AnnounceDesignReviewBoardEvent(this, o.getRoundID(), o.getHandles(), o.getTcRatings(), o.getTcsRatings(), images);
			announceCoderSpt.fireAnnounceDesignReviewBoard(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
		} else if (message instanceof AnnounceDesignReviewBoardResults) {
			AnnounceDesignReviewBoardResults o = (AnnounceDesignReviewBoardResults) message;
			// Send the event
			AnnounceDesignReviewBoardResultsEvent evt = new AnnounceDesignReviewBoardResultsEvent(this, o.getRoundID(), o.getCoders(), o.getReviewerNames(), o.getScores(), o.getFinalScores());
			announceCoderSpt.fireAnnounceDesignReviewBoardResults(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
		} else if (message instanceof AnnounceDevelopmentReviewBoard) {
			AnnounceDevelopmentReviewBoard o = (AnnounceDevelopmentReviewBoard) message;
			// Create images out of the bytes
			byte[][] imageBytes = o.getImages();
			Image[] images = new Image[imageBytes.length];
			for (int x = 0; x < imageBytes.length; x++) {
				images[x] = Toolkit.getDefaultToolkit().createImage(imageBytes[x]);
			}
			CommonRoutines.loadImagesFully(images);
			// Send the event
			AnnounceDevelopmentReviewBoardEvent evt = new AnnounceDevelopmentReviewBoardEvent(this, o.getRoundID(), o.getHandles(), o.getTcRatings(), o.getTcsRatings(), images);
			announceCoderSpt.fireAnnounceDevelopmentReviewBoard(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
		} else if (message instanceof AnnounceDevelopmentReviewBoardResults) {
			AnnounceDevelopmentReviewBoardResults o = (AnnounceDevelopmentReviewBoardResults) message;
			// Send the event
			AnnounceDevelopmentReviewBoardResultsEvent evt = new AnnounceDevelopmentReviewBoardResultsEvent(this, o.getRoundID(), o.getCoders(), o.getReviewerNames(), o.getScores(), o.getFinalScores());
			announceCoderSpt.fireAnnounceDevelopmentReviewBoardResults(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
		} else if (message instanceof AnnounceTCSCoder) {
			AnnounceTCSCoder o = (AnnounceTCSCoder) message;
			Image image = Toolkit.getDefaultToolkit().createImage(o.getImage());
			CommonRoutines.loadImagesFully(new Image[] { image });
			// Send the event
			AnnounceTCSCoderEvent evt = new AnnounceTCSCoderEvent(this, o.getRoundID(), o.getCoderName(), o.getCoderType(), image, o.getHandle(), o.getTcRating(), o.getTcsRating(), o.getSeed(), o
						.getEarnings(), o.getTournamentNumberSubmissions(), o.getTournamentLevel1Average(), o.getTournamentLevel2Average(), o.getTournamentWins(), o.getLifetimeNumberSubmissions(), o
						.getLifetimeLevel1Average(), o.getLifetimeLevel2Average(), o.getLifetimeWins(), o.getSchool());
			announceCoderSpt.fireAnnounceTCSCoder(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
			// Announce Table Results (for TCO08 marathon, design, development and studio)
		} else if (message instanceof AnnounceTableResults) {
			AnnounceTableResults o = (AnnounceTableResults) message;
			// Send the event
			AnnounceTableResultsEvent evt = new AnnounceTableResultsEvent(this, o.getRoundID(), o.getCoders(), o.getColumnHeaders(), o.getScores(), o.getHighlights(), o.getRanks());
			announceCoderSpt.fireAnnounceTableResults(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
			// Announce TCS Winners
		} else if (message instanceof AnnounceTCSWinners) {
			AnnounceTCSWinners o = (AnnounceTCSWinners) message;
			Image designImg = Toolkit.getDefaultToolkit().createImage(o.getDesignImage());
			Image developImg = Toolkit.getDefaultToolkit().createImage(o.getDevelopmentImage());
			CommonRoutines.loadImagesFully(new Image[] { designImg, developImg });
			// Send the event
			AnnounceTCSWinnersEvent evt = new AnnounceTCSWinnersEvent(this, o.getRoundID(), o.getDesignHandle(), designImg, o.getDesignWinnerAverage(), o.getDevelopmentHandle(), developImg, o
						.getDevelopmentWinnerAverage(), o.getDesignRating(), o.getDevelopmentRating());
			announceCoderSpt.fireAnnounceTCSWinners(evt);
			// Update the current phase to the announcment phase
            timerSpt.fireTimerUpdate(new TimerEvent(this, 0));
			phaseSpt.fireAnnouncement(new PhaseEvent(this));
			// Show the system test results for a problem
		} else if (message instanceof ShowSystemTestResultsByProblem) {
			ShowSystemTestResultsByProblem o = (ShowSystemTestResultsByProblem) message;
			// Send the event
			SystemTestResultsEvent evt = new SystemTestResultsEvent(this, o.getRoundID(), o.getProblemID(), o.getDelay());
			systemTestResultsSpt.fireSystemTestResultsByProblem(evt);
			// Show the system test results for a problem
		} else if (message instanceof ShowSystemTestResultsByCoder) {
			ShowSystemTestResultsByCoder o = (ShowSystemTestResultsByCoder) message;
			// Send the event
			SystemTestResultsEvent evt = new SystemTestResultsEvent(this, o.getRoundID(), o.getCoderID(), o.getDelay());
			systemTestResultsSpt.fireSystemTestResultsByCoder(evt);
		} else if (message instanceof ShowSystemTestResultsByCoderAll) {
			ShowSystemTestResultsByCoderAll o = (ShowSystemTestResultsByCoderAll) message;
			SystemTestResultsEvent evt = new SystemTestResultsEvent(this, o.getRoundID(), 0, o.getDelay());
			systemTestResultsSpt.fireSystemTestResultsByCoderAll(evt);
			// System test for a coder started
		} else if (message instanceof ShowPlacement) {
			ShowPlacement o = (ShowPlacement) message;
			// Send the event
			placements = o.getPlacements();
			ShowPlacementEvent evt = new ShowPlacementEvent(this, o.getPlacements());
			showPlacementSupport.fireUpdatePlacement(evt);
		} else if (message instanceof ShowTCSPlacement) {
			ShowTCSPlacement o = (ShowTCSPlacement) message;
			ShowTCSPlacementEvent evt = new ShowTCSPlacementEvent(this, o.getPlacements(), o.getRoundID());
			showPlacementSupport.fireUpdateTCSPlacement(evt);
		} else if (message instanceof ShowScreen) {
            ShowScreen o = (ShowScreen) message;
            ShowScreenEvent evt = new ShowScreenEvent(this, o.getScreens());
            showScreenSupport.fireUpdateScreen(evt);
			// Define a new component contest
		} else if (message instanceof DefineComponentContestConnection) {
			DefineComponentContestConnection o = (DefineComponentContestConnection) message;
			componentConnectionSpt.fireDefineContest(o.getContestID(), o.getRoundID(), o.getComponentID(), o.getUrl(), o.getPollTime());
			// Define a new component contest
		} else if (message instanceof DefineComponentContest) {
			DefineComponentContest o = (DefineComponentContest) message;
			componentContestSpt.fireDefineContest(o.getContestID(), o.getRoundID(), o.getComponentData(), o.getCoderData(), o.getReviewBoardMembers());
			// Send a score update
		} else if (message instanceof ComponentScoreUpdate) {
			ComponentScoreUpdate o = (ComponentScoreUpdate) message;
			componentContestSpt.fireScoreUpdate(o.getContestID(), o.getRoundID(), o.getComponentID(), o.getCoderID(), o.getReviewerCoderID(), o.getScore());
			// Send an appeals update
		} else if (message instanceof ComponentAppeal) {
			ComponentAppeal o = (ComponentAppeal) message;
			final Constants.AppealStatus status;
			if (o.getStatus().equals(ComponentAppeal.APPEAL_SUCCESSFUL)) {
				status = Constants.AppealStatus.Successful;
			} else if (o.getStatus().equals(ComponentAppeal.APPEAL_REJECTED)) {
				status = Constants.AppealStatus.Failed;
			} else if (o.getStatus().equals(ComponentAppeal.APPEAL_PENDING)) {
				status = Constants.AppealStatus.Pending;
			} else {
				cat.warn("Unknown component appeal status: " + message);
				return;
			}
			final ComponentAppealEvent event = new ComponentAppealEvent(this, o.getContestID(), o.getRoundID(), o.getComponentID(), o.getAppealID(),
						o.getCoderID(), o.getReviewerCoderID(), status);
			
			componentContestSpt.fireAppealUpdate(event);
			// ------- Unknown
		} else if (message instanceof ShowComponentResultsByComponentID) {
			ShowComponentResultsByComponentID o = (ShowComponentResultsByComponentID) message;
			ShowComponentResultsEvent event = new ShowComponentResultsEvent(this, o.getContestID(), o.getRoundID(), o.getDelay());
			componentContestSpt.fireShowComponentResults(event);
		} else {
			cat.warn("Unknown message: " + message.toString());
		}
	}

	/**
	 * Adds a listener of type ConnectionListener
	 * 
	 * @param listener the listener to add
	 */
	public void addConnectionListener(ConnectionListener listener) {
		connectionSpt.addConnectionListener(listener);
	}

	/**
	 * Removes a listener of type ConnectionListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeConnectionListener(ConnectionListener listener) {
		connectionSpt.removeConnectionListener(listener);
	}

	/**
	 * Adds a listener of type LoginListener
	 * 
	 * @param listener the listener to add
	 */
	public void addLoginListener(LoginListener listener) {
		loginSpt.addLoginListener(listener);
	}

	/**
	 * Removes a listener of type LoginListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeLoginListener(LoginListener listener) {
		loginSpt.removeLoginListener(listener);
	}

	/**
	 * Adds a listener of type TimerListener
	 * 
	 * @param listener the listener to add
	 */
	public void addTimerListener(TimerListener listener) {
		timerSpt.addTimerListener(listener);
	}

	/**
	 * Removes a listener of type TimerListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeTimerListener(TimerListener listener) {
		timerSpt.removeTimerListener(listener);
	}

	/**
	 * Adds a listener of type PhaseListener
	 * 
	 * @param listener the listener to add
	 */
	public void addPhaseListener(PhaseListener listener) {
		phaseSpt.addListener(listener);
	}

	/**
	 * Adds a listener of type ComponentContestConnectionListener
	 * 
	 * @param listener the listener to add
	 */
	public void addComponentContestConnectionListener(ComponentContestConnectionListener listener) {
		componentConnectionSpt.addListener(listener);
	}

	/**
	 * Adds a listener of type ComponentContestListener
	 * 
	 * @param listener the listener to add
	 */
	public void addComponentContestListener(ComponentContestListener listener) {
		componentContestSpt.addListener(listener);
	}

	/**
	 * Remove a listener of type ComponentContestListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeComponentContestListener(ComponentContestListener listener) {
		componentContestSpt.removeListener(listener);
	}

	/**
	 * Remove a listener of type ComponentContestConnectionListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeComponentContestConnectionListener(ComponentContestConnectionListener listener) {
		componentConnectionSpt.removeListener(listener);
	}

	/**
	 * Removes a listener of type PhaseListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removePhaseListener(PhaseListener listener) {
		phaseSpt.addListener(listener);
	}

	/**
	 * Adds a listener of type RoomListener
	 * 
	 * @param listener the listener to add
	 */
	public void addRoomListener(RoomListener listener) {
		roomSpt.addRoomListener(listener);
	}

	/**
	 * Removes a listener of type RoomListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeRoomListener(RoomListener listener) {
		roomSpt.removeRoomListener(listener);
	}

	/**
	 * Adds a listener of type ProblemListener
	 * 
	 * @param listener the listener to add
	 */
	public void addProblemListener(ProblemListener listener) {
		problemSpt.addProblemListener(listener);
	}

	/**
	 * Removes a listener of type ProblemListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeProblemListener(ProblemListener listener) {
		problemSpt.removeProblemListener(listener);
	}

	/**
	 * Adds a listener of type ProblemResultListener
	 * 
	 * @param listener the listener to add
	 */
	public void addProblemResultListener(ProblemResultListener listener) {
		problemResultSpt.addProblemResultListener(listener);
	}

	/**
	 * Removes a listener of type ProblemResultListener
	 * 
	 * @param listener the listener to remove
	 */
	public void removeProblemResultListener(ProblemResultListener listener) {
		problemResultSpt.removeProblemResultListener(listener);
	}

	/**
	 * Adds a listener of type ContestListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addContestListener(ContestListener listener) {
		contestSpt.addContestListener(listener);
	}

	/**
	 * Removes a listener of type ContestListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeContestListener(ContestListener listener) {
		contestSpt.removeContestListener(listener);
	}

	/**
	 * Adds a listener of type RoundListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addRoundListener(RoundListener listener) {
		roundSpt.addRoundListener(listener);
	}

	/**
	 * Removes a listener of type RoundListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeRoundListener(RoundListener listener) {
		roundSpt.removeRoundListener(listener);
	}

	/**
	 * Adds a listener of type AnnounceCoderListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addAnnounceCoderListener(AnnouncementListener listener) {
		announceCoderSpt.addAnnounceCoderListener(listener);
	}

	/**
	 * Removes a listener of type AnnounceCoderListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeAnnounceCoderListener(AnnouncementListener listener) {
		announceCoderSpt.removeAnnounceCoderListener(listener);
	}

	/**
	 * Adds a listener of type TeamListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addTeamListener(TeamListener listener) {
		teamSpt.addTeamListener(listener);
	}

	/**
	 * Removes a listener of type TeamListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeTeamListener(TeamListener listener) {
		teamSpt.removeTeamListener(listener);
	}

	/**
	 * Adds a listener of type VoteListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addVoteListener(VoteListener listener) {
		voteSpt.addVoteListener(listener);
	}

	/**
	 * Removes a listener of type VoteListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeVoteListener(VoteListener listener) {
		voteSpt.removeVoteListener(listener);
	}

	/**
	 * Adds a listener of type SystemTestResultsListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addSystemTestResultsListener(SystemTestResultsListener listener) {
		systemTestResultsSpt.addSystemTestResultsListener(listener);
	}

	/**
	 * Removes a listener of type SystemTestResultsListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeSystemTestResultsListener(SystemTestResultsListener listener) {
		systemTestResultsSpt.removeSystemTestResultsListener(listener);
	}

	/**
	 * Adds a listener of type PlacementChangeListener
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addShowPlacementListener(ShowPlacementListener listener) {
		showPlacementSupport.addListener(listener);
	}

	/**
	 * Removes a listener of type SystemTestResultsListener
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeShowPlacementListener(ShowPlacementListener listener) {
		showPlacementSupport.removeListener(listener);
	}

	/**
	 * Adds a listener of type Screen change
	 * 
	 * @param listener the listener to add
	 */
	public synchronized void addShowScreenListener(ShowScreenListener listener) {
		showScreenSupport.addListener(listener);
	}

	/**
	 * Removes a listener of type Screen change
	 * 
	 * @param listener the listener to remove
	 */
	public synchronized void removeShowScreenListener(ShowScreenListener listener) {
		showScreenSupport.removeListener(listener);
	}
	
	public synchronized int[] getShowPlacements() {
        return placements;
	}
}
