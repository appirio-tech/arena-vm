/*
 * Copyright (c) TopCoder
 *
 * Created on Jul 14, 2011
 */
package com.topcoder.farm.shared.invocation;

/**
 * @author mural
 * @version $Id$
 */
public interface InvocationFeedbackPublisher {
    void publish(Object feedback);
}
