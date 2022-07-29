/*
 * Pair
 * 
 * Created 08/02/2006
 */
package com.topcoder.farm.shared.util;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Pair<K,V> {
    private K fst;
    private V snd;
    
    public Pair() {
    }
    
    public Pair(K fst, V snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public K getFst() {
        return fst;
    }
    
    public void setFst(K fst) {
        this.fst = fst;
    }
    
    public V getSnd() {
        return snd;
    }
    
    public void setSnd(V snd) {
        this.snd = snd;
    }
}
