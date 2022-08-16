package com.topcoder.server.broadcaster;

final class ExodusTester {

    public static void main(String[] args) {
        ExodusLocalClient localClient = new ExodusLocalClient();
        localClient.start();
        try {
            int prev = -1;
            for (; ;) {
                Object obj = localClient.receive();
                System.out.println("received: " + obj);
                String s = (String) obj;
                int id = Integer.parseInt(s);
                if (prev + 1 != id) {
                    throw new RuntimeException("out of order: " + prev + " " + id);
                }
                if (id == MITTester.N - 1) {
                    break;
                }
                prev = id;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        localClient.stop();
    }

}
