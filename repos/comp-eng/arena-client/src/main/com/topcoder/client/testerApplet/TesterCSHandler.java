package com.topcoder.client.testerApplet;

import com.topcoder.shared.netCommon.CSHandler;

public class TesterCSHandler extends CSHandler {
    protected boolean writeObjectOverride(Object obj) {
        return false;
    }
}
