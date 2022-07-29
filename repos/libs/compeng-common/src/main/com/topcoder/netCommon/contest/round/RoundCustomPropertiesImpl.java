/*
 * RoundCustomPropertiesImpl Created 09/12/2007
 */
package com.topcoder.netCommon.contest.round;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.netCommon.contest.ResultDisplayType;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines an implementation of <code>RoundCustomProperties</code>. It can hold all custom properties.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: RoundCustomPropertiesImpl.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class RoundCustomPropertiesImpl implements RoundCustomProperties, Serializable, CustomSerializable {
    private Long perUserCodingTime;

    private Boolean showScoresOfOtherCoders;

    private ResultDisplayType[] allowedScoreTypesToShow;

    private Language[] allowedLanguages;

    private Long codingLengthOverride;

    /** Represents the set of allowed programming languages. It is initialized lazily. */
    private transient Set allowedLanguagesSet;

    /**
     * Creates a new instance of <code>RoundCustomPropertiesImpl</code>. It is required by custom serialization.
     */
    public RoundCustomPropertiesImpl() {
    }

    /**
     * Creates a new instance of <code>RoundCustomPropertiesImpl</code>. All custom properties are provided.
     * 
     * @param perUserCodingTime the coding time for different users.
     * @param showScoresOfOtherCoders a flag indicating if other coders' scores are allowed to be viewed.
     * @param allowedScoreTypesToShow all allowed methods to show scores/status in the summary window on the client.
     * @param allowedLanguages all allowed programming languages.
     * @param codingLengthOverride the coding time which overrides the default coding time.
     */
    public RoundCustomPropertiesImpl(Long perUserCodingTime, boolean showScoresOfOtherCoders,
        ResultDisplayType[] allowedScoreTypesToShow, Language[] allowedLanguages, Long codingLengthOverride) {
        this.perUserCodingTime = perUserCodingTime;
        this.showScoresOfOtherCoders = Boolean.valueOf(showScoresOfOtherCoders);
        this.allowedScoreTypesToShow = allowedScoreTypesToShow;
        this.allowedLanguages = allowedLanguages;
        this.codingLengthOverride = codingLengthOverride;
    }

    public Long getPerUserCodingTime() {
        return perUserCodingTime;
    }

    /**
     * Sets the coding time for each user if there is different start time for coding phase.
     * 
     * @param perUserCodingTime the coding time for each different user.
     */
    public void setPerUserCodingTime(Long perUserCodingTime) {
        this.perUserCodingTime = perUserCodingTime;
    }

    public ResultDisplayType[] getAllowedScoreTypesToShow() {
        return allowedScoreTypesToShow;
    }

    /**
     * Sets all allowed methods to show scores/status in the summary window on the client. There is no copy.
     * 
     * @param allowedScoreTypesToShow allowed methods to show scores/status.
     */
    public void setAllowedScoreTypesToShow(ResultDisplayType[] allowedScoreTypesToShow) {
        this.allowedScoreTypesToShow = allowedScoreTypesToShow;
    }

    public Boolean getShowScoresOfOtherCoders() {
        return showScoresOfOtherCoders;
    }

    /**
     * Sets a flag indicating if the scores of other coders can be displayed.
     * 
     * @param showScoresOfOtherCoders <code>true</code> if showing other coders' scores is allowed; <code>false</code>
     *            otherwise.
     */
    public void setShowScoresOfOtherCoders(Boolean showScoresOfOtherCoders) {
        this.showScoresOfOtherCoders = showScoresOfOtherCoders;
    }

    public Language[] getAllowedLanguages() {
        return allowedLanguages;
    }

    /**
     * Sets all allowed programming languages. There is no copy.
     * 
     * @param allowedLanguages allowed programming languages.
     */
    public void setAllowedLanguages(Language[] allowedLanguages) {
        this.allowedLanguages = allowedLanguages;
        this.allowedLanguagesSet = null;
    }

    public boolean allowsLanguage(Language lang) {
        if (allowedLanguagesSet == null) {
            if (getAllowedLanguages() == null) {
                return false;
            }
            allowedLanguagesSet = new HashSet(Arrays.asList(getAllowedLanguages()));
        }
        return allowedLanguagesSet.contains(lang);
    }

    public Long getCodingLengthOverride() {
        return codingLengthOverride;
    }

    /**
     * Sets the coding time. This coding time overrides the default coding time. It is in minutes.
     * 
     * @param codingLengthOverride the coding time.
     */
    public void setCodingLengthOverride(Long codingLengthOverride) {
        this.codingLengthOverride = codingLengthOverride;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        this.perUserCodingTime = (Long) reader.readObject();
        this.showScoresOfOtherCoders = (Boolean) reader.readObject();
        this.allowedScoreTypesToShow = (ResultDisplayType[]) reader.readObjectArray(ResultDisplayType.class);
        this.allowedLanguages = (Language[]) reader.readObjectArray(Language.class);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeObject(this.perUserCodingTime);
        writer.writeObject(this.showScoresOfOtherCoders);
        writer.writeObjectArray(this.allowedScoreTypesToShow);
        writer.writeObjectArray(this.allowedLanguages);
    }
}
