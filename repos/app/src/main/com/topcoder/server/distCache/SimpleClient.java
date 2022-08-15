package com.topcoder.server.distCache;

import java.io.*;
import java.rmi.RemoteException;

public class SimpleClient {

    static CacheClient client = CacheClientFactory.createCacheClient();

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            while (readEval(in)) {
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


    static boolean readEval(BufferedReader in)
            throws IOException {
        System.out.print("> ");
        System.out.flush();

        String line = in.readLine();

        if (line == null) {
            return false;
        }

        return processLine(line);
    }

    static boolean processLine(String line) {
        line = line.trim();
        if (line.length() == 0) {
            return true;
        }

        if (line.equals("info")) {
            System.out.println("client: " + client);
        } else if (line.equals("bulk")) {
            try {
                for (int i = 0; i < 100000; i++) {
                    client.set("foo." + i, new Integer(i));
                    if (i % 10000 == 0) {
                        System.out.print(".");
                    }
                }
                System.out.println();

            } catch (RemoteException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        } else if (line.startsWith("lock ")) {
            String key = line.substring(line.indexOf(' ') + 1);
            try {
                System.out.println("LOCKED " + key + "=" +
                        client.getAndLock(key));
            } catch (RemoteException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }

        } else if (line.startsWith("unlock ")) {
            String key = line.substring(line.indexOf(' ') + 1);
            try {
                client.releaseLock(key);
                System.out.println("UNLOCKED " + key);
            } catch (RemoteException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            try {
                int pos = line.indexOf('=');
                if (pos == -1) {
                    System.out.println(line + "=" + client.get(line));
                } else {
                    client.set(line.substring(0, pos), line.substring(pos + 1));
                }
            } catch (RemoteException e) {
                System.out.println("Exception: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return true;
    }

}
