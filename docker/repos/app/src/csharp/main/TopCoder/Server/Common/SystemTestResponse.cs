namespace TopCoder.Server.Common {

    using System;

    sealed class SystemTestResponse: CustomWriteSerializable {

        readonly int requestID;
        readonly TestResult[] testResults;

        internal SystemTestResponse(int requestID, TestResponse[] testResponses) {
            this.requestID=requestID;
            testResults=new TestResult[testResponses.Length];
            for (int i=0; i<testResults.Length; i++) {
                testResults[i]=testResponses[i].TestResult;
            }
        }

        public void CustomWriteObject(ICSWriter writer) {
            writer.WriteInt(requestID);
            int size=testResults.Length;
            if (size>short.MaxValue) {
                throw new ApplicationException("TestResult array big size: "+size);
            }
            writer.WriteShort((short) size);
            foreach (TestResult result in testResults) {
                result.CustomWriteObject(writer);
            }
        }

        public override string ToString() {
            return "SystemTestResponse RequestID="+requestID;
        }

    }

}
