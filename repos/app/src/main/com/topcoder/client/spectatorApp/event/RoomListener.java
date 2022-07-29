/**
 * RoomListener.java
 *
 * Description:		Interface for room notifications
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public interface RoomListener extends java.util.EventListener {

    /**
     * Method called to define a room
     *
     * @param evt associated event
     */
    public void defineRoom(DefineRoomEvent evt);

    /**
     * Method called to show a specific room
     *
     * @param evt associated event
     */
    public void showRoom(ShowRoomEvent evt);

    /**
     * Method called to show that a player won a room!
     *
     * @param evt associated event
     */
    public void roomWinner(RoomWinnerEvent evt);

}


/* @(#)RoomListener.java */
