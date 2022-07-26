/**
 * LoginRequest.java Description: Specifies a login request for both spectator and contest applets
 * 
 * @author Tim "Pops" Roberts
 * @version 1.1
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.SealedSerializable;

/**
 * Defines a request to log in the current user.<br>
 * Use: When the current user wants to log in and proceed to use the arena's feature, this request should be sent.<br>
 * Note: Most of the requests must be sent after this request, unless it is noted otherwise. The login request should
 * not be sent while the user is already logged in. Most of the information are optional, except <code>userid</code>,
 * <code>password</code> and <code>loginType</code>. When phone number, first name, last name, email and company
 * name are specified, the user is registered for TopCoder automatically (deprecated). The password must be encrypted
 * using the key exchanged.
 * 
 * @author Tim "Pops" Roberts
 * @version $Id: LoginRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see LogoutRequest
 */
public final class LoginRequest extends BaseRequest {
    /** Represents the handle of the user to be logged in. */
    private String userid;

    /** Represents the encrypted password of the user to be logged in. */
    private SealedSerializable password;

    /** Represents the handle of the user to be logged in. */
    private String newHandle;

    /** Represents the phone number of the user to be logged in. */
    private String phoneNumber;

    /** Represents the type of login. */
    private int type;

    /** Represents the arena protocol version used by the client. */
    private int protocolVersion;

    /** Represents the ID of the badge of the user to be logged in. */
    private String badgeId;

    /** Represents the first name of the user to be logged in. */
    private String firstName;

    /** Represents the last name of the user to be logged in. */
    private String lastName;

    /** Represents the email of the user to be logged in. */
    private String email;

    /** Represents the company name of the user to be logged in. */
    private String companyName;

    /**
     * Creates a new instance of <code>LoginRequest</code>. It is required by custom serialization.
     */
    public LoginRequest() {
    }

    /**
     * Creates a new instance of <code>LoginRequest</code>. All optional fields are initialized as empty. For arena
     * client, the login type is <code>ContestConstants.LOGIN</code>.
     * 
     * @param userid the handle of the user for authentication.
     * @param password the encrypted password of the user for authentication.
     * @param loginType the type of login.
     * @see ContestConstants#SPECTATOR_LOGIN
     * @see ContestConstants#FORWARDER_LOGIN
     * @see ContestConstants#GUEST_LOGIN
     * @see ContestConstants#LOGIN
     */
    public LoginRequest(String userid, SealedSerializable password, int loginType) {
        this(userid, password, null, loginType, null, null, null, null, "", null);
    }

    /**
     * Creates a new instance of <code>LoginRequest</code>. The protocol version will be initialized as specified in
     * <code>ContestConstants.PROTOCOL_VERSION</code>. All optional fields can be <code>null</code> or empty
     * string. For arena client, the login type is <code>ContestConstants.LOGIN</code>.
     * 
     * @param userid the handle of the user for authentication.
     * @param password the password of the user for authentication.
     * @param newHandle the handle of the user. It is not used now.
     * @param loginType the type of login.
     * @param badgeId the badge ID of the user. It is not used now.
     * @param firstName the first name of the user.
     * @param lastName the last name of the user.
     * @param email the email address of the user.
     * @param companyName the company name of the user.
     * @param phoneNumber the phone number of the user.
     * @see ContestConstants#SPECTATOR_LOGIN
     * @see ContestConstants#FORWARDER_LOGIN
     * @see ContestConstants#GUEST_LOGIN
     * @see ContestConstants#LOGIN
     */
    public LoginRequest(String userid, SealedSerializable password, String newHandle, int loginType, String badgeId,
        String firstName, String lastName, String email, String companyName, String phoneNumber) {
        this.userid = userid;
        this.password = password;
        this.newHandle = newHandle;
        type = loginType;
        this.badgeId = badgeId;
        this.companyName = companyName;
        this.phoneNumber = setToNullIfEmpty(phoneNumber);
        this.firstName = setToNullIfEmpty(firstName);
        this.lastName = setToNullIfEmpty(lastName);
        this.email = setToNullIfEmpty(email);
        protocolVersion = ContestConstants.PROTOCOL_VERSION;
        if (loginType != ContestConstants.LOGIN && loginType != ContestConstants.SPECTATOR_LOGIN
            && loginType != ContestConstants.GUEST_LOGIN && loginType != ContestConstants.FORWARDER_LOGIN) {
            throw new IllegalArgumentException("Bad login type!");
        }
    }

    /**
     * Returns <code>null</code> when the string is an empty string (length is 0).
     * 
     * @param s the string.
     * @return the string as argument or <code>null</code> if <code>s</code> is an empty string.
     */
    private static String setToNullIfEmpty(String s) {
        return s == null || s.length() == 0 ? null : s;
    }

    /**
     * Gets the handle of the user for authentication.
     * 
     * @return the handle of the user.
     */
    public String getUserID() {
        return userid;
    }

    /**
     * Gets the encrypted password of the user for authentication.
     * 
     * @return the encrypted password of the user.
     */
    public SealedSerializable getPassword() {
        return password;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(userid);
        writer.writeObject(password);
        writer.writeString(newHandle);
        writer.writeInt(type);
        writer.writeInt(protocolVersion);
        writer.writeString(badgeId);
        writer.writeString(firstName);
        writer.writeString(lastName);
        writer.writeString(email);
        writer.writeString(companyName);
        writer.writeString(phoneNumber);
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        userid = reader.readString();
        password = (SealedSerializable) reader.readObject();
        newHandle = reader.readString();
        type = reader.readInt();
        protocolVersion = reader.readInt();
        badgeId = reader.readString();
        firstName = reader.readString();
        lastName = reader.readString();
        email = reader.readString();
        companyName = reader.readString();
        phoneNumber = reader.readString();
    }

    /**
     * Gets the string representation of this object
     * 
     * @return the string representation of this object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.LoginRequest) [");
        ret.append("userid = ");
        if (userid == null) {
            ret.append("null");
        } else {
            ret.append(userid.toString());
        }
        ret.append(", ");
        ret.append("password = ");
        if (password == null) {
            ret.append("null");
        } else {
            ret.append(password.toString());
        }
        ret.append(", ");
        ret.append("type = ");
        ret.append(type);
        ret.append(", ");
        ret.append("protocolVersion = ");
        ret.append(protocolVersion);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    public int getRequestType() {
        return type;
    }

    /**
     * Gets the protocol version used by the client.
     * 
     * @return the protocol version.
     */
    public int getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Gets the handle of the user. It can be <code>null</code>. When it is specified, it must be the same as
     * <code>userid</code>.
     * 
     * @return the handle of the user.
     */
    public String getNewHandle() {
        return newHandle;
    }

    /**
     * Gets the badge ID of the user. It is not used. It can be <code>null</code>.
     * 
     * @return the badge ID of the user.
     */
    public String getBadgeId() {
        return badgeId;
    }

    /**
     * Gets the first name of the user. It can be <code>null</code>. When it is specified, a new handle is
     * registered.
     * 
     * @return the first name of the user.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user. It can be <code>null</code>. When it is specified, a new handle is registered.
     * 
     * @return the last name of the user.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the user. It can be <code>null</code>. When it is specified, a new handle is
     * registered.
     * 
     * @return the email address of the user.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the phone number of the user. It can be <code>null</code>. When it is specified, a new handle is
     * registered.
     * 
     * @return the phone number of the user.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the company name of the user. It can be <code>null</code>. When it is specified, a new handle is
     * registered.
     * 
     * @return the company name of the user.
     */
    public String getCompanyName() {
        return companyName;
    }
}
