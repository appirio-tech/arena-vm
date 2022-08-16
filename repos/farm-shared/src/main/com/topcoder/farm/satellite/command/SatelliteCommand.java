/*
 * SatelliteCommand
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.satellite.command;

import java.io.Serializable;

import com.topcoder.farm.satellite.SatelliteNodeCallback;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface SatelliteCommand extends Serializable, CustomSerializable {

    public Object execute(SatelliteNodeCallback node) throws Exception ;

}
