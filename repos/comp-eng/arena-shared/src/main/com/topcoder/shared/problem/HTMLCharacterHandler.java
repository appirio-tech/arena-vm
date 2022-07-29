/*
 * HTMLCharacterHandler
 * 
 * Created 06/15/2006
 */
package com.topcoder.shared.problem;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class that allow to decode/encode HTML character entities 
 * and numeric references
 * 
 * @author Diego Belfer (mural)
 * @version $Id: HTMLCharacterHandler.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class HTMLCharacterHandler {
    private static final int MAX_UNICODE_VALUE = 0x10FFFF;
    private final static Map nameToUnicodeValue;
    
    static {
        nameToUnicodeValue = new HashMap(512);
        nameToUnicodeValue.put("AElig", new Character('\u00C6'));
        nameToUnicodeValue.put("Aacute", new Character('\u00C1'));
        nameToUnicodeValue.put("Acirc", new Character('\u00C2'));
        nameToUnicodeValue.put("Agrave", new Character('\u00C0'));
        nameToUnicodeValue.put("Alpha", new Character('\u0391'));
        nameToUnicodeValue.put("Aring", new Character('\u00C5'));
        nameToUnicodeValue.put("Atilde", new Character('\u00C3'));
        nameToUnicodeValue.put("Auml", new Character('\u00C4'));
        nameToUnicodeValue.put("Beta", new Character('\u0392'));
        nameToUnicodeValue.put("Ccedil", new Character('\u00C7'));
        nameToUnicodeValue.put("Chi", new Character('\u03A7'));
        nameToUnicodeValue.put("Dagger", new Character('\u2021'));
        nameToUnicodeValue.put("Delta", new Character('\u0394'));
        nameToUnicodeValue.put("ETH", new Character('\u00D0'));
        nameToUnicodeValue.put("Eacute", new Character('\u00C9'));
        nameToUnicodeValue.put("Ecirc", new Character('\u00CA'));
        nameToUnicodeValue.put("Egrave", new Character('\u00C8'));
        nameToUnicodeValue.put("Epsilon", new Character('\u0395'));
        nameToUnicodeValue.put("Eta", new Character('\u0397'));
        nameToUnicodeValue.put("Euml", new Character('\u00CB'));
        nameToUnicodeValue.put("Gamma", new Character('\u0393'));
        nameToUnicodeValue.put("Iacute", new Character('\u00CD'));
        nameToUnicodeValue.put("Icirc", new Character('\u00CE'));
        nameToUnicodeValue.put("Igrave", new Character('\u00CC'));
        nameToUnicodeValue.put("Iota", new Character('\u0399'));
        nameToUnicodeValue.put("Iuml", new Character('\u00CF'));
        nameToUnicodeValue.put("Kappa", new Character('\u039A'));
        nameToUnicodeValue.put("Lambda", new Character('\u039B'));
        nameToUnicodeValue.put("Mu", new Character('\u039C'));
        nameToUnicodeValue.put("Ntilde", new Character('\u00D1'));
        nameToUnicodeValue.put("Nu", new Character('\u039D'));
        nameToUnicodeValue.put("OElig", new Character('\u0152'));
        nameToUnicodeValue.put("Oacute", new Character('\u00D3'));
        nameToUnicodeValue.put("Ocirc", new Character('\u00D4'));
        nameToUnicodeValue.put("Ograve", new Character('\u00D2'));
        nameToUnicodeValue.put("Omega", new Character('\u03A9'));
        nameToUnicodeValue.put("Omicron", new Character('\u039F'));
        nameToUnicodeValue.put("Oslash", new Character('\u00D8'));
        nameToUnicodeValue.put("Otilde", new Character('\u00D5'));
        nameToUnicodeValue.put("Ouml", new Character('\u00D6'));
        nameToUnicodeValue.put("Phi", new Character('\u03A6'));
        nameToUnicodeValue.put("Pi", new Character('\u03A0'));
        nameToUnicodeValue.put("Prime", new Character('\u2033'));
        nameToUnicodeValue.put("Psi", new Character('\u03A8'));
        nameToUnicodeValue.put("Rho", new Character('\u03A1'));
        nameToUnicodeValue.put("Scaron", new Character('\u0160'));
        nameToUnicodeValue.put("Sigma", new Character('\u03A3'));
        nameToUnicodeValue.put("THORN", new Character('\u00DE'));
        nameToUnicodeValue.put("Tau", new Character('\u03A4'));
        nameToUnicodeValue.put("Theta", new Character('\u0398'));
        nameToUnicodeValue.put("Uacute", new Character('\u00DA'));
        nameToUnicodeValue.put("Ucirc", new Character('\u00DB'));
        nameToUnicodeValue.put("Ugrave", new Character('\u00D9'));
        nameToUnicodeValue.put("Upsilon", new Character('\u03A5'));
        nameToUnicodeValue.put("Uuml", new Character('\u00DC'));
        nameToUnicodeValue.put("Xi", new Character('\u039E'));
        nameToUnicodeValue.put("Yacute", new Character('\u00DD'));
        nameToUnicodeValue.put("Yuml", new Character('\u0178'));
        nameToUnicodeValue.put("Zeta", new Character('\u0396'));
        nameToUnicodeValue.put("aacute", new Character('\u00E1'));
        nameToUnicodeValue.put("acirc", new Character('\u00E2'));
        nameToUnicodeValue.put("acute", new Character('\u00B4'));
        nameToUnicodeValue.put("aelig", new Character('\u00E6'));
        nameToUnicodeValue.put("agrave", new Character('\u00E0'));
        nameToUnicodeValue.put("alefsym", new Character('\u2135'));
        nameToUnicodeValue.put("alpha", new Character('\u03B1'));
        nameToUnicodeValue.put("amp", new Character('\u0026'));
        nameToUnicodeValue.put("and", new Character('\u2227'));
        nameToUnicodeValue.put("ang", new Character('\u2220'));
        nameToUnicodeValue.put("aring", new Character('\u00E5'));
        nameToUnicodeValue.put("asymp", new Character('\u2248'));
        nameToUnicodeValue.put("atilde", new Character('\u00E3'));
        nameToUnicodeValue.put("auml", new Character('\u00E4'));
        nameToUnicodeValue.put("bdquo", new Character('\u201E'));
        nameToUnicodeValue.put("beta", new Character('\u03B2'));
        nameToUnicodeValue.put("brvbar", new Character('\u00A6'));
        nameToUnicodeValue.put("bull", new Character('\u2022'));
        nameToUnicodeValue.put("cap", new Character('\u2229'));
        nameToUnicodeValue.put("ccedil", new Character('\u00E7'));
        nameToUnicodeValue.put("cedil", new Character('\u00B8'));
        nameToUnicodeValue.put("cent", new Character('\u00A2'));
        nameToUnicodeValue.put("chi", new Character('\u03C7'));
        nameToUnicodeValue.put("circ", new Character('\u02C6'));
        nameToUnicodeValue.put("clubs", new Character('\u2663'));
        nameToUnicodeValue.put("cong", new Character('\u2245'));
        nameToUnicodeValue.put("copy", new Character('\u00A9'));
        nameToUnicodeValue.put("crarr", new Character('\u21B5'));
        nameToUnicodeValue.put("cup", new Character('\u222A'));
        nameToUnicodeValue.put("curren", new Character('\u00A4'));
        nameToUnicodeValue.put("dArr", new Character('\u21D3'));
        nameToUnicodeValue.put("dagger", new Character('\u2020'));
        nameToUnicodeValue.put("darr", new Character('\u2193'));
        nameToUnicodeValue.put("deg", new Character('\u00B0'));
        nameToUnicodeValue.put("delta", new Character('\u03B4'));
        nameToUnicodeValue.put("diams", new Character('\u2666'));
        nameToUnicodeValue.put("divide", new Character('\u00F7'));
        nameToUnicodeValue.put("eacute", new Character('\u00E9'));
        nameToUnicodeValue.put("ecirc", new Character('\u00EA'));
        nameToUnicodeValue.put("egrave", new Character('\u00E8'));
        nameToUnicodeValue.put("empty", new Character('\u2205'));
        nameToUnicodeValue.put("emsp", new Character('\u2003'));
        nameToUnicodeValue.put("ensp", new Character('\u2002'));
        nameToUnicodeValue.put("epsilon", new Character('\u03B5'));
        nameToUnicodeValue.put("equiv", new Character('\u2261'));
        nameToUnicodeValue.put("eta", new Character('\u03B7'));
        nameToUnicodeValue.put("eth", new Character('\u00F0'));
        nameToUnicodeValue.put("euml", new Character('\u00EB'));
        nameToUnicodeValue.put("euro", new Character('\u20AC'));
        nameToUnicodeValue.put("exist", new Character('\u2203'));
        nameToUnicodeValue.put("fnof", new Character('\u0192'));
        nameToUnicodeValue.put("forall", new Character('\u2200'));
        nameToUnicodeValue.put("frac12", new Character('\u00BD'));
        nameToUnicodeValue.put("frac14", new Character('\u00BC'));
        nameToUnicodeValue.put("frac34", new Character('\u00BE'));
        nameToUnicodeValue.put("frasl", new Character('\u2044'));
        nameToUnicodeValue.put("gamma", new Character('\u03B3'));
        nameToUnicodeValue.put("ge", new Character('\u2265'));
        nameToUnicodeValue.put("gt", new Character('\u003E'));
        nameToUnicodeValue.put("hArr", new Character('\u21D4'));
        nameToUnicodeValue.put("harr", new Character('\u2194'));
        nameToUnicodeValue.put("hearts", new Character('\u2665'));
        nameToUnicodeValue.put("hellip", new Character('\u2026'));
        nameToUnicodeValue.put("iacute", new Character('\u00ED'));
        nameToUnicodeValue.put("icirc", new Character('\u00EE'));
        nameToUnicodeValue.put("iexcl", new Character('\u00A1'));
        nameToUnicodeValue.put("igrave", new Character('\u00EC'));
        nameToUnicodeValue.put("image", new Character('\u2111'));
        nameToUnicodeValue.put("infin", new Character('\u221E'));
        nameToUnicodeValue.put("int", new Character('\u222B'));
        nameToUnicodeValue.put("iota", new Character('\u03B9'));
        nameToUnicodeValue.put("iquest", new Character('\u00BF'));
        nameToUnicodeValue.put("isin", new Character('\u2208'));
        nameToUnicodeValue.put("iuml", new Character('\u00EF'));
        nameToUnicodeValue.put("kappa", new Character('\u03BA'));
        nameToUnicodeValue.put("lArr", new Character('\u21D0'));
        nameToUnicodeValue.put("lambda", new Character('\u03BB'));
        nameToUnicodeValue.put("lang", new Character('\u2329'));
        nameToUnicodeValue.put("laquo", new Character('\u00AB'));
        nameToUnicodeValue.put("larr", new Character('\u2190'));
        nameToUnicodeValue.put("lceil", new Character('\u2308'));
        nameToUnicodeValue.put("ldquo", new Character('\u201C'));
        nameToUnicodeValue.put("le", new Character('\u2264'));
        nameToUnicodeValue.put("lfloor", new Character('\u230A'));
        nameToUnicodeValue.put("lowast", new Character('\u2217'));
        nameToUnicodeValue.put("loz", new Character('\u25CA'));
        nameToUnicodeValue.put("lrm", new Character('\u200E'));
        nameToUnicodeValue.put("lsaquo", new Character('\u2039'));
        nameToUnicodeValue.put("lsquo", new Character('\u2018'));
        nameToUnicodeValue.put("lt", new Character('\u003C'));
        nameToUnicodeValue.put("macr", new Character('\u00AF'));
        nameToUnicodeValue.put("mdash", new Character('\u2014'));
        nameToUnicodeValue.put("micro", new Character('\u00B5'));
        nameToUnicodeValue.put("middot", new Character('\u00B7'));
        nameToUnicodeValue.put("minus", new Character('\u2212'));
        nameToUnicodeValue.put("mu", new Character('\u03BC'));
        nameToUnicodeValue.put("nabla", new Character('\u2207'));
        nameToUnicodeValue.put("nbsp", new Character('\u00A0'));
        nameToUnicodeValue.put("ndash", new Character('\u2013'));
        nameToUnicodeValue.put("ne", new Character('\u2260'));
        nameToUnicodeValue.put("ni", new Character('\u220B'));
        nameToUnicodeValue.put("not", new Character('\u00AC'));
        nameToUnicodeValue.put("notin", new Character('\u2209'));
        nameToUnicodeValue.put("nsub", new Character('\u2284'));
        nameToUnicodeValue.put("ntilde", new Character('\u00F1'));
        nameToUnicodeValue.put("nu", new Character('\u03BD'));
        nameToUnicodeValue.put("oacute", new Character('\u00F3'));
        nameToUnicodeValue.put("ocirc", new Character('\u00F4'));
        nameToUnicodeValue.put("oelig", new Character('\u0153'));
        nameToUnicodeValue.put("ograve", new Character('\u00F2'));
        nameToUnicodeValue.put("oline", new Character('\u203E'));
        nameToUnicodeValue.put("omega", new Character('\u03C9'));
        nameToUnicodeValue.put("omicron", new Character('\u03BF'));
        nameToUnicodeValue.put("oplus", new Character('\u2295'));
        nameToUnicodeValue.put("or", new Character('\u2228'));
        nameToUnicodeValue.put("ordf", new Character('\u00AA'));
        nameToUnicodeValue.put("ordm", new Character('\u00BA'));
        nameToUnicodeValue.put("oslash", new Character('\u00F8'));
        nameToUnicodeValue.put("otilde", new Character('\u00F5'));
        nameToUnicodeValue.put("otimes", new Character('\u2297'));
        nameToUnicodeValue.put("ouml", new Character('\u00F6'));
        nameToUnicodeValue.put("para", new Character('\u00B6'));
        nameToUnicodeValue.put("part", new Character('\u2202'));
        nameToUnicodeValue.put("permil", new Character('\u2030'));
        nameToUnicodeValue.put("perp", new Character('\u22A5'));
        nameToUnicodeValue.put("phi", new Character('\u03C6'));
        nameToUnicodeValue.put("pi", new Character('\u03C0'));
        nameToUnicodeValue.put("piv", new Character('\u03D6'));
        nameToUnicodeValue.put("plusmn", new Character('\u00B1'));
        nameToUnicodeValue.put("pound", new Character('\u00A3'));
        nameToUnicodeValue.put("prime", new Character('\u2032'));
        nameToUnicodeValue.put("prod", new Character('\u220F'));
        nameToUnicodeValue.put("prop", new Character('\u221D'));
        nameToUnicodeValue.put("psi", new Character('\u03C8'));
        nameToUnicodeValue.put("quot", new Character('\u0022'));
        nameToUnicodeValue.put("rArr", new Character('\u21D2'));
        nameToUnicodeValue.put("radic", new Character('\u221A'));
        nameToUnicodeValue.put("rang", new Character('\u232A'));
        nameToUnicodeValue.put("raquo", new Character('\u00BB'));
        nameToUnicodeValue.put("rarr", new Character('\u2192'));
        nameToUnicodeValue.put("rceil", new Character('\u2309'));
        nameToUnicodeValue.put("rdquo", new Character('\u201D'));
        nameToUnicodeValue.put("real", new Character('\u211C'));
        nameToUnicodeValue.put("reg", new Character('\u00AE'));
        nameToUnicodeValue.put("rfloor", new Character('\u230B'));
        nameToUnicodeValue.put("rho", new Character('\u03C1'));
        nameToUnicodeValue.put("rlm", new Character('\u200F'));
        nameToUnicodeValue.put("rsaquo", new Character('\u203A'));
        nameToUnicodeValue.put("rsquo", new Character('\u2019'));
        nameToUnicodeValue.put("sbquo", new Character('\u201A'));
        nameToUnicodeValue.put("scaron", new Character('\u0161'));
        nameToUnicodeValue.put("sdot", new Character('\u22C5'));
        nameToUnicodeValue.put("sect", new Character('\u00A7'));
        nameToUnicodeValue.put("shy", new Character('\u00AD'));
        nameToUnicodeValue.put("sigma", new Character('\u03C3'));
        nameToUnicodeValue.put("sigmaf", new Character('\u03C2'));
        nameToUnicodeValue.put("sim", new Character('\u223C'));
        nameToUnicodeValue.put("spades", new Character('\u2660'));
        nameToUnicodeValue.put("sub", new Character('\u2282'));
        nameToUnicodeValue.put("sube", new Character('\u2286'));
        nameToUnicodeValue.put("sum", new Character('\u2211'));
        nameToUnicodeValue.put("sup", new Character('\u2283'));
        nameToUnicodeValue.put("sup1", new Character('\u00B9'));
        nameToUnicodeValue.put("sup2", new Character('\u00B2'));
        nameToUnicodeValue.put("sup3", new Character('\u00B3'));
        nameToUnicodeValue.put("supe", new Character('\u2287'));
        nameToUnicodeValue.put("szlig", new Character('\u00DF'));
        nameToUnicodeValue.put("tau", new Character('\u03C4'));
        nameToUnicodeValue.put("there4", new Character('\u2234'));
        nameToUnicodeValue.put("theta", new Character('\u03B8'));
        nameToUnicodeValue.put("thetasym", new Character('\u03D1'));
        nameToUnicodeValue.put("thinsp", new Character('\u2009'));
        nameToUnicodeValue.put("thorn", new Character('\u00FE'));
        nameToUnicodeValue.put("tilde", new Character('\u02DC'));
        nameToUnicodeValue.put("times", new Character('\u00D7'));
        nameToUnicodeValue.put("trade", new Character('\u2122'));
        nameToUnicodeValue.put("uArr", new Character('\u21D1'));
        nameToUnicodeValue.put("uacute", new Character('\u00FA'));
        nameToUnicodeValue.put("uarr", new Character('\u2191'));
        nameToUnicodeValue.put("ucirc", new Character('\u00FB'));
        nameToUnicodeValue.put("ugrave", new Character('\u00F9'));
        nameToUnicodeValue.put("uml", new Character('\u00A8'));
        nameToUnicodeValue.put("upsih", new Character('\u03D2'));
        nameToUnicodeValue.put("upsilon", new Character('\u03C5'));
        nameToUnicodeValue.put("uuml", new Character('\u00FC'));
        nameToUnicodeValue.put("weierp", new Character('\u2118'));
        nameToUnicodeValue.put("xi", new Character('\u03BE'));
        nameToUnicodeValue.put("yacute", new Character('\u00FD'));
        nameToUnicodeValue.put("yen", new Character('\u00A5'));
        nameToUnicodeValue.put("yuml", new Character('\u00FF'));
        nameToUnicodeValue.put("zeta", new Character('\u03B6'));
        nameToUnicodeValue.put("zwj", new Character('\u200D'));
        nameToUnicodeValue.put("zwnj", new Character('\u200C'));
    }

    /**
     * Decode the given text, replacing all entity references and numeric character references
     * by its unicode characters
     * 
     * @param text the Text to decode
     * 
     * @return the resulting text
     */
    public static String decode(String text) {
        int pos = text.indexOf('&');
        if (pos == -1) return text;
        StringBuffer result = new StringBuffer(text.length());
        int lstPos = 0;
        while (pos > -1) {
            Character encodedChar=null;
            result.append(text.substring(lstPos, pos));
            int endPos = text.indexOf(';', pos);
            if (endPos > pos) {
                if (text.charAt(pos+1) == '#') {
                    if (text.charAt(pos+2) == 'x') {
                        encodedChar = resolveValueCharacter(text.substring(pos+3, endPos), 16);
                    } else {
                        encodedChar = resolveValueCharacter(text.substring(pos+2, endPos), 10);
                    }
                } else {
                    encodedChar = resolveEntityValueCharacter(text.substring(pos+1, endPos));
                }
            } 
            if (encodedChar == null) {
                result.append("&");
                pos++;
            } else {
                result.append(encodedChar);
                pos = endPos + 1;
            }
            lstPos = pos; 
            pos = text.indexOf('&', lstPos);
        }
        result.append(text.substring(lstPos));
        return result.toString();
    }
    
    
    /**
     * Returns the Character for the given name example "gt" "lt"
     * 
     * @param name Name of the entity
     * 
     * @return The resulting character, null if the name could not be resolved
     */
    public static Character resolveEntityValueCharacter(String name) {
        return (Character) nameToUnicodeValue.get(name);
    }

    /**
     * Returns the character correspoding to the number given <code>strValue</code>
     * <code>radix</code> represents the radix of the strValue 
     */
    private static Character resolveValueCharacter(String strValue, int radix) {
        try {
            int value = Integer.parseInt(strValue, radix);
            if (isValidUnicode(value)) return getUnicodeCharacter(value);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Returns true if the value is a valid unicode value
     * 
     * @param value Value to check
     * 
     * @return the result of the check
     */
    public static boolean isValidUnicode(int value) {
        return value >= 0 && value <= MAX_UNICODE_VALUE;
    }


    private static Character getUnicodeCharacter(int charValue) {
        return new Character((char) charValue);
    }
    
    /**
     * Encodes certain characters in the text into HTML entities. '&amp;', '&lt;', '&gt;' and '&quot;' are encoded into
     * HTML entities.
     * 
     * @param text the text to be encoded.
     * @return the HTML encoded text.
     */
    public static String encodeSimple(String text) {
        StringBuffer buf = new StringBuffer(text.length());

        for (int i = 0; i < text.length(); i++) {
            switch (text.charAt(i)) {
            case '&':
                buf.append("&amp;");
                break;
            case '<':
                buf.append("&lt;");
                break;
            case '>':
                buf.append("&gt;");
                break;
            case '"':
                buf.append("&quot;");
                break;
            default:
                buf.append(text.charAt(i));
            }
        }
        return buf.toString();
    }
}
