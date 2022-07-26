package com.topcoder.server.listener.monitor;

import com.topcoder.shared.netCommon.CSHandler;
import com.topcoder.shared.netCommon.CSHandlerFactory;

public final class MonitorCSHandlerFactory implements CSHandlerFactory {

    public CSHandler newInstance() {
        return new MonitorCSHandler();
    }

}
