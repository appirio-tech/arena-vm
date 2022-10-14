package com.topcoder.shared.netCommon.screening;

import java.io.IOException;

import com.topcoder.shared.netCommon.screening.request.ScreeningCompileRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningErrorRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningGenericPopupRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningKeepAliveRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningLoginRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningLogoutRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningOpenComponentForCodingRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningSaveRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningSubmitRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningTestRequest;
import com.topcoder.shared.netCommon.screening.request.ScreeningTunnelIPRequest;
import com.topcoder.shared.netCommon.screening.response.ScreeningCompileResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningKeepAliveResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningLoginResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningOpenComponentResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningPopUpGenericResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningSaveResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningSubmitResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningSynchTimeResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningTestResponse;
import com.topcoder.shared.netCommon.screening.response.ScreeningUnsynchronizeResponse;
import com.topcoder.shared.netCommon.CSHandler;

public final class ScreeningCSHandler extends CSHandler {

    //Requests
    private static final byte LOGINREQUEST = 101;
    private static final byte LOGOUTREQUEST = 102;
    //private static final byte GETPROBLEMREQUEST = 103;
    private static final byte SAVEREQUEST = 104;
    private static final byte COMPILEREQUEST = 105;
    private static final byte TESTREQUEST = 106;
    private static final byte SUBMITREQUEST = 107;

    private static final byte OPENCOMPONENTREQUEST = 109;
    private static final byte KEEPALIVEREQUEST = 110;
    private static final byte ERRORREQUEST = 111;
    private static final byte GENERICPOPUPREQUEST = 112;

    private static final byte TUNNELIPREQUEST = 115;

    //Responses
    private static final byte LOGINRESPONSE = 51;
    //private static final byte CREATEPROBLEMSRESPONSE = 52;
    //private static final byte GETPROBLEMRESPONSE = 53;
    private static final byte SYNCHTIMERESPONSE = 55;
    private static final byte OPENCOMPONENTRESPONSE = 56;
    private static final byte POPUPGENERICRESPONSE = 57;
    private static final byte KEEPALIVERESPONSE = 58;
    private static final byte UNSYNCHRONIZERESPONSE = 59;

    private static final byte SAVERESPONSE = 60;
    private static final byte COMPILERESPONSE = 61;
    private static final byte TESTRESPONSE = 62;
    private static final byte SUBMITRESPONSE = 63;

    private String dump(int code) {
        switch (code) {
        case LOGINREQUEST:
            return "LoginReq";
        case LOGOUTREQUEST:
            return "LogoutReq";
        case SAVEREQUEST:
            return "SaveReq";
        case COMPILEREQUEST:
            return "CompileReq";
        case TESTREQUEST:
            return "TestReq";
        case SUBMITREQUEST:
            return "SubmitReq";
        case OPENCOMPONENTREQUEST:
            return "OpenCompReq";
        case KEEPALIVEREQUEST:
            return "KeepAliveReq";
        case ERRORREQUEST:
            return "ErrorReq";
        case GENERICPOPUPREQUEST:
            return "PopupReq";
        case TUNNELIPREQUEST:
            return "TunnelReq";

        case LOGINRESPONSE:
            return "LoginRes";
        case SYNCHTIMERESPONSE:
            return "SynchTimeRes";
        case OPENCOMPONENTRESPONSE:
            return "OpenCompRes";
        case POPUPGENERICRESPONSE:
            return "PopupRes";
        case KEEPALIVERESPONSE:
            return "KeepAliveRes";
        case UNSYNCHRONIZERESPONSE:
            return "UnsynchRes";

        case SAVERESPONSE:
            return "SaveRes";
        case COMPILERESPONSE:
            return "CompileRes";
        case TESTRESPONSE:
            return "TestRes";
        case SUBMITRESPONSE:
            return "SubmitRes";
        default :
            return "InvalidType, code: " + code;
        }
    }

    public ScreeningCSHandler() {
    }

    protected boolean writeObjectOverride(Object object) throws IOException {
        byte RC = 0;

        if (object instanceof ScreeningLoginRequest)
            RC = LOGINREQUEST;
        else if (object instanceof ScreeningLogoutRequest)
            RC = LOGOUTREQUEST;
        else if (object instanceof ScreeningSaveRequest)
            RC = SAVEREQUEST;
        else if (object instanceof ScreeningCompileRequest)
            RC = COMPILEREQUEST;
        else if (object instanceof ScreeningTestRequest)
            RC = TESTREQUEST;
        else if (object instanceof ScreeningSubmitRequest)
            RC = SUBMITREQUEST;
        else if (object instanceof ScreeningOpenComponentForCodingRequest)
            RC = OPENCOMPONENTREQUEST;
        else if (object instanceof ScreeningKeepAliveRequest)
            RC = KEEPALIVEREQUEST;
        else if (object instanceof ScreeningErrorRequest)
            RC = ERRORREQUEST;
        else if (object instanceof ScreeningGenericPopupRequest)
            RC = GENERICPOPUPREQUEST;
        else if (object instanceof ScreeningTunnelIPRequest)
            RC = TUNNELIPREQUEST;

        else if (object instanceof ScreeningLoginResponse)
            RC = LOGINRESPONSE;
        else if (object instanceof ScreeningSynchTimeResponse)
            RC = SYNCHTIMERESPONSE;
        else if (object instanceof ScreeningOpenComponentResponse)
            RC = OPENCOMPONENTRESPONSE;
        else if (object instanceof ScreeningPopUpGenericResponse)
            RC = POPUPGENERICRESPONSE;
        else if (object instanceof ScreeningKeepAliveResponse)
            RC = KEEPALIVERESPONSE;
        else if (object instanceof ScreeningUnsynchronizeResponse)
            RC = UNSYNCHRONIZERESPONSE;
        else if (object instanceof ScreeningSaveResponse)
            RC = SAVERESPONSE;
        else if (object instanceof ScreeningCompileResponse)
            RC = COMPILERESPONSE;
        else if (object instanceof ScreeningTestResponse)
            RC = TESTRESPONSE;
        else if (object instanceof ScreeningSubmitResponse)
            RC = SUBMITRESPONSE;

        if (RC != 0) {
            writeByte(RC);
            customWriteObject(object);
            return true;
        }

        return false;
    }

