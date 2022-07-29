package com.topcoder.server.ejb.DBServices;

final class AssignRoomsItem {

    private final int coderID;
    private final int rating;
    private final int rating_no_vol;
    private final String region;
    private final int seed;

    AssignRoomsItem(int coderID, int rating, int rating_no_vol, String region, int seed) {
        this.coderID = coderID;
        this.rating = rating;
        this.rating_no_vol = rating_no_vol;
        this.region = region;
        this.seed = seed;
    }

    String getRegion() {
        return region;
    }

    int getCoderID() {
        return coderID;
    }

    public String toString() {
        return "coderID=" + coderID + ", rating=" + rating + ", rating_no_vol=" + rating_no_vol + ", region=" + region + ", seed=" + seed;
    }

}
