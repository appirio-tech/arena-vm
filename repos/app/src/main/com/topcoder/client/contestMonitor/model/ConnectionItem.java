package com.topcoder.client.contestMonitor.model;

import java.util.StringTokenizer;

public class ConnectionItem {

    private static final int[] PERIOD = {1, 5, 10};

    static final int MAX_PERIODS = PERIOD.length + 1;

    private static final int MAX_TIME = 10;

    private final Integer serverId;
    private final Integer connId;
    private final String ip;
    private final String alignedIP;
    private final int[] total = new int[MAX_PERIODS];
    private final int[] time = new int[MAX_TIME];

    private String username = "";

    ConnectionItem(int serverId, int connId, String ip) {
        this.serverId = new Integer(serverId);
        this.connId = new Integer(connId);
        this.ip = ip;
        alignedIP = getAlignedIP(ip);
    }

    private static String getAlignedIP(String ip) {
        StringTokenizer tk = new StringTokenizer(ip, ".");
        String r = "";
        while (tk.hasMoreTokens()) {
            String s = tk.nextToken();
            while (s.length() < 3) {
                s = '0' + s;
            }
            r += s;
        }
        return r;
    }

    public String toString() {
        return "serverID=" + serverId + ", connID=" + connId + ", username=" + username + ", IP=" + ip;
    }

    public Integer getServerId() {
        return serverId;
    }

    public Integer getConnId() {
        return connId;
    }

    public String getIp() {
        return ip;
    }

    String getAlignedIP() {
        return alignedIP;
    }

    void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    int getTotal(int ind) {
        return total[ind];
    }

    private void recalc() {
        for (int i = 0; i < MAX_PERIODS - 1; i++) {
            int sum = 0;
            int lim = PERIOD[i];
            for (int j = 0; j < lim; j++) {
                sum += time[j];
            }
            total[i] = sum;
        }
    }

    void add(int num) {
        for (int i = time.length - 1; i > 0; i--) {
            time[i] = time[i - 1];
        }
        time[0] = num;
        total[MAX_PERIODS - 1] += num;
        recalc();
    }

}
