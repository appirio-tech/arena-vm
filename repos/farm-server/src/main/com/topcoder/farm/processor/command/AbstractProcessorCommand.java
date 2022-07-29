/*
 * AbstractProcessorCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.processor.command;

import com.topcoder.farm.processor.api.ProcessorNodeCallback;
import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.farm.satellite.command.AbstractSatelliteCommand;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractProcessorCommand extends AbstractSatelliteCommand {
    public AbstractProcessorCommand() {
    }

    protected Object bareExecute(SatelliteNodeCallback node) throws Exception {
        return bareExecuteProcessor((ProcessorNodeCallback) node);
    }

    protected abstract Object bareExecuteProcessor(ProcessorNodeCallback node) throws Exception ;
}
