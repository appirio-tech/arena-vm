/*
 * SystemTestResultPartialComparator
 *
 * Created 03/22/2007
 */
package com.topcoder.server.ejb.TestServices.to;

import java.util.Comparator;


/**
 * This comparator gives a partial order. It only compares
 * some of the fields.
 * 
 * sort order is  : coderId, componentId, roundId, systemTestVersion(desc)
 * @author Diego Belfer (mural)
 * @version $Id: SystemTestResultPartialComparator.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class SystemTestResultPartialComparator implements Comparator {
    public final static SystemTestResultPartialComparator INSTANCE = new SystemTestResultPartialComparator();

    public int compare(Object obj1, Object obj2) {
        SystemTestResult one = (SystemTestResult) obj1;
        SystemTestResult other = (SystemTestResult) obj2;
        if (one.coderId < other.coderId) return -1;
        else if (one.coderId > other.coderId) return 1;
        if (one.componentId < other.componentId) return -1;
        else if (one.componentId > other.componentId) return 1;
        //WE DON'T NEED TO CHECK THE CONTEST. Rounds belongs to only one contest
        if (one.roundId < other.roundId) return -1;
        else if (one.roundId > other.roundId) return 1;
        if (one.systemTestVersion > other.systemTestVersion) return -1;
        else if (one.systemTestVersion < other.systemTestVersion) return 1;
        return 0;
    }
}