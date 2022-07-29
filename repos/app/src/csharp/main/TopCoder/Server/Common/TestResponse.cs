namespace TopCoder.Server.Common {

    sealed class TestResponse: CustomWriteSerializable {

        readonly int requestID;
        readonly TestResult testResult;

        internal TestResponse(int requestID, bool hasResult, object result, int elapsedTime, 
                              string stdout, string stderr, bool isTimeout) {
            this.requestID=requestID;
            testResult=new TestResult(hasResult,result,elapsedTime,stdout,stderr, isTimeout);
        }

        public void CustomWriteObject(ICSWriter writer) {
            writer.WriteInt(requestID);
            testResult.CustomWriteObject(writer);
        }

        internal int ElapsedTime {
            get {
                return testResult.ElapsedTime;
            }
        }

        internal TestResult TestResult {
            get {
                return testResult;
            }
        }

        public override string ToString() {
            return "TestResponse requestID="+requestID+" "+testResult;
        }

    }

}
