/*
 * ComponentStateRWLockManager
 *
 * Created 03/22/2007
 */
package com.topcoder.server.ejb.TestServices;


import com.topcoder.farm.shared.util.concurrent.ReadWriteLock;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLockManager;

/**
 * @author Diego Belfer (mural)
 * @version $Id: ComponentStateRWLockManager.java 59940 2007-04-17 16:20:14Z thefaxman $
 */
public class ComponentStateRWLockManager extends ReadWriteLockManager {
    //FXIME mural doc
    public ReadWriteLock.Lock getLock(int roundId, int componentId, int coderId, boolean writeLock) {
        String key = buildKey(roundId, componentId, coderId);
        if (writeLock) {
            return getWriteLock(key);
        } else {
            return getReadLock(key);
        }
    }

    public String buildKey(int roundId, int componentId, int coderId) {
        return "R"+roundId+".C"+componentId+".U"+coderId;
    }

    public ReadWriteLock getLock(int roundId, int componentId, int coderId) {
        String key = buildKey(roundId, componentId, coderId);
        return getLock(key);
    }

    public void safeUnlock(ReadWriteLock.Lock lock) {
        if (lock != null && lock.isLocked()) {
            lock.unlock();
        }
    }
}
