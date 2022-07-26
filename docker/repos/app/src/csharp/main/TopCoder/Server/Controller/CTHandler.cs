namespace TopCoder.Server.Controller {

    using System;
    using System.IO;
    using System.Net.Sockets;
    using System.Threading;

    using TopCoder.Server.Util;

    sealed class CTHandler {

        readonly Thread readThread;
        readonly CTController controller;
        readonly string hostname;
        readonly int port;

        ClientSocket socket;

        internal CTHandler(string address, CTController controller) {
            this.controller=controller;
            string[] str=address.Split(new char[]{':'});
            if (str.Length!=2) {
                throw new ArgumentException("incorrect address="+address+", not <host:port>");
            }
            hostname=str[0];
            port=int.Parse(str[1]);
            readThread=new Thread(new ThreadStart(ReadRun));
            readThread.Start();
        }

        internal void Stop() {
            readThread.Abort();
            if (socket != null) {
                socket.Close();
            }
        }

        internal void Send(object response) {
            socket.WriteObject(response);
        }

        void ReadRun() {
            for (;;) {
                try {
                    try {
                        socket=new ClientSocket(hostname,port);
                        Log.WriteLine("connected");
                        for (;;) {
                            controller.Receive(socket.ReadObject());
                        }
                    } catch (SocketException) {
                    } catch (EndOfStreamException) {
                        Log.WriteLine("lost connection");
                    }
                    bool exit = false;
                    try {
                        Thread.Sleep(1000);
                    } catch (ThreadAbortException) {
                        exit = true;
                    }
                    if (socket!=null) {
                        socket.Close();
                    }
                    if (exit) {
                        break;
                    }
                } catch (Exception e) {
                    Log.WriteLine("in CTHandler: "+e);
                }
            }
        }

    }

}
