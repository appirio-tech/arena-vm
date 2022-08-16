namespace TopCoder.Server.Common {

    using System;
    using System.IO;

    sealed class DirectoryUtils {

        const string SubmissionsDir="submissions\\";

        readonly static string baseDir=AppDomain.CurrentDomain.BaseDirectory+SubmissionsDir;

        static DirectoryUtils() {
            Directory.CreateDirectory(baseDir);
        }

        DirectoryUtils() {
        }

        internal static string GetDir(Language language, int userID, int contestID, int roundID, int problemID) {
            string langStr;
            switch (language) {
            case Language.CSHARP:
                langStr = "csharp";
                break;
            case Language.VB:
                langStr = "vb";
                break;
            default:
                throw new ApplicationException("unknown language: " + language);
            }
            string dirName = baseDir + langStr + "\\u" +userID+"\\c"+contestID+"\\r"+roundID+"\\p"+problemID+ "\\";
            try {
                Directory.CreateDirectory(dirName);
            } catch (IOException) {
            }
            return dirName;
        }

    }

}
