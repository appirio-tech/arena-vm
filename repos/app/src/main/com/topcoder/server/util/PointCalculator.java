package com.topcoder.server.util;

import com.topcoder.shared.util.Formatters;

public class PointCalculator {

    static final long CODING_LEN = 46 * 60 * 1000;
    static final int POINTS = 500;

    public static double computeSubmissionPoints(long et, int pointVal, final long codingLength, int numSubmissions) {
        double elapsedTime = (long) et;
        double newPointVal;
        if (elapsedTime <= codingLength) {
            newPointVal = pointVal * (.3 + .7 / (10.0 * Math.pow(elapsedTime / (double) codingLength, 2.0) + 1));
        } else {
            newPointVal = 0;
        }
        newPointVal = Formatters.getDouble(newPointVal).doubleValue();
        if (newPointVal > 0 && numSubmissions > 0) {	 // Penalize them 10%
            double maxPenalty = pointVal * 3 / 10;
            maxPenalty = Formatters.getDouble(maxPenalty).doubleValue();
            double newPenalizedPointValue = newPointVal - ((pointVal * 0.1) * numSubmissions);
            newPenalizedPointValue = Formatters.getDouble(newPenalizedPointValue).doubleValue();
            if (newPenalizedPointValue < maxPenalty) {
                newPointVal = maxPenalty;
            } else {
                newPointVal = newPenalizedPointValue;
            }
        }
        return newPointVal;
    }

    public static void main(String[] args) {
        int pointVal = POINTS;
        long codingLength = CODING_LEN;
        int submissions = 0;
        if (args.length == 2) submissions = Integer.parseInt(args[1]);
        double points = computeSubmissionPoints(Long.parseLong(args[0]), pointVal, codingLength, submissions);
        System.out.println(points);
    }

    public static double computeSubmissionPoints(long et, int submissions) {
        int pointVal = POINTS;
        long codingLength = CODING_LEN;
        return computeSubmissionPoints(et, pointVal, codingLength, submissions);
    }

    public static double computeSubmissionPoints(long et) {
        int pointVal = POINTS;
        long codingLength = CODING_LEN;
        return computeSubmissionPoints(et, pointVal, codingLength, 0);
    }
}
