namespace TopCoder.Server.Controller {

    using System;
    using System.IO;
    using System.Net.Sockets;

    sealed class ClientSocket {

        readonly TcpClient tcpClient;
        readonly ObjectReader reader;
        readonly ObjectWriter writer;

        internal ClientSocket(string hostname, int port) {
            tcpClient=new TcpClient(hostname,port);
            Stream stream=tcpClient.GetStream();
            reader=new ObjectReader(stream);
            writer=new ObjectWriter(stream);
        }

        internal object ReadObject() {
            return reader.ReadObject();
        }

        internal void WriteObject(object obj) {
            writer.WriteObject(obj);
        }

        internal void Close() {
            reader.Close();
            tcpClient.Close();
        }

    }

}
