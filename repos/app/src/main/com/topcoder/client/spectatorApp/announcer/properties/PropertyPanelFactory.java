package com.topcoder.client.spectatorApp.announcer.properties;

import com.topcoder.client.spectatorApp.announcer.events.AnnounceCoderEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceReviewBoardEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceReviewBoardResultsEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceTableResultsEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceTCSCoderEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnounceTCSWinnersEvent;
import com.topcoder.client.spectatorApp.announcer.events.AnnouncerEvent;
import com.topcoder.client.spectatorApp.announcer.events.DefineComponentContestConnectionEvent;
import com.topcoder.client.spectatorApp.announcer.events.DefineContestEvent;
import com.topcoder.client.spectatorApp.announcer.events.DefineRoundEvent;
import com.topcoder.client.spectatorApp.announcer.events.IgnorePhaseChangeEvent;
import com.topcoder.client.spectatorApp.announcer.events.SetRoomWinnerEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowComponentEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowComponentResultsByComponentIDEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowImageEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowPhaseChangeEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowPlacementEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowScreenEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowRoundEvent;
import com.topcoder.client.spectatorApp.announcer.events.AbstractShowScreenEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowStudioIndividualResultsEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowSystemTestResultsByCoderAllEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowSystemTestResultsByCoderEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowSystemTestResultsByProblemEvent;
import com.topcoder.client.spectatorApp.announcer.events.ShowTCSPlacementEvent;

/**
 * The property panel factory for generating property panels based on the given
 * event..
 * 
 * @author Tim
 * @version 1.0
 */
public class PropertyPanelFactory {
	/**
	 * Private constructor to prevent instantiation
	 */
	private PropertyPanelFactory() {}

	/**
	 * Returns a property panel for the given event. If the event is
	 * unrecognizable, it returns a TitlePropertyPanel
	 * 
	 * @param event the event to generate a property panel for
	 * @return the PropertyPanel
	 */
	public static final PropertyPanel getPropertyPanel(AnnouncerEvent event) {
		if (event instanceof AnnounceCoderEvent) return new AnnounceCoderPropertyPanel((AnnounceCoderEvent) event);
		if (event instanceof AnnounceReviewBoardEvent) return new AnnounceReviewBoardPropertyPanel((AnnounceReviewBoardEvent) event);
		if (event instanceof AnnounceReviewBoardResultsEvent) return new AnnounceReviewBoardResultsPropertyPanel((AnnounceReviewBoardResultsEvent) event);
		if (event instanceof AnnounceTCSCoderEvent) return new AnnounceTCSCoderPropertyPanel((AnnounceTCSCoderEvent) event);
		if (event instanceof AnnounceTCSWinnersEvent) return new AnnounceTCSWinnerPropertyPanel((AnnounceTCSWinnersEvent) event);
		if (event instanceof DefineContestEvent) return new DefineContestPropertyPanel((DefineContestEvent) event);
		if (event instanceof DefineRoundEvent) return new DefineRoundPropertyPanel((DefineRoundEvent) event);
		if (event instanceof DefineComponentContestConnectionEvent) return new DefineComponentContestConnectionPropertyPanel((DefineComponentContestConnectionEvent) event);
		if (event instanceof SetRoomWinnerEvent) return new SetRoomWinnerPropertyPanel((SetRoomWinnerEvent) event);
		if (event instanceof ShowComponentResultsByComponentIDEvent) return new ShowComponentResultsByComponentIDPanel((ShowComponentResultsByComponentIDEvent) event);
		if (event instanceof ShowPhaseChangeEvent) return new ShowPhaseChangePropertyPanel((ShowPhaseChangeEvent) event);
		if (event instanceof ShowRoundEvent) return new ShowRoundPropertyPanel((ShowRoundEvent) event);
		if (event instanceof ShowComponentEvent) return new ShowComponentPropertyPanel((ShowComponentEvent) event);
		if (event instanceof ShowPlacementEvent) return new ShowPlacementPropertyPanel((ShowPlacementEvent) event);
		if (event instanceof ShowTCSPlacementEvent) return new ShowTCSPlacementPropertyPanel((ShowTCSPlacementEvent) event);
		if (event instanceof ShowScreenEvent) return new ShowScreenPropertyPanel((ShowScreenEvent) event);
		if (event instanceof ShowSystemTestResultsByCoderEvent) return new ShowSystemTestResultsByCoderPanel((ShowSystemTestResultsByCoderEvent) event);
		if (event instanceof ShowSystemTestResultsByCoderAllEvent) return new ShowSystemTestResultsByCoderAllPanel((ShowSystemTestResultsByCoderAllEvent) event);
		if (event instanceof ShowSystemTestResultsByProblemEvent) return new ShowSystemTestResultsByProblemPanel((ShowSystemTestResultsByProblemEvent) event);
        if (event instanceof AbstractShowScreenEvent) return new ShowStudioPropertyPanel((AbstractShowScreenEvent)event);
        if (event instanceof ShowStudioIndividualResultsEvent) return new ShowStudioIndividualResultsPropertyPanel((ShowStudioIndividualResultsEvent)event);
        if (event instanceof AnnounceTableResultsEvent) return new AnnounceTableResultsPropertyPanel((AnnounceTableResultsEvent)event);
        if (event instanceof IgnorePhaseChangeEvent) return new IgnorePhaseChangePropertyPanel((IgnorePhaseChangeEvent)event);
        if (event instanceof ShowImageEvent) return new ShowImagePropertyPanel((ShowImageEvent)event);
		return new TitlePropertyPanel(event);
	}
}
