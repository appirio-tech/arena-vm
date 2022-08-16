package com.topcoder.client.contestApplet.panels.main;

import java.util.*;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestant.*;
import com.topcoder.client.contestant.view.*;
import com.topcoder.netCommon.contestantMessages.response.data.*;

public class FaderPanel extends JPanel implements HeartbeatListener {

    private JLabel round = null;
    private JLabel room = null;
    private JLabel user = null; // user/captain
    private JLabel seed = null; // seed/team
    private JLabel score = null;
    private Contestant model;

    private List rooms = new LinkedList();
    private Iterator iterator = rooms.iterator();
    private boolean everyOtherSecond = false;

    private boolean enabled = false;

    public FaderPanel(Contestant model) {
        super(new GridBagLayout());
        this.model = model;

        setOpaque(false);

        round = new JLabel("");
        room = new JLabel("");
        user = new JLabel("");
        seed = new JLabel("");
        score = new JLabel("");

        round.setForeground(Common.STATUS_COLOR);
        round.setToolTipText("Round");
        room.setToolTipText("Room");
        // TODO figure out teams
//    if (mode==Common.SINGLE) {
        user.setToolTipText("Coder handle.");
        seed.setToolTipText("Room seed.");
////    } else {
//        user.setToolTipText("Team name.");
//        seed.setToolTipText("Team captain.");
//    }
        score.setToolTipText("Current points accumulated.");

        create();

        start(); // todo add dynamic start/stop
        LocalPreferences.getInstance().addSaveObserver(new Observer() {
            public void update(Observable o, Object arg) {
                setEnabled();
            }
        });
        setEnabled();
    }


    private void setEnabled() {
        LocalPreferences pref = LocalPreferences.getInstance();
        enabled = !pref.getBoolean(LocalPreferences.LEADER_TICKER_DISABLED, false);
        if (!enabled) {
            clear();
        }
    }

    public void start() {
        model.addHeartbeatListener(this);
    }

    public void stop() {
        model.removeHeartbeatListener(this);
    }

    private void create() {
        // add the ranking table to the panel
        GridBagConstraints gbc = Common.getDefaultConstraints();

        // set the size
        setMinimumSize(new Dimension(0, 0));
        setPreferredSize(new Dimension(0, 0));

        //gbc.fill = GridBagConstraints.BOTH;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JPanel rndp = new JPanel();
        rndp.setOpaque(false);
        rndp.setLayout(new BoxLayout(rndp, BoxLayout.X_AXIS));
        rndp.setMinimumSize(new Dimension(125, 20));
        rndp.setPreferredSize(new Dimension(125, 20));
        rndp.add(round);

        JPanel rp = new JPanel();
        rp.setOpaque(false);
        rp.setLayout(new BoxLayout(rp, BoxLayout.X_AXIS));
        rp.setMinimumSize(new Dimension(50, 20));
        rp.setPreferredSize(new Dimension(50, 20));
        rp.add(room);

        JPanel up = new JPanel();
        up.setOpaque(false);
        up.setLayout(new BoxLayout(up, BoxLayout.X_AXIS));
        up.setMinimumSize(new Dimension(85, 20));
        up.setPreferredSize(new Dimension(85, 20));
        up.add(user);

        JPanel sp = new JPanel();
        sp.setOpaque(false);
        sp.setLayout(new BoxLayout(sp, BoxLayout.X_AXIS));
        sp.setMinimumSize(new Dimension(15, 20));
        sp.setPreferredSize(new Dimension(15, 20));
        sp.add(seed);

        JPanel cp = new JPanel();
        cp.setOpaque(false);
        cp.setLayout(new BoxLayout(cp, BoxLayout.X_AXIS));
        cp.setMinimumSize(new Dimension(45, 20));
        cp.setPreferredSize(new Dimension(45, 20));
        cp.add(score);

        Common.insertInPanel(rndp, this, gbc, 0, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(rp, this, gbc, 1, 0, 1, 1, 0.1, 0.1);
        Common.insertInPanel(up, this, gbc, 2, 0, 1, 1, 1.0, 0.1);
        Common.insertInPanel(sp, this, gbc, 3, 0, 1, 1, 1.0, 0.1);
        Common.insertInPanel(cp, this, gbc, 4, 0, 1, 1, 0.1, 0.1);
    }


    private void refreshIterator() {
        rooms.clear();
        RoundModel rounds[] = model.getActiveRounds();
        for (int i = 0; i < rounds.length; i++) {
            if (rounds[i].hasCoderRooms()) {
                rooms.addAll(Arrays.asList(rounds[i].getCoderRooms()));
            }
        }
        iterator = rooms.iterator();
    }

    private RoomModel getNextRoom() {
        if (!iterator.hasNext()) {
            refreshIterator();
            if (!iterator.hasNext()) {
                return null;
            }
        }
        return (RoomModel) iterator.next();
    }

//    public void setTickerEnabled(boolean enabled) {
//        this.enabled = enabled;
//    }

    public void tick() {
        // rotate every 2 seconds
        everyOtherSecond = !everyOtherSecond;
        if (!enabled || everyOtherSecond) {
            return;
        }
        updateColors(Color.black, Color.black, 0);
        RoomModel room = getNextRoom();
        if (room != null && room.hasLeader()) {
            LeaderboardItem leader = room.getLeader();
            updateDisplay(
                    getRoundLabel(room.getRoundModel()),
                    getRoomLabel(room),
                    leader.getUserName(),
                    leader.getSeed(),
                    leader.getPoints(),
                    leader.getUserRating(),
                    "");
            repaint();
        }
    }

    private String getRoomLabel(RoomModel room) {
        return room.getName();
//        return room.getRoomNumber().toString();
    }


    private String getRoundLabel(RoundModel round) {
        return round.getDisplayName() + ":";
//        return room.getRoomNumber().toString();
    }

    public void clear() {
        round.setText("");
        room.setText("");
        user.setText("");
        user.setIcon(null);
        seed.setText("");
        score.setText("");
    }


    private void updateDisplay(String round, String room, String u, int sd, double s, int rk, String tm) {
        this.round.setText(round);
        this.room.setText(room);
        user.setText(u);
        seed.setText(String.valueOf(sd));
//    if (mode==Common.SINGLE)  TODO teams?
//    else seed.setText(tm);
        score.setText(Common.formatScore(s));

        updateColors(Common.getRankColor(rk), Color.white, rk);
    }

    private void updateColors(Color c1, Color c2, int rank) {
        room.setForeground(c2);
        user.setForeground(c1);
        user.setIcon(Common.getRankIcon(rank));
        seed.setForeground(c2);
        score.setForeground(c2);
    }
}
