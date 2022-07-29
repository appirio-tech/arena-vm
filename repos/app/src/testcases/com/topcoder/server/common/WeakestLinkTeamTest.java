package com.topcoder.server.common;

import java.util.Arrays;

import junit.framework.TestCase;

public final class WeakestLinkTeamTest extends TestCase {

    private static WeakestLinkTeam.Entry getEntry(int coderId, double points) {
        return getEntry(coderId, points, 0);
    }

    private static WeakestLinkTeam.Entry getEntry(int coderId, double points, double pointsSum) {
        return getEntry(coderId, points, pointsSum, 0);
    }

    private static WeakestLinkTeam.Entry getEntry(int coderId, double points, double pointsSum, double qualPoints) {
        return getEntry(coderId, points, pointsSum, qualPoints, 0);
    }

    private static WeakestLinkTeam.Entry getEntry(int coderId, double points, double pointsSum, double qualPoints, int votes) {
        WeakestLinkCoder coder = new WeakestLinkCoder(coderId, pointsSum, qualPoints, 1, null);
        for (int i = 0; i < votes; i++) {
            coder.plusOneVote();
        }
        return new WeakestLinkTeam.Entry(coder, points);
    }

    public static void testSortByPoints() {
        WeakestLinkTeam.Entry[] entries = {
            getEntry(0, 1),
            getEntry(1, 0),
            getEntry(2, 2),
        };
        Arrays.sort(entries);
        assertEquals(2, entries[0].getCoderId());
        assertEquals(0, entries[1].getCoderId());
        assertEquals(1, entries[2].getCoderId());
    }

    public static void testSortByPointsSum() {
        WeakestLinkTeam.Entry[] entries = {
            getEntry(0, 0, 1),
            getEntry(1, 0, 0),
            getEntry(2, 0, 2),
        };
        Arrays.sort(entries);
        assertEquals(2, entries[0].getCoderId());
        assertEquals(0, entries[1].getCoderId());
        assertEquals(1, entries[2].getCoderId());
    }

    public static void testSortByQualPoints() {
        WeakestLinkTeam.Entry[] entries = {
            getEntry(0, 0, 0, 1),
            getEntry(1, 0, 0, 0),
            getEntry(2, 0, 0, 2),
        };
        Arrays.sort(entries);
        assertEquals(2, entries[0].getCoderId());
        assertEquals(0, entries[1].getCoderId());
        assertEquals(1, entries[2].getCoderId());
    }

    public static void testSortByVotes() {
        WeakestLinkTeam.Entry[] entries = {
            getEntry(0, 0, 0, 0, 1),
            getEntry(1, 0, 0, 0, 0),
            getEntry(2, 0, 0, 0, 2),
        };
        Arrays.sort(entries);
        assertEquals(1, entries[0].getCoderId());
        assertEquals(0, entries[1].getCoderId());
        assertEquals(2, entries[2].getCoderId());
    }

}
