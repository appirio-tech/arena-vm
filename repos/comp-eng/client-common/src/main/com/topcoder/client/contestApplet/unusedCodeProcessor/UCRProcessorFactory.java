/*
 * UCRProcessorFactory.java
 *
 * Created on May 10, 2005, 2:50 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.topcoder.client.contestApplet.unusedCodeProcessor;
import com.topcoder.shared.language.CPPLanguage;
import com.topcoder.shared.language.CSharpLanguage;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.language.VBLanguage;

/**
 *
 * @author rfairfax
 */
public class UCRProcessorFactory {
    
    public static UCRProcessor getProcessor(int language) throws Exception {
        switch(language) {
            case VBLanguage.ID:
                return new VBProcessor();
            case CSharpLanguage.ID:
                return new CSharpProcessor();
            case JavaLanguage.ID:
                return new JavaProcessor();
            case CPPLanguage.ID:
                return new CPPProcessor();
            default:
                break;
        }
        throw new RuntimeException("No Processor Found");
    }
    
}
