/*
 * TeamListList.java Created on June 27, 2002, 12:30 AM
 */

package com.topcoder.netCommon.contestantMessages.lists;

import java.util.Collection;

/**
 * Defines a list of team information.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: TeamListList.java 72143 2008-08-06 05:54:59Z qliu $
 */
public class TeamListList extends ListWrapper {
    /**
     * Creates a new instance of TeamListList. Initially, there is no item in the list.
     */
    public TeamListList() {
        super(0);
    }

    /**
     * Creates a new instance of TeamListList. The team information list is given as a collection.
     * 
     * @param al the collection of team information.
     */
    public TeamListList(Collection al) {
        super(al);
    }

    /**
     * Gets the team information at the index.
     * 
     * @param index the index to be get.
     * @return the team information at the index.
     */
    public TeamRowList getRow(int index) {
        return (TeamRowList) super.get(index);
    }
}
