/*
 * User: Mike Cervantes (emcee)
 * Date: May 16, 2002
 * Time: 8:57:08 PM
*/
package com.topcoder.server.contest;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;


public class ContestData implements CustomSerializable, Serializable {

    private int id = 0;
    private String name = "";
    private Date startDate = new Date();
    private Date endDate = new Date();
    private int groupId = -1;
    private String status = "A";
    private String adText = "";
    private Date adStartDate = new Date();
    private Date adEndDate = new Date();
    private String adTask = "";
    private String adCommand = "";
    private boolean activateMenu = false;
    private Season season = new Season();
    
    public ContestData() {
        
    }
    
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(id);
        writer.writeString(name);
        if(startDate == null)
            writer.writeLong(0);
        else
            writer.writeLong(startDate.getTime());
        if(endDate == null)
            writer.writeLong(0);
        else
            writer.writeLong(endDate.getTime());
        writer.writeInt(groupId);
        writer.writeString(status);
        writer.writeString(adText);
        if(adStartDate == null)
            writer.writeLong(0);
        else
            writer.writeLong(adStartDate.getTime());
        if(adEndDate == null)
            writer.writeLong(0);
        else
            writer.writeLong(adEndDate.getTime());
        writer.writeString(adTask);
        writer.writeString(adCommand);
        writer.writeBoolean(activateMenu);
        writer.writeObject(season);
    }
    
    public void customReadObject(CSReader reader) throws IOException {
        id = reader.readInt();
        name = reader.readString();
        long l = reader.readLong();
        if(l == 0)
            startDate = null;
        else
            startDate = new Date(l);
        
        l = reader.readLong();
        if(l == 0)
            endDate = null;
        else
            endDate = new Date(l);
        
        groupId = reader.readInt();
        status = reader.readString();
        adText = reader.readString();
        l = reader.readLong();
        if(l == 0)
            adStartDate = null;
        else
            adStartDate = new Date(l);
        
        l = reader.readLong();
        if(l == 0)
            adEndDate = null;
        else
            adEndDate = new Date(l);
        
        adTask = reader.readString();
        adCommand = reader.readString();
        activateMenu = reader.readBoolean();
        season = (Season)reader.readObject();
    }

    public ContestData(int id, String name, Date startDate, Date endDate, int groupId, String adText, Date adStartDate, Date adEndDate, String adTask, String adCommand, String status, boolean activateMenu, Season season) {
        this.activateMenu = activateMenu;
        this.adCommand = adCommand;
        this.adEndDate = adEndDate;
        this.adStartDate = adStartDate;
        this.adTask = adTask;
        this.adText = adText;
        this.endDate = endDate;
        this.groupId = groupId;
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.status = status;
        this.season = season;
    }

    /**
     * Constructs new ContestData with specified ID. This constructor should
     * be used when new contest(round) should be created.<p>
     * In order to meet the "1.2.1 Using Sequences when creating rounds" 
     * requirement the ID for this constructor should be requested from
     * Admin Listener server by ContestManagementController in 
     * ContestSelectionFrame when "Add" button is pressed.
     *
     * @param  id an ID of newly created contest(round)
     * @since  Admin Tool 2.0
     */
    public ContestData(int id) {
        this.id = id;
    }

    public boolean isActivateMenu() {
        return activateMenu;
    }

    public void setActivateMenu(boolean activateMenu) {
        this.activateMenu = activateMenu;
    }

    public String getAdCommand() {
        return adCommand;
    }

    public void setAdCommand(String adCommand) {
        this.adCommand = adCommand;
    }

    public Date getAdEndDate() {
        return adEndDate;
    }

    public void setAdEndDate(Date adEndDate) {
        this.adEndDate = adEndDate;
    }

    public void setAdEndDate(long adEndDate) {
        this.adEndDate = new Date(adEndDate);
    }


    public Date getAdStartDate() {
        return adStartDate;
    }

    public void setAdStartDate(Date adStartDate) {
        this.adStartDate = adStartDate;
    }

    public void setAdStartDate(long adStartDate) {
        this.adStartDate = new Date(adStartDate);
    }


    public String getAdTask() {
        return adTask;
    }

    public void setAdTask(String adTask) {
        this.adTask = adTask;
    }

    public String getAdText() {
        return adText;
    }

    public void setAdText(String adText) {
        this.adText = adText;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = new Date(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = new Date(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Season getSeason() {
        return season;
    }
    
    public void setSeason(Season season) {
        this.season = season;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ContestData) {
            return id == ((ContestData) obj).getId();
        }
        return false;
    }
}
