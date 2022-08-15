/*
        This is the template for Long Contest MPSQAS Code
        Each "define" sourrounded by brackets gets expanded before compiling.
        The following defines are used (spaces used to prevent problems):

        < PACKAGE_NAME> - Name of package, same format as MPSQAS Solutions
        < TESTER_IO_CLASS> - Constant for the class name (LongTest)
        < CALLING_METHODS> - A special define, the dynamic methods go here.  The block is repeated until < /CALLING_METHODS>
        < WRITE_ARGS> - A special define, the dynamic args for a method go here.  The block is repeated until < /WRITE_ARGS>

        < RETURN_TYPE> - Return type for function
        < METHOD_NAME> - Method name of function
        < METHOD_NUMBER> - Number of method, used for IO
        < PARAMS> - Expends to entire params of function (ex: int a0, int b0,...)
        < ARG_NAME> - Expends to the variable for the current arg
        < RETURN_METHOD_NAME> - Gets the IO function to read the return type
        < DEFAULT_RETURN> - Default return for the return type
*/

package <PACKAGE_NAME>;

import com.topcoder.services.util.LongTesterIO;
import java.io.*;
import java.lang.Thread;
import com.topcoder.services.persistentcache.*;
import com.topcoder.services.persistentcache.impl.*;
import com.topcoder.shared.netCommon.CustomSerializable;

public class <TESTER_IO_CLASS> {

        private int time;
        private boolean status;
        private BufferedInputStream br;
        private BufferedOutputStream bw;
        private BufferedInputStream readBr;
        private BufferedOutputStream readBw;
        private String test;
        private String path;
        private int timeLimit;

    private <CLASS_NAME> sol;

        <CALLING_METHODS>
        public void <METHOD_NAME>(<PARAMS>) {
                try {
                        LongTesterIO.startMethod(bw,<METHOD_NUMBER>);
                        <WRITE_ARGS>
                        LongTesterIO.writeArg(bw, <ARG_NAME>);
                        </WRITE_ARGS>
                        LongTesterIO.flush(bw);
//int xxx = 0;
//int ch; while((ch = br.read()) >= 0){ addFatalError(Integer.toString(ch,16)+" ");xxx++;if(xxx == 19)break; }
//if(br != null)
//return ;
                        time = LongTesterIO.getInt(br);
//addFatalError("time = "+time);
                        LongTesterIO.addTime(time);
                        status = LongTesterIO.getInt(br)>0;
//addFatalError("status = "+status);
                        //<RETURN_TYPE> val = LongTesterIO.<RETURN_METHOD_NAME>(br);

                        //return val;
                } catch(Throwable e) {
                        System.err.println("ERROR");
                        e.printStackTrace();
                        try{
                                br.close();
                                bw.close();
                        } catch (IOException ioe) {
                                ioe.printStackTrace();
                        }
                        status = false;
                        //return <DEFAULT_RETURN>;
                }
        }
        public <RETURN_TYPE> getResult_<METHOD_NAME>() {
                try {
                        <RETURN_TYPE> val = LongTesterIO.<RETURN_METHOD_NAME>(br);
//System.out.println("val = "+val);
                        return val;
                } catch(Throwable e) {
                        System.err.println("ERROR");
                        e.printStackTrace();
                        try{
                                br.close();
                                bw.close();
                        } catch (IOException ioe) {
                                ioe.printStackTrace();
                        }
                        status = false;
                        return <DEFAULT_RETURN>;
                }
        }
        </CALLING_METHODS>

        public void setTotalTime(long l) {
            LongTesterIO.setTime(l);
        }

        public void addFatalError(String s) {
            LongTesterIO.addFatalError(s);
        }
        
        public void setResultObject(Object o) {
            LongTesterIO.setResultObject(o);
        }

        public int getTime() {
                return time;
        }

        public boolean getStatus() {
                return status;
        }

        public String getPath() {
                return path;
        }
        public String getTest() {
                return test;
        }

        public void abort() {
            //in case of emergency, break glass
            try {
                LongTesterIO.shutdownSocket();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setTimeLimit(int limit) {
                timeLimit = limit;
                try {
                    LongTesterIO.setTimeout(bw, limit);
                    LongTesterIO.flush(bw);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
        }

        private class ExposedMethodReader extends Thread {
            public void run() {
                try {
                    while(true) {
                        int command = readBr.read();
                        //System.out.println("GOT: " + command);
                        if(command == LongTesterIO.METHOD_START) {
                            int method = LongTesterIO.getInt2(readBr);

                            switch(method) {
                            <EXPOSED_METHODS>
                                case <METHOD_NUMBER>: {
                                    <ARGS>
                                            <ARG_TYPE> <ARG_NAME> = LongTesterIO.<ARG_METHOD_NAME>(readBr);
                                    </ARGS>

                                    <RETURN_TYPE> val = sol.<METHOD_NAME>(<PARAMS>);

                                    LongTesterIO.writeArg(readBw,val);
                                    LongTesterIO.flush(readBw);
                                    break;
                                }
                            </EXPOSED_METHODS>
                            }

                        } else if(command == LongTesterIO.TERMINATE || command == -1) {
                            //shutdown
                            return;
                        } else {
                            System.out.println("UNKNOWN: " + command);
                        }
                     }
                } catch (NullPointerException e) {
                  //Ignore this one
                } catch (Exception e) {
                    System.err.println("Exposed wrapper error: ");
                    e.printStackTrace();
                }
            }
        }

        public <TESTER_IO_CLASS>(BufferedInputStream br, BufferedOutputStream bw, BufferedInputStream readBr, BufferedOutputStream readBw, String subPath, String test, <CLASS_NAME> sol) {
                this.bw=bw;
                this.br=br;
                this.test=test;
                this.readBw = readBw;
                this.readBr = readBr;
                this.path = subPath;
                this.sol = sol;

                new ExposedMethodReader().start();
        }

     public WriterCache newCacheInstance() {
            try {
                return new WriterCache(PersistentCacheProvider.newInstance("MM_<CLASS_NAME>"));
            } catch (Exception e) {
                return new WriterCache(null);
            }
        }

        public class WriterCache {
            private PersistentCache cache;
            private boolean throwExceptions = false;

            private WriterCache(PersistentCache cache) {
                this.cache = cache;
            }

            public void throwExceptions(boolean value) {
                this.throwExceptions = value;
            }

            public void setMinimalVersion(long version) {
                try {
                    if (cache != null) {
                        cache.setMinimalVersion(version);
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }

            public void put(String key, Object object) {
                try {
                    if (cache != null) {
                        cache.put(key, object);
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }

            public Object get(String key) {
                try {
                    if (cache != null) {
                        return cache.get(key);
                    }
                } catch (Exception e) {
                    handleException(e);
                }
                return null;
            }

            public void remove(String key) {
                try {
                    if (cache != null) {
                        cache.remove(key);
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }

            public void clear() {
                try {
                    if (cache != null) {
                        cache.clear();
                    }
                } catch (Exception e) {
                    handleException(e);
                }
            }

            public int size() {
                try {
                    if (cache != null) {
                        return cache.size();
                    }
                } catch (Exception e) {
                    handleException(e);
                }
                return 0;
            }

public int getUserID(){
return -1;
}

            private void handleException(Exception e) {
                cache = null;
                if (throwExceptions) {
                    throw new RuntimeException(e);
                } else {
                    e.printStackTrace();
                }
            }
        }
}
