package com.topcoder.utilities.email;

import java.io.Serializable;
import java.sql.Date;

/////////////////////////////////////////////////////////

final class EMailMessage implements Serializable {

/////////////////////////////////////////////////////////

    //private int MailId;
    private String MailSubject;
    private java.sql.Date MailSentDate;
    private String MailText;
    private String MailToAddress;
    private String MailFromAddress;
    private int CoderId;
    //private String Reason;
    private String Mode;

    EMailMessage() {
        //MailId = 0;
        MailSubject = "";
        MailSentDate = null;
        MailText = "";
        MailToAddress = "";
        MailFromAddress = "";
        CoderId = 0;
        //Reason = "";
        Mode = "";
    }

// set
    /*
  public void setMailId(int MailId) {
    this.MailId = MailId;
  }
  */

    void setMailSubject(String MailSubject) {
        this.MailSubject = MailSubject;
    }

    void setMailSentDate(java.sql.Date MailSentDate) {
        this.MailSentDate = MailSentDate;
    }

    void setMailText(String MailText) {
        this.MailText = MailText;
    }

    void setMailToAddress(String MailToAddress) {
        this.MailToAddress = MailToAddress;
    }

    void setMailFromAddress(String MailFromAddress) {
        this.MailFromAddress = MailFromAddress;
    }

    void setCoderId(int CoderId) {
        this.CoderId = CoderId;
    }

    /*
  public void setReason(String Reason) {
    this.Reason = Reason;
  }
  */

    void setMode(String Mode) {
        this.Mode = Mode;
    }

// get
    /*
  public int getMailId() {
    return MailId;
  }
  */

    String getMailSubject() {
        return MailSubject;
    }

    java.sql.Date getMailSentDate() {
        return MailSentDate;
    }

    String getMailText() {
        return MailText;
    }

    String getMailToAddress() {
        return MailToAddress;
    }

    String getMailFromAddress() {
        return MailFromAddress;
    }

    int getCoderId() {
        return CoderId;
    }

    String getMode() {
        return Mode;
    }

    /*
  public String getReason() {
    return Reason;
  }
  */

}
