/*
 * AbstractSatelliteCommand
 * 
 * Created 07/03/2006
 */
package com.topcoder.farm.controller.command;

import java.io.Serializable;

import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ControllerCommand extends Serializable, CustomSerializable {
    public Object execute(Object controller) throws Exception;
}
