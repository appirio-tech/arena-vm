package com.topcoder.utilities;

import java.io.*;
import java.util.ArrayList;

public class CommentStripper {

    int idx;
    int ptr;
    char[][] ignore;

    public CommentStripper() {
        ArrayList ignore = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader("ignore.txt"));
            String s = br.readLine();
            while (s != null) {
                if (s.length() > 0)
                    ignore.add(s);
                s = br.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ignore = new char[ignore.size()][];
        for (int i = 0; i < ignore.size(); i++) {
            this.ignore[i] = ((String) ignore.get(i)).toCharArray();
        }
    }

    public String stripComments(String input) {
        idx = 0;
        ptr = 0;
        String ret = code((" " + input + " ").toCharArray());
        char noSp[] = new char[ret.length()];
        int ptr = 0;
        for (int i = 0; i < ret.length();) {
            while (i < ret.length() && Character.isWhitespace(ret.charAt(i))) i++;
            if (i < ret.length())
                noSp[ptr++] = ret.charAt(i++);
        }
        return new String(noSp, 0, ptr);
    }

    private String code(char[] chars) {
        boolean deleting = false;
        int brackets = 0;
        char ret[] = new char[chars.length];
        boolean[][] places = new boolean[ignore.length][];
        for (int i = 0; i < places.length; i++)
            places[i] = new boolean[ignore[i].length];
        while (idx < chars.length) {
            boolean reset = true;
            if (chars[idx] == '\'') {
                if (!deleting)
                    ret[ptr++] = chars[idx++];
                else
                    idx++;
                if (chars[idx] == '\\') {
                    if (deleting)
                        idx++;
                    else if (idx < chars.length) ret[ptr++] = chars[idx++];
                }
                if (deleting)
                    idx++;
                else if (idx < chars.length) ret[ptr++] = chars[idx++];
                if (deleting)
                    idx++;
                else if (idx < chars.length) ret[ptr++] = chars[idx++];
            } else if (chars[idx] == '"') {
                if (deleting)
                    idx++;
                else
                    ret[ptr++] = chars[idx++];
                while (idx < chars.length && chars[idx] != '"') {
                    if (chars[idx] == '\\') {
                        if (deleting)
                            idx++;
                        else
                            ret[ptr++] = chars[idx++];
                    }
                    if (deleting)
                        idx++;
                    else
                        ret[ptr++] = chars[idx++];
                }
                if (deleting)
                    idx++;
                else if (idx < chars.length) ret[ptr++] = chars[idx++];
            } else if (chars[idx] == '/' && chars[idx + 1] == '/') {
                while (idx < chars.length && chars[idx] != '\n') idx++;
            } else if (idx + 1 < chars.length && chars[idx] == '/' && chars[idx + 1] == '*') {
                idx += 4;
                while (idx < chars.length && !(chars[idx - 2] == '*' && chars[idx - 1] == '/')) idx++;
            } else if (chars[idx] == ' ' || chars[idx] == '\t' || chars[idx] == '\n' || chars[idx] == '\r') {
                for (int i = 0; i < places.length; i++) {
                    for (int j = places[i].length - 2; j >= 0; j--) {
                        if (places[i][j] && ignore[i][j + 1] == ' ') {
                            places[i][j + 1] = true;
                        }
                        places[i][j] = false;
                    }
                    if (places[i][places[i].length - 1])//match found, delete
                    {
                        ptr = ptr - places[i].length;
                        deleting = true;
                        for (int k = 0; k < places.length; k++)
                            for (int j = 0; j < places[k].length; j++)
                                places[k][j] = false;
                        brackets = -1;
                    }
                    if (' ' == ignore[i][0]) places[i][0] = true;
                }
                if (chars[idx] == '\r')
                    chars[idx] = '\n';
                if (deleting)
                    idx++;
                else
                    ret[ptr++] = chars[idx++];
                while (idx < chars.length && (chars[idx] == ' ' || chars[idx] == '\t' || chars[idx] == '\n' || chars[idx] == '\r')) idx++;
            } else if (deleting) {
                if (chars[idx] == ';' && brackets == -1) {
                    deleting = false;
                }
                if (chars[idx] == '{') {
                    if (brackets == -1)
                        brackets = 1;
                    else
                        brackets++;
                }
                if (chars[idx] == '}') {
                    if (brackets == 1) deleting = false;
                    brackets--;
                }
                idx++;
            } else {
                ret[ptr] = chars[idx];
                for (int i = 0; i < places.length; i++) {
                    for (int j = places[i].length - 2; j >= 0; j--) {
                        if (places[i][j] && ignore[i][j + 1] == chars[idx]) {
                            places[i][j] = false;
                            places[i][j + 1] = true;
                        }
                    }
                    if (places[i][places[i].length - 1])//match found, delete
                    {
                        ptr = ptr - places[i].length;
                        deleting = true;
                        for (int k = 0; k < places.length; k++)
                            for (int j = 0; j < places[k].length; j++)
                                places[k][j] = false;
                        brackets = -1;
                    }
                    if (chars[idx] == ignore[i][0]) places[i][0] = true;
                }
                ptr++;
                idx++;
            }
        }
        String r = new String(ret, 0, ptr);
//        System.out.println(r);
        return r;
    }

    public static void main(String args[]) {
        try {

            FileInputStream fis = new FileInputStream(args[0]);
            byte b[] = new byte[100000];
            int len = fis.read(b);
            String s = new CommentStripper().stripComments(new String(b, 0, len));
            System.out.println("*************\r\n" + s);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
