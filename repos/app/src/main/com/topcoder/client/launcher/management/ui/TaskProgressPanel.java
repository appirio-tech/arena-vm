package com.topcoder.client.launcher.management.ui;

import javax.swing.JPanel;

import com.topcoder.client.launcher.common.task.ApplicationTaskProgressListener;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.BorderLayout;

public class TaskProgressPanel extends JPanel implements ApplicationTaskProgressListener {
    private int taskMax;

    private JLabel nameLabel = null;

    private JProgressBar progressBar = null;

    private JLabel commentLabel = null;

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setLayout(new BorderLayout());
        commentLabel = new JLabel();
        nameLabel = new JLabel();
        this.add(nameLabel, BorderLayout.NORTH);
        this.add(getProgressBar(), BorderLayout.CENTER);
        this.add(commentLabel, BorderLayout.SOUTH);
    }

    public TaskProgressPanel() {
        initialize();
    }

    public void finish() {
        nameLabel.setText("");
        commentLabel.setText("");
        progressBar.setMaximum(1);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
    }

    public void dispose() {
        finish();
    }

    public void newTask(String name, int max) {
        finish();

        nameLabel.setText(name);
        commentLabel.setText("");
        progressBar.setMaximum(max);
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        progressBar.setString(Integer.toString(0) + "/" + Integer.toString(taskMax));
        taskMax = max;
    }

    public void progress(int progress, String comment) {
        commentLabel.setText(comment);
        progressBar.setValue(progress);
        progressBar.setString(Integer.toString(progress) + "/" + Integer.toString(taskMax));
    }

    /**
     * This method initializes progressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getProgressBar() {
        if (progressBar == null) {
            progressBar = new JProgressBar();
            progressBar.setStringPainted(true);
        }
        return progressBar;
    }
}
