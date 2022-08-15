package com.topcoder.client.mpsqasApplet.view;


public interface MainUserRoomView
        extends View {

    public abstract int getSelectedUserIndex();

    public abstract boolean isPaid(int index);
}
