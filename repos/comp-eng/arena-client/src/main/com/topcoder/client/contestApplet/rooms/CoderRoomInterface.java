package com.topcoder.client.contestApplet.rooms;


public interface CoderRoomInterface {
    public void closeCodingWindow();

    public void challengeButtonEvent(java.awt.event.ActionEvent e);
    
    boolean isCodingWindowOpened();
}
