package com.topcoder.utilities.contestcreate;

import java.util.ArrayList;
import java.text.ParseException;


public class ListParser {

    public static String[] parseList(String text)
            throws ParseException {
        ArrayList list = new ArrayList();

        boolean seenopen = false;
        String line = null;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!seenopen) {
                if (Character.isWhitespace(c)) {
                    continue;
                }

                if (c != '{') {
                    throw new ParseException("{ expected", i);
                }

                seenopen = true;
                line = "";
            } else {
                if (c == '}') {
                    list.add(line);
                    line = null;
                    seenopen = false;
                } else {
                    line += c;
                }

            }
        }

        if (line != null) {
            throw new ParseException("Unexpected end of line", text.length());
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] split(String text) {
        ArrayList words = new ArrayList();
        int len = text.length();

        boolean inquote = false;
        String word = "";
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (inquote) {
                if (c == '"') {
                    inquote = false;
                } else {
                    word += c;
                }
            } else {
                if (Character.isWhitespace(c)) {
                    if (word.length() > 0) {
                        words.add(word);
                        word = "";
                    }
                } else if (c == '"') {
                    inquote = true;
                } else {
                    word += c;
                }
            }
        }

        if (word.length() > 0) {
            words.add(word);
            word = "";
        }

        return (String[]) words.toArray(new String[words.size()]);
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            try {
                String[] res = parseList(args[i]);

                System.out.println(args[i] + " ==> " + res.length);
                for (int j = 0; j < res.length; j++) {
                    System.out.println("* " + res[j]);
                    String[] words = split(res[j]);
                    for (int k = 0; k < words.length; k++) {
                        System.out.println(":" + words[k] + ":");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


}
