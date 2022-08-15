namespace TopCoder.Server.Common {

    using TopCoder.Server.Util;
    using System.Collections;

    sealed class TestRequest: BaseTestRequest {

        object[] args;

        Hashtable dllFiles;

        public override void CustomReadObject(ICSReader reader) {
            base.CustomReadObject(reader);
            args=reader.ReadObjectArray();
            dllFiles = reader.ReadHashtable();
        }

        internal object[] Args {
            get {
                return args;
            }
        }

        internal Hashtable DllFiles {
            get {
                return dllFiles;
            }
        }

        public override string ToString() {
            string name="TestRequest "+base.ToString()+" Args={";
            for (int i=0; i<args.Length; i++) {
                if (i!=0) {
                    name+=",";
                }
                name+=StringUtils.ToString(args[i]);
            }
            name+="}";
            return name;
        }

    }

}
