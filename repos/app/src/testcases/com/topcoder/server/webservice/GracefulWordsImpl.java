package com.topcoder.server.webservice;

import java.util.*;

public class GracefulWordsImpl implements GracefulWordsIF {

    public GracefulWordsImpl() {
    }

    public String[] breakDownSentence(String s) {
        String valid = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < s.length(); i++) {
            if (valid.indexOf(s.charAt(i)) == -1) {
                s = s.substring(0, i) + s.substring(i + 1, s.length());
                i--;
            }
        }
        Vector a = new Vector();
        StringTokenizer b = new StringTokenizer(s);
        while (b.hasMoreTokens()) {
            a.add(b.nextToken());
        }
        String[] toreturn = new String[a.size()];
        for (int i = 0; i < a.size(); i++) {
            toreturn[i] = (String) a.elementAt(i);
        }
        return toreturn;
    }

    public int sentenceGrace(String s) {
        StringTokenizer t = new StringTokenizer(s);

        int toreturn = 0;
        while (t.hasMoreTokens()) {
            String a = t.nextToken();
            a = a.toUpperCase();
            char[] c = a.toCharArray();
            Arrays.sort(c);
            int thisgrace = 0;
            char current = ' ';
            int reps = 0;
            for (int i = 0; i < c.length; i++) {
                if (i == 0) {
                    current = c[i];
                    reps = 1;
                } else {
                    if (c[i] == current) {
                        reps++;
                    } else {
                        thisgrace += reps * reps;
                        current = c[i];
                        reps = 1;
                    }
                }
            }
            thisgrace += reps * reps;
//                        System.out.println(thisgrace);
            toreturn += 1000 / thisgrace;
        }
        return toreturn;
    }
}
