namespace TopCoder.Server.Tester {

    using System;
    using System.Reflection;
    using System.Threading;

    sealed class TestRunner {
        readonly MethodInfo method;
        readonly object[] parameters;

        volatile bool ended;
        volatile bool hasResult;
        volatile object result;
        volatile string exceptionTrace="";
        Type type;

        internal TestRunner(Type type, MethodInfo method, object[] parameters) {
            this.type = type;
            this.method=method;
            this.parameters=parameters;
        }

        internal void Run() {
            ended = false;
            hasResult = false;
            try {
                object obj = Activator.CreateInstance(type);
                result=method.Invoke(obj,parameters);
                hasResult=true;
            } catch (TargetInvocationException e) {
                exceptionTrace = e.GetBaseException().ToString();
            } catch (ThreadAbortException) {
            } catch (Exception e) {
                Exception innerException=e.InnerException;
                if (innerException==null) {
                    exceptionTrace="innerException is null, "+e.ToString();
                } else if (innerException is OutOfMemoryException) {
                } else if (!(innerException is ThreadAbortException)) {
                    exceptionTrace=innerException.ToString();
                }
            }
            lock (this) {
                ended = true;
                Monitor.PulseAll(this);
            }
        }

        internal bool HasResult {
            get {
                return hasResult;
            }
        }

        internal object Result {
            get {
                return result;
            }
        }

        internal string ExceptionTrace {
            get {
                return exceptionTrace;
            }
        }

        internal bool hasEnded 
        {
            get
            {
                return ended;
            }
        }

    }

}
