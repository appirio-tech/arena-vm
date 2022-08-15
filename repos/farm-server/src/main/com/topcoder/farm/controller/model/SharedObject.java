/*
 * SharedObject
 * 
 * Created 01/11/2006
 */
package com.topcoder.farm.controller.model;

import java.util.Date;

/**
 * SharedObject interface
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface SharedObject {

    public Object getObject();

    public void setObject(Object object);

    public String getObjectKey();

    public void setObjectKey(String objectKey);

    public String getClientOwner();

    public void setClientOwner(String clientOwner);

    public Long getId();

    public void setId(Long id);

    public Date getStorageDate();

    public void setStorageDate(Date creationDate);

}