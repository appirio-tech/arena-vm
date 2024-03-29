package com.topcoder.server.distCache;

import java.rmi.Naming;
import java.rmi.RemoteException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;

public class GenericRMIProxy
        implements InvocationHandler {


    String[] _urls;
    Object[] _targets;

    public GenericRMIProxy(String[] urls) {
        _urls = urls;
        _targets = new Object[_urls.length];
    }


    /**
     *  Reset our list of targets.  Connectios will be established
     *  to and targets we do not have a current handle to.
     */

    private void reset() {
        for (int i = 0; i < _targets.length; i++) {
            if (_targets[i] == null) {
                _targets[i] = connect(_urls[i]);
            }
        }
    }

    /**
     *  try to lookup a remote object by url.
     */
    private Object connect(String url) {
        try {
            return Naming.lookup(url);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     *  invoke a method on the proxied object
     */

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        for (int retry = 0; retry < 2; retry++) {
            // do reset / eval sequence twice because
            // we might need to first discover the disconnect
            // then reset.
            reset();

            for (int i = 0; i < _targets.length; i++) {
                if (_targets[i] != null) {
                    try {
                        return method.invoke(_targets[i], args);
                    } catch (Exception e) {
                        _targets[i] = null;
                    }
                }
            }
        }


        throw new RemoteException("Proxy cannot connect to sources");
    }


}
