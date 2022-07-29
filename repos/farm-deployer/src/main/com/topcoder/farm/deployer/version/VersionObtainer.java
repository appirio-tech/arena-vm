/*
 * VersionObtainer
 * 
 * Created 09/08/2006
 */
package com.topcoder.farm.deployer.version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class VersionObtainer {
    private static final Pattern versionSpitter = Pattern.compile("(.+?)(\\-\\{.*\\}){0,1}(\\-[0-9]+.*)(\\.jar)");
    
    public FileVersion fromString(String filename) {
        Matcher matcher = versionSpitter.matcher(filename);
        if (matcher.matches()) {
            String version = matcher.group(3);
            if (version.length()!=0) {
                version = version.substring(1);
            }
            String type = matcher.group(2);
            if (type == null) {
                type = "";
            }
            return new FileVersion(filename, version, matcher.group(1)+type+matcher.group(4));
        } else {
            return new FileVersion(filename, "", filename);
        }
    }
    
    public static class FileVersion {
        private String originalFileName;
        private String version;
        private String unversionedFileName;
   
        public FileVersion(String originalFileName, String version, String unversionedFileName) {
            this.originalFileName = originalFileName;
            this.version = version;
            this.unversionedFileName = unversionedFileName;
        } 
        
        public String getOriginalFileName() {
            return originalFileName;
        }

        public String getUnversionedFileName() {
            return unversionedFileName;
        }

        public String getVersion() {
            return version;
        }
    }

    public FileVersion fromFileAndVersion(String fileName, String version) {
        String versionStr = "";
        if (version != null && version.length() > 0) {
            versionStr =  "-" + version ;
        }
        String realName = fileName.substring(0, fileName.length()-4) + versionStr + ".jar";
        return new FileVersion(realName, version, fileName);
    }
}
