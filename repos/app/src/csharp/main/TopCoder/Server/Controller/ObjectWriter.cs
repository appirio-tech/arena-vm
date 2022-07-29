namespace TopCoder.Server.Controller {

    using System.IO;
    using TopCoder.Io.Serialization.BasicType.Impl;

    sealed class ObjectWriter {

        readonly byte[] buffer=new byte[IOConstants.REQUEST_BIG_BUFFER_SIZE];
        readonly CSWriter memoryWriter;
        readonly MemoryStream memoryStream;
        readonly CSWriter writer;
        readonly BufferedStream binaryWriter;

        internal ObjectWriter(Stream stream) {
            memoryStream=new MemoryStream(buffer);
            memoryWriter=new CSWriter(new BasicTypeWriter(memoryStream));
            binaryWriter=new BufferedStream(stream);
            writer=new CSWriter(new BasicTypeWriter(binaryWriter));
        }

        internal void WriteObject(object obj) {
            memoryStream.Seek(0,SeekOrigin.Begin);
            memoryWriter.WriteObject(obj);
            memoryWriter.Flush();
            int size=(int) memoryStream.Position;
            writer.WriteInt(size);
            writer.Flush();
            binaryWriter.Write(buffer,0,size);
            binaryWriter.Flush();
        }

    }

}
