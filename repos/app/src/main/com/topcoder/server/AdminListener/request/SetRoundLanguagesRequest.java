/*
 * SetRoundLanguagesRequest
 * 
 * Created 05/15/2007
 */
package com.topcoder.server.AdminListener.request;

import java.io.IOException;

import com.topcoder.server.contest.RoundLanguageData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * @autor Diego Belfer (Mural)
 * @version $Id: SetRoundLanguagesRequest.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class SetRoundLanguagesRequest extends ContestManagementRequest {

    private int id;
    private RoundLanguageData languages;
    
    public SetRoundLanguagesRequest() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeObject(languages);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        languages = (RoundLanguageData)reader.readObject();
    }

    public SetRoundLanguagesRequest(int id, RoundLanguageData languages) {
        this.id = id;
        this.languages = languages;
    }

    public int getId() {
        return id;
    }

    public RoundLanguageData getLanguages() {
        return languages;
    }
}
