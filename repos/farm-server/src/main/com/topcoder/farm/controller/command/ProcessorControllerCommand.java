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
public interface ProcessorControllerCommand extends Serializable, ControllerCommand, CustomSerializable {
}
