namespace TopCoder.Server.Controller {

    using System.IO;
    using TopCoder.Io.Serialization.BasicType.Impl;

    sealed class ObjectReader {

        readonly CSReader reader;

        internal ObjectReader(Stream stream) {
            reader=new CSReader(new BasicTypeReader(new BufferedStream(stream)));
        }

        internal object ReadObject() {
            int size=reader.ReadInt();
            if (size<=0) {
                throw new IOException("size="+size);
            }
            reader.ResetMemoryUsageCounter();
            reader.MemoryUsageLimit = (ulong)size;
            return reader.ReadObject();
        }

        internal void Close() {
            reader.Close();
        }

    }

}
