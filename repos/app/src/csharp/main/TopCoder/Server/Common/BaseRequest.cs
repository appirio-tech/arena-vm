namespace TopCoder.Server.Common {

    abstract class BaseRequest: CustomReadSerializable {

        int languageID;
        int requestID;
        int userID;
        int contestID;
        int roundID;
        int problemID;

        public virtual void CustomReadObject(ICSReader reader) {
            languageID=reader.ReadInt();
            requestID=reader.ReadInt();
            userID=reader.ReadInt();
            contestID=reader.ReadInt();
            roundID=reader.ReadInt();
            problemID=reader.ReadInt();
        }

        internal int LanguageID {
            get {
                return languageID;
            }
        }

        internal int RequestID {
            get {
                return requestID;
            }
        }

        internal int UserID {
            get {
                return userID;
            }
        }

        internal int ContestID {
            get {
                return contestID;
            }
        }

        internal int RoundID {
            get {
                return roundID;
            }
        }

        internal int ProblemID {
            get {
                return problemID;
            }
        }

        public override string ToString() {
            return "requestID="+requestID+" userID="+userID+" roundID="+roundID+
                " problemID="+problemID;
        }

    }

}
