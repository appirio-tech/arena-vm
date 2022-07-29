namespace TopCoder.Server.Common {

    using System.Collections;

    sealed class SystemTestRequest: BaseTestRequest {

        object[][] tests;
        object[] expected;
        bool failOnFirst;

        Hashtable dllFiles;

        public override void CustomReadObject(ICSReader reader) {
            base.CustomReadObject(reader);
            tests=reader.ReadObjectArrayArray();
            expected=reader.ReadObjectArray();
            failOnFirst=reader.ReadBool();
            dllFiles = reader.ReadHashtable();
        }

        internal object[][] Tests {
            get {
                return tests;
            }
        }

        internal object[] Expected {
            get {
                return expected;
            }
        }

        internal bool FailOnFirst {
            get {
                return failOnFirst;
            }
        }

        internal Hashtable DllFiles {
            get {
                return dllFiles;
            }
        }

    }

}
