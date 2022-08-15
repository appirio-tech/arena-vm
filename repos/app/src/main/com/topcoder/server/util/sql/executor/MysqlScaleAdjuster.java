package com.topcoder.server.util.sql.executor;

final class MysqlScaleAdjuster implements ScaleAdjuster {

    public int getScale(int scale) {
        int result = scale - 1;
        if (result < 0) {
            throw new RuntimeException("result = " + result);
        }
        return result;
    }

}
