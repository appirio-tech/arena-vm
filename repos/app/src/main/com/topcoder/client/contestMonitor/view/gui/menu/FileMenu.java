package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.CommandSender;
import com.topcoder.client.contestMonitor.model.MonitorController.RoundAccess;
import com.topcoder.client.contestMonitor.view.gui.AlreadyDisabledException;
import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.client.contestMonitor.view.gui.RoundAccessFrame;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.response.BlobColumnResponse;
import com.topcoder.server.AdminListener.response.RoundAccessResponse;
import com.topcoder.server.AdminListener.response.TextColumnResponse;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.Map;
import java.util.List;

public final class FileMenu extends MonitorBaseMenu {

    private final MonitorFrame frame;
    private final RoundAccessFrame chooseRoundDialog;
    private JMenu menu;
    private JMenuItem refreshAccessItem, loggingItem, exitItem, objectLoadItem, objectSearchItem, textLoadItem;
    private JMenuItem textSearchItem;
    private JMenuItem chooseRoundItem;
    private ObjectLoaderFrame objectLoaderFrame;
    private ObjectSearchFrame objectSearchFrame;
    private TextLoaderFrame textLoaderFrame;
    private TextSearchFrame textSearchFrame;

    // Constructor
    public FileMenu(Frame parent, CommandSender sender, MonitorFrame frame) {
        super(parent, sender);
        this.frame = frame;

        objectLoaderFrame = new ObjectLoaderFrame(frame, sender);
        objectSearchFrame = new ObjectSearchFrame(frame, sender);
        textLoaderFrame = new TextLoaderFrame(frame, sender);
        textSearchFrame = new TextSearchFrame(frame, sender);

        chooseRoundDialog = new RoundAccessFrame(sender, frame);

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);

        chooseRoundItem = getChooseRoundItem();
        refreshAccessItem = getRefreshAccessItem();
        loggingItem = getLoggingItem();
        exitItem = getExitItem();
        objectLoadItem = getObjectLoadItem();
        objectSearchItem = getObjectSearchItem();
        textLoadItem = getTextLoadItem();
        textSearchItem = getTextSearchItem();

        menu.add(chooseRoundItem);
        menu.add(refreshAccessItem);
        menu.add(loggingItem);
        menu.add(exitItem);
        menu.addSeparator();
        menu.add(objectLoadItem);
        menu.add(objectSearchItem);
        menu.add(textLoadItem);
        menu.add(textSearchItem);
    }

    public void applySecurity(Set allowedFunctions) {
        loggingItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_LOGGING)));
        objectLoadItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_OBJECT_LOAD)));
        objectSearchItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_BLOB_SEARCH)));
        textLoadItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_LOAD)));
        textSearchItem.setEnabled(allowedFunctions.contains(AdminConstants.getPermission(AdminConstants.REQUEST_TEXT_SEARCH)));
    }

    // Individual menu items
    private JMenuItem getChooseRoundItem() {
        return getMenuItem("Load round access...", KeyEvent.VK_C, new Runnable() {
            public void run() {
                try {
                    frame.disableUIPendingResponse(RoundAccessResponse.class, chooseRoundDialog, 120);
                    getSender().sendRoundAccess();
                } catch (AlreadyDisabledException e) {
                    return;
                }
            }
        });
    }


    private JMenuItem getRefreshAccessItem() {
        return getMenuItem("Refresh command access", KeyEvent.VK_R, new Runnable() {
            public void run() {
                getSender().sendRefreshAccess(frame.getIntegerRoundId());
            }
        });
    }

    private JMenuItem getLoggingItem() {
        return getMenuItem("Logging", KeyEvent.VK_L, new Runnable() {
            public void run() {
                frame.getLoggingFrameManager().displayLoggingStreamsFrame();
            }
        });
    }

    private JMenuItem getExitItem() {
        return getMenuItem("Exit", KeyEvent.VK_X, new Runnable() {
            public void run() {
                frame.close();
            }
        });
    }

    private JMenuItem getObjectLoadItem() {
        return getMenuItem("Blob loader...", KeyEvent.VK_B, new Runnable() {
            public void run() {
                try {
                    frame.disableUIPendingResponse(BlobColumnResponse.class, objectLoaderFrame, AdminConstants.BLOB_COLUMN_TIMEOUT);
                    getSender().sendBlobColumnRequest();
                } catch (AlreadyDisabledException e) {
                    return;
                }
            }
        });
    }

    private JMenuItem getObjectSearchItem() {
        return getMenuItem("Blob search...", KeyEvent.VK_S, new Runnable() {
            public void run() {
                try {
                    frame.disableUIPendingResponse(BlobColumnResponse.class, objectSearchFrame, AdminConstants.BLOB_COLUMN_TIMEOUT);
                    getSender().sendBlobColumnRequest();
                } catch (AlreadyDisabledException e) {
                    return;
                }
            }
        });
    }

    private JMenuItem getTextLoadItem() {
        return getMenuItem("Text loader...", KeyEvent.VK_O, new Runnable() {
            public void run() {
                try {
                    frame.disableUIPendingResponse(TextColumnResponse.class, textLoaderFrame, AdminConstants.TEXT_COLUMN_TIMEOUT);
                    getSender().sendTextColumnRequest();
                } catch (AlreadyDisabledException e) {
                    return;
                }
            }
        });
    }

    private JMenuItem getTextSearchItem() {
        return getMenuItem("Text search...", KeyEvent.VK_H, new Runnable() {
            public void run() {
                try {
                    frame.disableUIPendingResponse(TextColumnResponse.class, textSearchFrame, AdminConstants.TEXT_COLUMN_TIMEOUT);
                    getSender().sendTextColumnRequest();
                } catch (AlreadyDisabledException e) {
                    return;
                }
            }
        });
    }

    // Menu retrieval
    public JMenu getFileMenu() {
        return menu;
    }
}
