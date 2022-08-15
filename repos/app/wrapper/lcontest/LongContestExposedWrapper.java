/*
	This is the template for Long Contest MPSQAS Code
	Each "define" sourrounded by brackets gets expanded before compiling.
	The following defines are used (spaces used to prevent problems):
	
	< WRAPPER_CLASS> - Constant for the class name (Wrapper)
	< CLASS_NAME> - Name of problem class
	< METHODS> - A special define, the dynamic methods go here.  The block is repeated until < /METHODS>
	< ARGS> - A special define, the dynamic args for a method go here.  The block is repeated until < /ARGS>

	< ARG_TYPE> - Type for current arg
	< RETURN_TYPE> - Return type for current method
	< METHOD_NAME> - Method name of function
	< METHOD_NUMBER> - Number of method, used for IO
	< PARAMS> - Expends to entire params of function (ex: a0, a1,...)
	< ARG_NAME> - Expends to the variable for the current arg
	< ARG_METHOD_NAME> - Gets the IO function to read the arg type
*/

import com.topcoder.services.util.LongTesterIO;
import com.topcoder.services.tester.java.SocketWrapper;
import com.topcoder.services.tester.java.NMaxSecurityManager;
import com.topcoder.shared.common.ServicesConstants;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class <EXPOSED_WRAPPER_CLASS> {
    
        private <EXPOSED_WRAPPER_CLASS>() {
            
        }
        
        public static class Stopwatch {
            public Stopwatch() {
                this.time = 0;
                this.start = 0;
                this.stoppedStart = 0;
                this.stoppedTime = 0;
            }
            
            public void reset(long time) {
                this.start = 0;
                this.time = time;
                this.stoppedTime = 0;
                this.stoppedStart = 0;
            }
            
            private long time;
            private long start;
            private long stoppedStart;
            private long stoppedTime;
            
            public void start() {
                this.start = System.currentTimeMillis();
                if(stoppedStart != 0) {
                    stoppedTime += (System.currentTimeMillis() - stoppedStart);
                    stoppedStart = 0;
                }
            }
            
            public void stop() {
                time = time - (System.currentTimeMillis() - start);
                this.start = 0;
                this.stoppedStart = System.currentTimeMillis();
            }
            
            public long getStoppedTime() {
                return stoppedTime;
            }
            
            public long getTimeRemaining() {
                if(start == 0) {
                    return time;
                }
                if(time < 0) {
                    return 0;
                }
                long t = time - (System.currentTimeMillis() - start);
                return t>0?t:0;
            }
            
            public boolean hasTimeRemaining() {
                return getTimeRemaining() > 0;
            }
        }
    
        public static BufferedInputStream br;
        public static BufferedOutputStream bw;
        public static SocketWrapper sw;
        public static Stopwatch watch;

        public static void initialize(Stopwatch myWatch) throws IOException {
            sw = new SocketWrapper(new Socket(InetAddress.getByName(null), ServicesConstants.MARATHON_PORT_NUMBER+1));
			br = new BufferedInputStream(sw.getInputStream());
			bw = new BufferedOutputStream(sw.getOutputStream());
            watch = myWatch;
        }
        
        public static void shutdown() {
            try {
                br.close();
                bw.close();
                sw.close();
            } catch (Exception ignore) {
                
            }
        }
        
        <EXPOSED_METHODS>
	public synchronized static <RETURN_TYPE> <METHOD_NAME>(<PARAMS>) {
		try {
                        watch.stop();
			LongTesterIO.startMethod(bw,<METHOD_NUMBER>);
			<WRITE_ARGS>
			LongTesterIO.writeArg(bw, <ARG_NAME>);
			</WRITE_ARGS>
                        LongTesterIO.flush(bw);

                	<RETURN_TYPE> val = LongTesterIO.<RETURN_METHOD_NAME>(br);
			
			return val;
		} catch(Throwable e) {
			//e.printStackTrace();
			try{
				br.close();
				bw.close();
			} catch (IOException ioe) { 
				ioe.printStackTrace();
			}
			return <DEFAULT_RETURN>;
                } finally {
                    watch.start();
                }
	}
	</EXPOSED_METHODS>
        
}
