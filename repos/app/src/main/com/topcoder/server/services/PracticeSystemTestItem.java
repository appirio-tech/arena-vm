package com.topcoder.server.services;

final class PracticeSystemTestItem {

    private final int coderID;
    private final long submitTime;

    PracticeSystemTestItem(int coderID, long submitTime) {
        this.coderID = coderID;
        this.submitTime = submitTime;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PracticeSystemTestItem)) {
            return false;
        }
        PracticeSystemTestItem item = (PracticeSystemTestItem) obj;
        return coderID == item.coderID && submitTime == item.submitTime;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + coderID;
        result = 37 * result + (int) (submitTime ^ (submitTime >>> 32));
        return result;
    }

}
