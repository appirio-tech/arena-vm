namespace TopCoder.Server.Common {

    using TopCoder.Server.Util;

    sealed class RestartServiceRequest : BaseTestRequest {
        int requesttype;

        public override void CustomReadObject(ICSReader reader) {
            requesttype=reader.ReadInt();
            Log.WriteLine("requesttype="+requesttype);
        }

        public int getType {
            get {
                return requesttype;
            }
        }
    }

}