package com.topcoder.services.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ServicesConstants;

/* convenience object that embodies an execed process */

public final class ExecWrapper {
    private static Logger logger = Logger.getLogger(ExecWrapper.class);

    private static final boolean DEFAULT_PTIME = true;
    /* usePosix = use an external wrapper and POSIX calls to enforce timeouts on an
     * entire process group, rather than using a helper thread and
     * Process.destroy() to take out just the initial process. */

    private static final String ALARM = ServicesConstants.ALARM;

    public boolean error, finished;
    public int exitval;
    public long timetaken;
    public String stdout, stderr;

    public ExecWrapper(Object cmd, String[] env, File path, String stdin, int timeout, int maxread) {
        this(cmd, env, path, stdin, timeout, maxread, DEFAULT_PTIME);
    }
    
    public ExecWrapper(Object cmd, String[] env, File path, String stdin, int timeout, int maxread, boolean usePosix) {
        Process proc;
        HelperThread reader1, reader2, timer = null;
        error = finished = false;
        exitval = -1;
        timetaken = System.currentTimeMillis();
        try {
            if (cmd instanceof String) {
                String rcmd = usePosix ? ALARM + " " + timeout + " " + (String) cmd : (String) cmd;
                logger.info("Before ExecWrapper ExecWrapper : "+rcmd+" path:"+path);
                proc = Runtime.getRuntime().exec(rcmd, env, path);
                logger.info("After ExecWrapper ExecWrapper");
            } else {
                String[] rcmd = (String[]) cmd;
                if (usePosix) {
                    int n = rcmd.length;
                    rcmd = new String[2 + n];
                    rcmd[0] = ALARM;
                    rcmd[1] = "" + timeout;
                    logger.info("Before Array Copy");
                    System.arraycopy(cmd, 0, rcmd, 2, n);
                }
                logger.info("Before ExecWrapper Else");
                proc = Runtime.getRuntime().exec(rcmd, env, path);
                logger.info("After ExecWrapper Else");
            }
        } catch (Exception e) {
            timetaken = -1;

            /* copy the exception so we can at least see it */
            if (stderr == null) {
                stderr = "ExecWrapper exception: " + e.toString();
            } else {
                stderr += "\nExecWrapper exception: " + e.toString();
            }

            error = true;
            return;
        }
        if (stdin == null) stdin = "";
        logger.info("Before Helper Thread");
        new HelperThread(proc.getOutputStream(), stdin);
        logger.info("After Helper Thread1");
        reader1 = new HelperThread(proc.getInputStream(), maxread);
        logger.info("After Helper Thread2");
        reader2 = new HelperThread(proc.getErrorStream(), maxread);
        logger.info("After Helper Thread3");
        if (!usePosix)
            timer = new HelperThread(Thread.currentThread(), timeout);
        logger.info("After Helper Thread4");
        try {
            exitval = proc.waitFor();
            if (exitval != 142) /* 142 == 128 | SIGALRM */
                finished = true;
        } catch (InterruptedException e) {
        }
        timetaken = System.currentTimeMillis() - timetaken;
        if (!usePosix) {
            proc.destroy();
            timer.interrupt();
            Thread.interrupted();
            try {
                timer.join();
            } catch (InterruptedException e) {
            }
            Thread.interrupted();
            logger.info("After more stuff");
        }
        try {
            reader1.quit();
            reader2.quit();
            logger.info("After Quit");
            
            reader1.join();
            logger.info("After Join 1");
            reader2.join();
            logger.info("After Join 2");
        } catch (InterruptedException e) {
        }
        
        try {
            proc.destroy();
        } catch (Exception e) {
        }
        stdout = reader1.str;
        stderr = reader2.str;
    }

    private static final class HelperThread extends Thread {

        public String str = "";

        private Object obj = null;
        private int max;
        
        private boolean quit = false;

        private HelperThread(Thread t, int m) {
            obj = t;
            max = m;
            start();
        }
        
        public void quit() {
            quit = true;
        }

        private HelperThread(OutputStream o, String s) {
            obj = o;
            str = s;
            start();
        }

        private HelperThread(InputStream i, int m) {
            obj = i;
            max = m;
            start();
        }

        public void run() {
            if (obj instanceof Thread) {
                try {
                    Thread.sleep(max);
                    ((Thread) (obj)).interrupt();
                } catch (InterruptedException e) {
                }
            } else if (obj instanceof OutputStream) {
                try {
                    ((OutputStream) (obj)).write(str.getBytes());
                    ((OutputStream) (obj)).close();
                } catch (Exception e) {
                }
            } else if (obj instanceof InputStream) {
                byte[] buf = new byte[max];
                
                int ret, ofs = 0;
                try {
                    while (ofs < max) {
                        ret = ((InputStream) obj).read(buf, ofs, max - ofs);
                        if (ret < 0) break;
                        ofs += ret;
                    }
                    if (ofs >= max) {
                        byte[] soak = new byte[4096];
                        while (0 < ((InputStream) obj).read(soak)) {
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                str = new String(buf, 0, ofs);
            }
        }
    }
}
