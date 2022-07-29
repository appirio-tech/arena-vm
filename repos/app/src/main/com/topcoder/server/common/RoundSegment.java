package com.topcoder.server.common;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;


public final class RoundSegment implements Serializable {


    private int contestId;
    private int roundId;
    private int segmentId;
    private String segmentDesc;
    private Timestamp start;
    private String startTime;
    private int startYear;
    private int startMonthNum;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int startSecond;
    private String startAMPM;
    private String startTimeZoneLong;
    private String startTimeZoneShort;
    private Timestamp end;
    private String endTime;
    private int endYear;
    private int endMonthNum;
    private int endDay;
    private int endHour;
    private int endMinute;
    private int endSecond;
    private String endAMPM;
    private String endTimeZoneLong;
    private String endTimeZoneShort;
    private String status;
    private String modified;


    public RoundSegment(int contestId, int round_id) {
        init();
        this.contestId = contestId;
        this.roundId = round_id;
    }


    public RoundSegment() {
        init();
        this.contestId = 0;
        this.roundId = 0;
    }


    private void init() {
        this.segmentId = 0;
        this.segmentDesc = "";
        this.start = null;
        this.startTime = "";
        this.startYear = 0;
        this.startMonthNum = 0;
        this.startDay = 0;
        this.startHour = 0;
        this.startMinute = 0;
        this.startSecond = 0;
        this.startAMPM = "";
        this.startTimeZoneLong = "";
        this.startTimeZoneShort = "";
        this.end = null;
        this.endTime = "";
        this.endYear = 0;
        this.endMonthNum = 0;
        this.endDay = 0;
        this.endHour = 0;
        this.endMinute = 0;
        this.endSecond = 0;
        this.endAMPM = "";
        this.endTimeZoneLong = "";
        this.endTimeZoneShort = "";
        this.status = "";
        this.modified = "";
    }


// set

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }

    public void setContestId(int contestId) {
        this.contestId = contestId;
    }

    public void setRoundId(int roundId) {
        this.roundId = roundId;
    }

    public void setSegmentDesc(String segmentDesc) {
        this.segmentDesc = segmentDesc;
    }

    public void setStart(Timestamp start) {
        this.start = start;
        this.startTime = getTime(start);
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        this.startYear = cal.get(Calendar.YEAR);
        this.startMonthNum = cal.get(Calendar.MONTH);
        this.startDay = cal.get(Calendar.DAY_OF_MONTH);
        this.startHour = cal.get(Calendar.HOUR);
        if (this.startHour == 0) this.startHour = 12;
        this.startMinute = cal.get(Calendar.MINUTE);
        this.startSecond = cal.get(Calendar.SECOND);
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            this.startAMPM = "AM";
        } else {
            this.startAMPM = "PM";
        }
        TimeZone tz = cal.getTimeZone();
        boolean daylight = tz.inDaylightTime(start);
        this.startTimeZoneLong = tz.getDisplayName(daylight, TimeZone.LONG);
        this.startTimeZoneShort = tz.getDisplayName(daylight, TimeZone.SHORT);
    }


/*
  public void setStartYear(int startYear) {
    this.startYear = startYear;
  }
  public void setStartMonthNum(int startMonthNum) {
    this.startMonthNum = startMonthNum;
  }
  public void setStartDay(int startDay) {
    this.startDay = startDay;
  }
  public void setStartHour(int startHour) {
    this.startHour = startHour;
  }
  public void setStartMinute(int startMinute) {
    this.startMinute = startMinute;
  }
  public void setStartSecond(int startSecond) {
    this.startSecond = startSecond;
  }
  public void setStartAMPM(String startAMPM) {
    this.startAMPM = startAMPM;
  }
  public void setStartTimeZoneLong(String startTimeZoneLong) {
    this.startTimeZoneLong = startTimeZoneLong;
  }
  public void setStartTimeZoneShort(String startTimeZoneShort) {
    this.startTimeZoneShort = startTimeZoneShort;
  }
*/


    public void setEnd(Timestamp end) {
        this.end = end;
        this.endTime = getTime(end);
        Calendar cal = Calendar.getInstance();
        cal.setTime(end);
        this.endYear = cal.get(Calendar.YEAR);
        this.endMonthNum = cal.get(Calendar.MONTH);
        this.endDay = cal.get(Calendar.DAY_OF_MONTH);
        this.endHour = cal.get(Calendar.HOUR);
        if (this.endHour == 0) this.endHour = 12;
        this.endMinute = cal.get(Calendar.MINUTE);
        this.endSecond = cal.get(Calendar.SECOND);
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            this.endAMPM = "AM";
        } else {
            this.endAMPM = "PM";
        }
        TimeZone tz = cal.getTimeZone();
        boolean daylight = tz.inDaylightTime(end);
        this.endTimeZoneLong = tz.getDisplayName(daylight, TimeZone.LONG);
        this.endTimeZoneShort = tz.getDisplayName(daylight, TimeZone.SHORT);
    }


