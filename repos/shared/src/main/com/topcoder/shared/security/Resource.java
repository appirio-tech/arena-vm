package com.topcoder.shared.security;

import java.io.Serializable;


/**
 * interface for resources, they could be web pages, web applications
 * etc.  anything that one might require authentication for access to.
 *
 * @author dok
 * @version $Id$
 */
public interface Resource extends Serializable {

    public String getName();
}
