/*
 * TestRequest
 * 
 * Created 12/28/2006
 */
package com.topcoder.services.tester.common;

import java.util.List;
import java.util.Map;

import com.topcoder.server.tester.ComponentFiles;
import com.topcoder.server.tester.Solution;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.problem.SimpleComponent;

/**
 * @author Diego Belfer (mural)
 * @version $Id: TestRequest.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public interface TestRequest extends CustomSerializable {

    public SimpleComponent getComponent();

    public Object[] getArgs();
    
    public boolean mustValidateArgs();

    public Solution getSolution();

    public ComponentFiles getComponentFiles();

    public List getDependencyComponentFiles();

    public Map getCompiledWebServiceClientFiles();

}