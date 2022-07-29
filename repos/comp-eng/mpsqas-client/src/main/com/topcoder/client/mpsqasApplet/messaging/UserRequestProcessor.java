package com.topcoder.client.mpsqasApplet.messaging;

import java.util.*;

/**
 * @author mitalub
 */
public interface UserRequestProcessor {

    public abstract void payUsers(ArrayList userIds);

    public abstract void savePendingPayments(HashMap payments);
}
