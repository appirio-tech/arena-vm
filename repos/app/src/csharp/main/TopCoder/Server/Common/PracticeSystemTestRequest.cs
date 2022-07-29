namespace TopCoder.Server.Common {

    sealed class PracticeSystemTestRequest {

        int requestID;
        SystemTestRequest[] systemTestRequests;

        public void CustomReadObject(ICSReader reader) {
            requestID=reader.ReadInt();
            int size=reader.ReadByte();
            systemTestRequests=new SystemTestRequest[size];
            for (int i=0; i<size; i++) {
                SystemTestRequest request=new SystemTestRequest();
                request.CustomReadObject(reader);
                systemTestRequests[i]=request;
            }
        }

        internal int RequestID {
            get {
                return requestID;
            }
        }

        internal SystemTestRequest[] SystemTestRequests {
            get {
                return systemTestRequests;
            }
        }

    }

}