/*
  public void setEndYear(int endYear) {
    this.endYear = endYear;
  }
  public void setEndMonthNum(int endMonthNum) {
    this.endMonthNum = endMonthNum;
  }
  public void setEndDay(int endDay) {
    this.endDay = endDay;
  }
  public void setEndHour(int endHour) {
    this.endHour = endHour;
  }
  public void setEndMinute(int endMinute) {
    this.endMinute = endMinute;
  }
  public void setEndSecond(int endSecond) {
    this.endSecond = endSecond;
  }
  public void setEndAMPM(String endAMPM) {
    this.endAMPM = endAMPM;
  }
  public void setEndTimeZoneLong(String endTimeZoneLong) {
    this.endTimeZoneLong = endTimeZoneLong;
  }
  public void setEndTimeZoneShort(String endTimeZoneShort) {
    this.endTimeZoneShort = endTimeZoneShort;
  }
*/


    public void setStatus(String status) {
        this.status = status;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }


// get
    public int getSegmentId() {
        return this.segmentId;
    }

    public int getContestId() {
        return this.contestId;
    }

    public int getRoundId() {
        return this.roundId;
    }

    public String getSegmentDesc() {
        return this.segmentDesc;
    }

    public Timestamp getStart() {
        return this.start;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public int getStartYear() {
        return this.startYear;
    }

    public int getStartMonthNum() {
        return this.startMonthNum;
    }

    public int getStartDay() {
        return this.startDay;
    }

    public int getStartHour() {
        return this.startHour;
    }

    public int setStartMinute() {
        return this.startMinute;
    }

    public int getStartSecond() {
        return this.startSecond;
    }

    public String getStartAMPM() {
        return this.startAMPM;
    }

    public String getStartTimeZoneLong() {
        return this.startTimeZoneLong;
    }

    public String getStartTimeZoneShort() {
        return this.startTimeZoneShort;
    }

    public Timestamp getEnd() {
        return this.end;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public int getEndYear() {
        return this.endYear;
    }

    public int getEndMonthNum() {
        return this.endMonthNum;
    }

    public int getEndDay() {
        return this.endDay;
    }

    public int getEndHour() {
        return this.endHour;
    }

    public int setEndMinute() {
        return this.endMinute;
    }

    public String getEndAMPM() {
        return this.endAMPM;
    }

    public String getEndTimeZoneLong() {
        return this.endTimeZoneLong;
    }

    public String getEndTimeZoneShort() {
        return this.endTimeZoneShort;
    }

    public int getEndSecond() {
        return this.endSecond;
    }

    public String getStatus() {
        return this.status;
    }

    public String getModified() {
        return this.modified;
    }


    //Method to retrieve a time value in hh:mm:ss format from a
    //java.sql.Timestamp object
    private String getTime(Timestamp timeStamp) {
        String timeStampString = null;
        int spaceIndex = 0;
        String timeVal = null;

        if (timeStamp != null) {
            timeStampString = timeStamp.toString();
            spaceIndex = timeStampString.indexOf(" ") + 1;
            timeVal = timeStampString.substring(spaceIndex, spaceIndex + 8);
        }

        return timeVal;
    }


}
