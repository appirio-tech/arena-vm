package com.topcoder.server.common;

import java.io.Serializable;

public class AffidavitRecipient implements Serializable {

    private String email;
    private String contestName;
    private java.sql.Date contestStart;
    private String division;
    private float money;
    private int placed;
    private Integer roomId;
    private int tied;
    private String userName;
    private String firstName;
    private String lastName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String country;
    private String zip;
    private java.sql.Date memberSince;
    private String referredBy;
    private String roomDesc;
    private boolean unrated;

//constructor
    public AffidavitRecipient() {
        email = "";
        contestName = "";
        contestStart = null;
        division = "";
        money = 0F;
        placed = 0;
        roomId = new Integer(0);
        tied = 0;
        userName = "";
        firstName = "";
        lastName = "";
        address1 = "";
        address2 = "";
        city = "";
        state = "";
        country = "";
        zip = "";
        memberSince = null;
        referredBy = "";
        roomDesc = "";
    }

//sets
    public void setEmail(String email) {
        this.email = email;
    }

    public void setContestName(String contestName) {
        this.contestName = contestName;
    }

    public void setContestStart(java.sql.Date contestStart) {
        this.contestStart = contestStart;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public void setPlaced(int placed) {
        this.placed = placed;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public void setTied(int tied) {
        this.tied = tied;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public void setAddress2(String address2) {
        if (address2 != null) this.address2 = address2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        if (state != null) this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setMemberSince(java.sql.Date memberSince) {
        this.memberSince = memberSince;
    }

    public void setReferredBy(String referredBy) {
        if (referredBy != null) this.referredBy = referredBy;
    }

    public void setRoomDesc(String roomDesc) {
        this.roomDesc = roomDesc;
    }

    public void setUnrated(boolean unrated) {
        this.unrated = unrated;
    }

//gets
    public String getEmail() {
        return this.email;
    }

    public String getContestName() {
        return this.contestName;
    }

    public java.sql.Date getContestStart() {
        return this.contestStart;
    }

    public String getDivision() {
        return this.division;
    }

    public float getMoney() {
        return this.money;
    }

    public int getPlaced() {
        return this.placed;
    }

    public Integer getRoomId() {
        return this.roomId;
    }

    public int getTied() {
        return this.tied;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getAddress1() {
        return this.address1;
    }

    public String getAddress2() {
        return this.address2;
    }

    public String getCity() {
        return this.city;
    }

    public String getState() {
        return this.state;
    }

    public String getCountry() {
        return this.country;
    }

    public String getZip() {
        return this.zip;
    }

    public java.sql.Date getMemberSince() {
        return this.memberSince;
    }

    public String getReferredBy() {
        return this.referredBy;
    }

    public String getRoomDesc() {
        return this.roomDesc;
    }

    public boolean isUnrated() {
        return unrated;
    }
}
