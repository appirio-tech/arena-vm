/*
 * Da Twink Daddy - 05/09/2002 - File Created
 */
package com.topcoder.client.contestMonitor.model;

import com.topcoder.server.listener.monitor.QuestionItem;
import com.topcoder.shared.util.StoppableThread;

/**
 * Takes the QuestionItems from a MonitorNetClient and distributes them to
 * separate, per-room QuestionTableModels.
 *
 *@author    Da Twink Daddy
 *@created   05/09/2002
 */
public class QuestionQueueDistributor implements StoppableThread.Client {

    /** QuestionItem source */
    private MonitorNetClient netClient;

    /** QuestionItem sink */
    private MonitorController controller;

    /** Thread that does all the work */
    private StoppableThread workThread;

    /**
     * Create a new QuestionQueueDistributor for the given MonitorNetClient
     *
     *@param nc  MonitorNetClient that retrieves QuestionItems from the server.
     *@param mc  MonitorContoller that keeps track of per-room QuestionTableModels
     */
    public QuestionQueueDistributor(MonitorNetClient nc, MonitorController mc) {
        netClient = nc;
        controller = mc;
        workThread = new StoppableThread(this, "QuestionQueueDistributor #" + System.identityHashCode(this));
    }

    /**
     * Performs one iteration of the {@link #workThread}. Retrieves one {@link
     * QuestionItem} from {@link #netClient} and puts it in the proper
     * QuestionsTableModel provided by {@link #controller}..
     *
     *@throws java.lang.InterruptedException  Description of the Exception
     */
    public void cycle() throws InterruptedException {
        QuestionItem item = netClient.dequeueQuestionItem();
        int roomID = item.getRoomID();
        QuestionsTableModel qtm = controller.getQuestionsTableModel(roomID);
        if (qtm != null) {
            qtm.addQuestion(item);
        }
    }

    /** Start this object processing. */
    final void start() {
        workThread.start();
    }

    /** Stops the object from retriving QuestionItems. */
    final void stop() {
        try {
            workThread.stopThread();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

