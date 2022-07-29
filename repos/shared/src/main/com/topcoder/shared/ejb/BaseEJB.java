package com.topcoder.shared.ejb;

import com.topcoder.shared.util.logging.Logger;

import javax.ejb.*;


/**
 * This class is intended to be a base class for all of Medecoms EJBs.  Code
 * common to all EJBs should probably be included here.
 *
 * @author  Jess Evans
 * @version  $Revision$
 */
public abstract class BaseEJB implements SessionBean {
    private static Logger log = Logger.getLogger(BaseEJB.class);


//****************************************************************************
//                                 Data Members
//****************************************************************************

    private SessionContext ctx;



//****************************************************************************
//                                 EJB lifecycle
//****************************************************************************

    /**
     * This method is required by the EJB Specification
     *
     */
    public void ejbActivate() {
    }


    /**
     * This method is required by the EJB Specification
     *
     */
    public void ejbPassivate() {
    }


    /**
     * This method is required by the EJB Specification.
     * Used to get the context ... for dynamic connection pools.
     * @throws CreateException
     */
    public void ejbCreate() throws CreateException {
    }


    /**
     * This method is required by the EJB Specification
     */
    public void ejbRemove() {
    }


    /**
     * Sets the transient SessionContext.
     * Sets the transient Properties.
     * @param ctx
     */
    public void setSessionContext(SessionContext ctx) {
        this.ctx = ctx;
        //props = ctx.getEnvironment();
    }


}
