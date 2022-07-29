namespace TopCoder.Server.Common {

    using System;

    sealed class PracticeSystemTestResponse: CustomWriteSerializable {

        readonly int requestID;
        readonly SystemTestResponse[] systemTestResponses;

        internal PracticeSystemTestResponse(int requestID, SystemTestResponse[] responses) {
            this.requestID=requestID;
            systemTestResponses=responses;
        }

        public void CustomWriteObject(ICSWriter writer) {
            writer.WriteInt(requestID);
            int size=systemTestResponses.Length;
            if (size>byte.MaxValue) {
                throw new ApplicationException("SystemTestResponse array big size: "+size);
            }
            writer.WriteByte((byte) size);
            foreach (SystemTestResponse response in systemTestResponses) {
                response.CustomWriteObject(writer);
            }
        }

    }

}
