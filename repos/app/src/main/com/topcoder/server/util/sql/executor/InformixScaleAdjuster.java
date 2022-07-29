package com.topcoder.server.util.sql.executor;

final class InformixScaleAdjuster implements ScaleAdjuster {

    public int getScale(int scale) {
        int result;
        if (scale == 255) {
            result = 0;
        } else {
            result = scale;
        }
        return result;
    }

}
