package com.topcoder.shared.email;


import com.topcoder.shared.ejb.EmailServices.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.*;


/**
 * The JobMonitor will poll the database and report on the current
 * email jobs that are waiting to be run, are being run, or
 * have finished but are still within their scheduled timeframe.
 *
 * The JobMonitor is not intended to be a production quality item,
 * but may still be useful for what it is.
 *
 * @author   Eric Ellingson
 * @version  $Revision$
 */

public class JobMonitor {


    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Context ctx = null;
        ResourceBundle resource = ResourceBundle.getBundle("Email");
        String contextFactory = resource.getString("context_factory");
        String contextProvider = resource.getString("context_provider");


        Hashtable ht = new Hashtable();
        ht.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        ht.put(Context.PROVIDER_URL, contextProvider);

        try {

            ctx = new InitialContext(ht);
//            EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
            EmailServer svr = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
//            EmailList list = ((EmailListHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailListHome")).create();
//            EmailTemplate template = ((EmailTemplateHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplateHome")).create();

            for (; ;) {
                StringWriter pageStr = new StringWriter();
                PrintWriter page = new PrintWriter(pageStr);

                page.println();
                page.println();
                page.println();
                page.println();
                page.println();
                page.println();
                page.println("The current time is: " + svr.getDate().toString());
                page.println();
                page.println("--TOO EARLY READY--");
                printJobs(page, ctx, svr.READY, svr.BEFORERANGE);
                page.println("--CURRENT READY--");
                printJobs(page, ctx, svr.READY, svr.INRANGE);
                page.println("--TOO LATE READY--");
                printJobs(page, ctx, svr.READY, svr.AFTERRANGE);
                page.println("--ALL ACTIVE--");
                printJobs(page, ctx, svr.ACTIVE, svr.ANYRANGE);
                page.println("--CURRENT COMPLETE--");
                printJobs(page, ctx, svr.COMPLETE, svr.INRANGE);
                page.println("--CURRENT INCOMPLETE--");
                printJobs(page, ctx, svr.INCOMPLETE, svr.INRANGE);
                System.out.print(pageStr.toString());
                Thread.sleep(5000);

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception ignore) {
                }
            }

        }

    }


    /**
     *
     * @param page
     * @param ctx
     * @param status
     * @param range
     */
    static void printJobs(PrintWriter page, Context ctx, int status, int range) {

        try {
            EmailJob job = ((EmailJobHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailJobHome")).create();
            EmailServer svr = ((EmailServerHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailServerHome")).create();
            //EmailList list = ((EmailListHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailList")).create();
            //EmailTemplate template = ((EmailTemplateHome) ctx.lookup("com.topcoder.shared.ejb.EmailServices.EmailTemplate")).create();

            Set jobs = svr.getJobs(status, range);

            Iterator jobItr = jobs.iterator();

            for (; jobItr.hasNext();) {

                int id = ((Integer) jobItr.next()).intValue();

                Map x = job.getJobDetailResults(id);

                if (x.size() < 10) {

                    page.println("Job " + id

                            + ": " + job.getStatusId(id)

                            + "(" + job.getStatusText(id) + ")"

                            + " " + DateFormat.getDateTimeInstance().format(job.getStartAfterDate(id))

                            + " to " + DateFormat.getDateTimeInstance().format(job.getStopBeforeDate(id))

                            + " - " + x.size() + " results found");

                    Set k = x.keySet();

                    Iterator i = k.iterator();

                    for (; i.hasNext();) {

                        Object key = i.next();

                        page.print("    " + key + " - " + x.get(key));

                        page.print(" ");

                        switch (((Integer) x.get(key)).intValue()) {

                            case 0:
                                page.print("TODO");
                                break;

                            case 1:
                                page.print("SENT");
                                break;

                            case 2:
                                page.print("FAILED: ");

                                page.print(job.getJobDetailReason(id, ((Integer) key).intValue()));

                                break;

                            default:
                                break;

                        }

                        page.println();

                    }

                } else {

                    int stat[] = new int[3];

                    Set k = x.keySet();

                    Iterator i = k.iterator();

                    for (; i.hasNext();) {

                        Object key = i.next();

                        int s = ((Integer) x.get(key)).intValue();

                        if (s >= 0 && s < stat.length) stat[s]++;

                    }

                    page.println("Job " + id

                            + ": " + job.getStatusId(id)

                            + "(" + job.getStatusText(id) + ")"

                            + " " + DateFormat.getDateTimeInstance().format(job.getStartAfterDate(id))

                            + " to " + DateFormat.getDateTimeInstance().format(job.getStopBeforeDate(id))

                            + " - " + x.size() + " results found"

                            + " (" + stat[0] + " todo, "

                            + stat[1] + " sent, "

                            + stat[2] + " failed)");

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


}

