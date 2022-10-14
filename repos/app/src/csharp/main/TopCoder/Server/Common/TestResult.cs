namespace TopCoder.Server.Common {

    using TopCoder.Server.Util;

    sealed class TestResult: CustomWriteSerializable {

        readonly bool hasResult;
        readonly object result;
        readonly int elapsedTime;
        readonly string stdout;
        readonly string stderr;
        int MAXSTDLIMIT = 1000;
        int MAXRESULT = 8000;
        readonly bool isTimeout;

        internal TestResult(bool hasResult, object result, int elapsedTime, string stdout, 
                            string stderr, bool isTimeout) {
            this.hasResult=hasResult;
            if (result is string) {
                result=Truncate((string) result, MAXRESULT);
            }
            this.result=result;
            this.elapsedTime=elapsedTime;
            this.stdout=stdout;
            this.stderr=stderr;
            this.isTimeout = isTimeout;
        }

        public void CustomWriteObject(ICSWriter writer) {
            writer.WriteBoolean(hasResult);
            writer.WriteObject(result);
            writer.WriteInt(elapsedTime);
            writer.WriteString(Truncate(stdout, MAXSTDLIMIT));
            writer.WriteString(Truncate(stderr, MAXSTDLIMIT));
            writer.WriteBoolean(isTimeout);
        }

        string Truncate(string s, int i) {
            int limit=i;
            if (s.Length>limit) {
                s=s.Substring(0,limit)+".. The rest was truncated";
            }
            return s;
        }

        internal int ElapsedTime {
            get {
                return elapsedTime;
            }
        }

        internal bool HasResult {
            get {
                return hasResult;
            }
        }

        internal object Result {
            get {
                return result;
            }
        }

        public override string ToString() {
            return "TestResult "+hasResult+" "+elapsedTime+" Result={"+
                StringUtils.ToString(result)+"} Stderr={"+stderr+"}";
        }

    }

}
