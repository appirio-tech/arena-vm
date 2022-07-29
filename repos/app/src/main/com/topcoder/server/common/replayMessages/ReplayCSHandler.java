package com.topcoder.server.common.replayMessages;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.NetCommonCSHandler;
import com.topcoder.server.common.ActionEvent;
import com.topcoder.server.common.ChatEvent;
import com.topcoder.server.common.CompileEvent;
import com.topcoder.server.common.ContestEvent;
import com.topcoder.server.common.LeaderEvent;
import com.topcoder.server.common.Location;
import com.topcoder.server.common.MoveEvent;
import com.topcoder.server.common.PhaseEvent;
import com.topcoder.server.common.ReplayChallengeEvent;
import com.topcoder.server.common.ReplayCompileEvent;
import com.topcoder.server.common.ReplaySubmitEvent;
import com.topcoder.server.common.ResponseEvent;
import com.topcoder.server.common.SubmitEvent;
import com.topcoder.server.common.TCEvent;
import com.topcoder.server.common.TestEvent;

public final class ReplayCSHandler extends NetCommonCSHandler {

    private static final byte TCEVENT = 72;
    private static final byte CHATEVENT = 73;
    private static final byte COMPILEEVENT = 74;
    private static final byte ACTIONEVENT = 75;
    private static final byte CONTESTEVENT = 76;
    private static final byte LEADEREVENT = 77;
    private static final byte PHASEEVENT = 78;
    private static final byte RESPONSEEVENT = 79;
    private static final byte MOVEEVENT = 80;
    private static final byte TESTEVENT = 81;
    private static final byte SUBMITEVENT = 82;
    private static final byte REPLAYSUBMITEVENT = 83;
    private static final byte REPLAYCHALLENGEEVENT = 84;
    private static final byte REPLAYCOMPILEEVENT = 85;
    private static final byte LOCATION = 86;


    private static final byte HEARTBEAT = 97;
    private static final byte BROADCASTER = 98;
    private static final byte CONFIRMATION = 99;

    public ReplayCSHandler() {
        super(null);
    }

    protected boolean writeObjectOverride2(Object object) throws IOException {
        if (object instanceof HeartbeatMessage) {
            writeByte(HEARTBEAT);
            return true;
        }
        if (object instanceof BroadcasterMessage) {
            writeByte(BROADCASTER);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ConfirmationMessage) {
            writeByte(CONFIRMATION);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ChatEvent) {
            writeByte(CHATEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ActionEvent) {
            writeByte(ACTIONEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof CompileEvent) {
            writeByte(COMPILEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ContestEvent) {
            writeByte(CONTESTEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof PhaseEvent) {
            writeByte(PHASEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ResponseEvent) {
            writeByte(RESPONSEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof LeaderEvent) {
            writeByte(LEADEREVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof MoveEvent) {
            writeByte(MOVEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof TestEvent) {
            writeByte(TESTEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof SubmitEvent) {
            writeByte(SUBMITEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ReplaySubmitEvent) {
            writeByte(REPLAYSUBMITEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ReplayChallengeEvent) {
            writeByte(REPLAYCHALLENGEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof ReplayCompileEvent) {
            writeByte(REPLAYCOMPILEEVENT);
            customWriteObject(object);
            return true;
        }
        if (object instanceof Location) {
            writeByte(LOCATION);
            customWriteObject(object);
            return true;
        }
        if (object instanceof TCEvent) {
            writeByte(TCEVENT);
            customWriteObject(object);
            return true;
        }
        return false;
    }

    protected Object readObjectOverride2(byte type) throws IOException {
        switch (type) {
        case HEARTBEAT:
            return new HeartbeatMessage();
        case BROADCASTER:
            BroadcasterMessage broadcasterMessage = new BroadcasterMessage();
            broadcasterMessage.customReadObject(this);
            return broadcasterMessage;
        case CONFIRMATION:
            ConfirmationMessage confirmationMessage = new ConfirmationMessage();
            confirmationMessage.customReadObject(this);
            return confirmationMessage;
        case CHATEVENT:
            ChatEvent ce = new ChatEvent();
            ce.customReadObject(this);
            return ce;
        case ACTIONEVENT:
            ActionEvent ae = new ActionEvent();
            ae.customReadObject(this);
            return ae;
        case COMPILEEVENT:
            CompileEvent coe = new CompileEvent();
            coe.customReadObject(this);
            return coe;
        case CONTESTEVENT:
            ContestEvent cte = new ContestEvent();
            cte.customReadObject(this);
            return cte;
        case PHASEEVENT:
            PhaseEvent pe = new PhaseEvent();
            pe.customReadObject(this);
            return pe;
        case RESPONSEEVENT:
            ResponseEvent re = new ResponseEvent();
            re.customReadObject(this);
            return re;
        case LEADEREVENT:
            LeaderEvent le = new LeaderEvent();
            le.customReadObject(this);
            return le;
        case MOVEEVENT:
            MoveEvent me = new MoveEvent();
            me.customReadObject(this);
            return me;
        case TESTEVENT:
            TestEvent te = new TestEvent();
            te.customReadObject(this);
            return te;
        case SUBMITEVENT:
            SubmitEvent se = new SubmitEvent();
            se.customReadObject(this);
            return se;
        case REPLAYSUBMITEVENT:
            ReplaySubmitEvent rse = new ReplaySubmitEvent();
            rse.customReadObject(this);
            return rse;
        case REPLAYCHALLENGEEVENT:
            ReplayChallengeEvent rce = new ReplayChallengeEvent();
            rce.customReadObject(this);
            return rce;
        case REPLAYCOMPILEEVENT:
            ReplayCompileEvent rce2 = new ReplayCompileEvent();
            rce2.customReadObject(this);
            return rce2;
        case LOCATION:
            Location l = new Location();
            l.customReadObject(this);
            return l;
        default:
            return super.readObjectOverride2(type);
        }
    }

}
