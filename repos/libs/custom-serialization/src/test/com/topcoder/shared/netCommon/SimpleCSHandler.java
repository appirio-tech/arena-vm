package com.topcoder.shared.netCommon;

import com.topcoder.shared.netCommon.CSHandler;

public final class SimpleCSHandler extends CSHandler {

    protected boolean writeObjectOverride(Object object) {
        return false;
    }

}