    protected Object readObjectOverride(byte type) throws IOException {
        switch (type) {
        case LOGINREQUEST:
            ScreeningLoginRequest loginReq = new ScreeningLoginRequest();
            loginReq.customReadObject(this);
            return loginReq;
        case LOGOUTREQUEST:
            ScreeningLogoutRequest logoutReq = new ScreeningLogoutRequest();
            logoutReq.customReadObject(this);
            return logoutReq;
        case SAVEREQUEST:
            ScreeningSaveRequest saveReq = new ScreeningSaveRequest();
            saveReq.customReadObject(this);
            return saveReq;
        case COMPILEREQUEST:
            ScreeningCompileRequest compileReq =
                    new ScreeningCompileRequest();
            compileReq.customReadObject(this);
            return compileReq;
        case TESTREQUEST:
            ScreeningTestRequest testReq = new ScreeningTestRequest();
            testReq.customReadObject(this);
            return testReq;
        case SUBMITREQUEST:
            ScreeningSubmitRequest submitReq = new ScreeningSubmitRequest();
            submitReq.customReadObject(this);
            return submitReq;
        case OPENCOMPONENTREQUEST:
            ScreeningOpenComponentForCodingRequest openReq =
                    new ScreeningOpenComponentForCodingRequest();
            openReq.customReadObject(this);
            return openReq;
        case KEEPALIVEREQUEST:
            ScreeningKeepAliveRequest keepAliveReq =
                    new ScreeningKeepAliveRequest();
            keepAliveReq.customReadObject(this);
            return keepAliveReq;
        case ERRORREQUEST:
            ScreeningErrorRequest errorReq = new ScreeningErrorRequest();
            errorReq.customReadObject(this);
            return errorReq;
        case GENERICPOPUPREQUEST:
            ScreeningGenericPopupRequest popupReq =
                    new ScreeningGenericPopupRequest();
            popupReq.customReadObject(this);
            return popupReq;
        case TUNNELIPREQUEST:
            ScreeningTunnelIPRequest tunnelIPReq =
                    new ScreeningTunnelIPRequest();
            tunnelIPReq.customReadObject(this);
            return tunnelIPReq;

        case LOGINRESPONSE:
            ScreeningLoginResponse loginRes = new ScreeningLoginResponse();
            loginRes.customReadObject(this);
            return loginRes;
        case SYNCHTIMERESPONSE:
            ScreeningSynchTimeResponse synchTimeRes =
                    new ScreeningSynchTimeResponse();
            synchTimeRes.customReadObject(this);
            return synchTimeRes;
        case OPENCOMPONENTRESPONSE:
            ScreeningOpenComponentResponse openCompRes =
                    new ScreeningOpenComponentResponse();
            openCompRes.customReadObject(this);
            return openCompRes;
        case POPUPGENERICRESPONSE:
            ScreeningPopUpGenericResponse popupRes =
                    new ScreeningPopUpGenericResponse();
            popupRes.customReadObject(this);
            return popupRes;
        case KEEPALIVERESPONSE:
            ScreeningKeepAliveResponse keepAliveRes =
                    new ScreeningKeepAliveResponse();
            keepAliveRes.customReadObject(this);
            return keepAliveRes;
        case UNSYNCHRONIZERESPONSE:
            ScreeningUnsynchronizeResponse unsynchRes =
                    new ScreeningUnsynchronizeResponse();
            unsynchRes.customReadObject(this);
            return unsynchRes;
        case SAVERESPONSE:
            ScreeningSaveResponse saveRes =
                    new ScreeningSaveResponse();
            saveRes.customReadObject(this);
            return saveRes;
        case COMPILERESPONSE:
            ScreeningCompileResponse compileRes =
                    new ScreeningCompileResponse();
            compileRes.customReadObject(this);
            return compileRes;
        case TESTRESPONSE:
            ScreeningTestResponse testRes =
                    new ScreeningTestResponse();
            testRes.customReadObject(this);
            return testRes;
        case SUBMITRESPONSE:
            ScreeningSubmitResponse submitRes =
                    new ScreeningSubmitResponse();
            submitRes.customReadObject(this);
            return submitRes;

        default :
            return super.readObjectOverride(type);
        }
    }
}
