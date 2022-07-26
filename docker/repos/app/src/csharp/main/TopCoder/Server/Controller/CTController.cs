namespace TopCoder.Server.Controller {

    using System;
    using System.Collections;
    using System.Threading;

    using TopCoder.Server.Util;

    sealed class CTController {

        readonly CTHandler handler;
        readonly Queue queue=new Queue();
        readonly ControllerWorker[] worker;
        readonly Thread shutDownThread;

        // just an object to lock on for shutting down
        readonly Queue lockThing=new Queue();

        internal CTController(string address, int numWorkerThreads) {
            Log.WriteLine("address="+address+", numWorkerThreads="+numWorkerThreads);
            try {
                handler=new CTHandler(address,this);
            } catch (ArgumentException e) {
                Log.WriteLine(""+e);
                return;
            }
            shutDownThread = new Thread(new ThreadStart(ShutDown));
            worker=new ControllerWorker[numWorkerThreads];
            for (int i=0; i<numWorkerThreads; i++) {
                worker[i]=new ControllerWorker(this,i);
            }
            shutDownThread.Start();
        }

        internal void Stop() {
            handler.Stop();
            for (int i=0; i<worker.Length; i++) {
                worker[i].Stop();
            }
            Log.WriteLine("stopped all worker threads");
        }

        internal void Send(object response) {
            handler.Send(response);
            Log.WriteLine("sent: "+response);
        }

        internal object Dequeue() {
            lock (queue) {
                while (queue.Count<=0) {
                    Monitor.Wait(queue);
                }
                return queue.Dequeue();
            }
        }

        internal void Receive(object request) {
            lock (queue) {
                queue.Enqueue(request);
                Monitor.Pulse(queue);
            }
            Log.WriteLine("received: "+request);
        }

        internal void CallShutDown() {
            lock (lockThing) {
                Monitor.Pulse(lockThing);
            }
        }

        internal void ShutDown() {
            lock (lockThing) {
                Monitor.Wait(lockThing);
            }
            Log.WriteLine("shutting down...");
            Stop();
        }

    }

}
