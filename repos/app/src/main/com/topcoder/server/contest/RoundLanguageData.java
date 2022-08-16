/*
 * RoundLanguageData
 * 
 * Created 05/15/2007
 */
package com.topcoder.server.contest;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: RoundLanguageData.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class RoundLanguageData implements CustomSerializable, Serializable {
    private int roundId = 0;
    private Set languages = new HashSet();

    public RoundLanguageData() {
    }
    
    public RoundLanguageData(int roundId) {
        this.roundId = roundId;
    }    
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(roundId);
        writer.writeObjectArray(languages.toArray());
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        roundId = reader.readInt();
        languages = new HashSet(Arrays.asList(reader.readObjectArray()));
    }
    
    public void setUseDefaultLanguages() {
        languages = new HashSet();
    }
    
    public void setLanguages(Collection languages) {
        if (languages == null || languages.size() == 0) {
            throw new IllegalArgumentException("No language included");
        }
        this.languages = new HashSet(languages);
    }
    
    public boolean isUseDefaultLanguages() {
        return languages.size() == 0;
    }
    
    /**
     * @return a Set<Language> containing languages set for the round.
     * @throws IllegalStateException if <code>isUseDefaultLanguages</code> is true
     */
    public Set getLanguages() {
        if (languages.size() == 0) {
            throw new IllegalStateException("Use default languages");
        }
        return new HashSet(languages);
    }

    public int getRoundId() {
        return roundId;
    }
}

