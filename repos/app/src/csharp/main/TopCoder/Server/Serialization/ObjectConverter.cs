namespace TopCoder.Server.Serialization {

    using System.IO;
    using System.Runtime.Serialization;
    using System.Runtime.Serialization.Formatters.Soap;

    sealed class ObjectConverter {

        ObjectConverter() {
        }

        static IFormatter getFormatter() {
            return new SoapFormatter();
        }

        internal static byte[] Serialize(object obj) {
            MemoryStream memoryStream = new MemoryStream();
            object wrappedObject = WrapUtil.Wrap(obj);
            getFormatter().Serialize(memoryStream, wrappedObject);
            memoryStream.Close();
            byte[] byteArray = memoryStream.GetBuffer();
            return byteArray;
        }

        internal static object Deserialize(byte[] byteArray) {
            object obj;
            if (byteArray.Length > 0) {
                MemoryStream memoryStream = new MemoryStream(byteArray);
                object wrappedObject = getFormatter().Deserialize(memoryStream);
                memoryStream.Close();
                obj = WrapUtil.Unwrap(wrappedObject);
            } else {
                obj = null;
            }
            return obj;
        }

    }

}
