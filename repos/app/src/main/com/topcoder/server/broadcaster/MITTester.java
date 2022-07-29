package com.topcoder.server.broadcaster;

final class MITTester {

    static final int N = 100;

    public static void main(String[] args) {
        MITLocalClient localClient = new MITLocalClient();
        localClient.start();
        for (int i = 0; i < N; i++) {
            localClient.send("" + i);
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        localClient.stop();
    }

}
