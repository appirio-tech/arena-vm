namespace TopCoder.Server.Common {

    using System;
    using System.Collections;

    interface ITester {

        TestResponse ProcessTestRequest(int requestID, Language language, byte[] dllBytes, byte[] pdbBytes,
            string className, string methodName, Type[] argTypes, object[] args, int userID,
            int contestID, int roundID, int problemID, bool isSystemTest, Hashtable dllFiles);

    }

}
