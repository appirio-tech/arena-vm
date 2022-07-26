/*
 * NullRoundCustomProperties
 * 
 * Created 09/12/2007
 */
package com.topcoder.netCommon.contest.round;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.ResolvedCustomSerializable;


/**
 * Defines an empty custom round properties.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: NullRoundCustomProperties.java 72092 2008-08-05 06:27:07Z qliu $
 */
public class NullRoundCustomProperties implements RoundCustomProperties, ResolvedCustomSerializable {
    /** Represents the singleton instance of the class. */
    public static final NullRoundCustomProperties INSTANCE = new NullRoundCustomProperties();

    /**
     * Creates a new instance of <code>NullRoundCustomProperties</code>.
     */
    public NullRoundCustomProperties() {
    }
    
    public ResultDisplayType[] getAllowedScoreTypesToShow() {
        return null;
    }

    public Long getPerUserCodingTime() {
        return null;
    }

    public Boolean getShowScoresOfOtherCoders() {
        return null;
    }
    
    public Language[] getAllowedLanguages() {
        return null;
    }

    public boolean allowsLanguage(Language lang) {
        return false;
    }
    
    public Long getCodingLengthOverride() {
        return null;
    }
    
    public Object readResolve() {
        return INSTANCE;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
    }

    public void customWriteObject(CSWriter writer) throws IOException {
    }
}
