package com.topcoder.utilities.matchReport;
import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.*;
import com.topcoder.shared.util.DBMS;

public class Problem
{
  private int roundId;
  private int levelId;
  private int divisionId;
  private int problemId;
  private String className;
  private double pointValue;
  private int coders;
  private int subs;
  private int succ;
  private double totalPoints;
  private String highScore;
  private String highCoder;
  private String bestTime;

  private static DecimalFormat df = new DecimalFormat("0.00%");
  private static DecimalFormat df2 = new DecimalFormat("0.00");
  private static DecimalFormat df3 = new DecimalFormat("0");

  public static void main(String args[]) throws Exception
  {
    Connection conn = DBMS.getDWConnection();

    Problem p = new Problem(conn, 4575, 1625, 2);
    //System.out.println("Class: " + p.getClassName());
    //System.out.println("Used As: " + p.getUsedAs());
    //System.out.println("Value: " + p.getPointValue());
    //System.out.println("Sub Rate: " + p.getSubRate());
    //System.out.println("Succ Rate: " + p.getSuccRate());
    //System.out.println("High Score: " + p.getHighScoreText());
    //System.out.println("Avg Points: " + p.getAvgPoints());
    System.out.println(p.getStatsHTML());

    conn.close();
  }

  public Problem(Connection conn, int roundId, int problemId, int divisionId) 
  {
    this.roundId = roundId;
    this.problemId = problemId;
    this.divisionId = divisionId;

    try{

      popInfo(conn);
      popCoders(conn);
      popStats(conn);
      popHigh(conn);

    }catch(Exception e){
      System.out.println("ERROR: Could not initialize Problem object.");
      e.printStackTrace();
    }
  }

  private void popCoders(Connection conn) throws Exception
  {

    StringBuffer sqlStr = new StringBuffer(256);
    sqlStr.append("select  ");
    sqlStr.append("  count(*) cnt  ");
    sqlStr.append("from ");
    sqlStr.append("  coder c,  ");
    sqlStr.append("  room_result rr  ");
    sqlStr.append("where ");
    sqlStr.append("  rr.round_id = ? and  ");
    sqlStr.append("  rr.division_id = ? and  ");
    sqlStr.append("  rr.coder_id = c.coder_id and  ");
    sqlStr.append("  c.status = 'A' and ");
    sqlStr.append("  rr.attended = 'Y'  ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    ps = conn.prepareStatement(sqlStr.toString());
    ps.setInt(1, this.roundId);
    ps.setInt(2, this.divisionId);

    rs = ps.executeQuery();
    if (rs.next())
    {
      this.coders = rs.getInt(1);
    }
    rs.close();
    ps.close();
    
  }

  private void popInfo(Connection conn) throws Exception
  {

    StringBuffer sqlStr = new StringBuffer(256);
    sqlStr.append("select  ");
    sqlStr.append("  p.level_id,   ");
    sqlStr.append("  p.class_name,  ");
    sqlStr.append("  p.points ");
    sqlStr.append("from   ");
    sqlStr.append("  problem p  ");
    sqlStr.append("where  ");
    sqlStr.append("  p.round_id = ? and  ");
    sqlStr.append("  p.division_id = ? and  ");
    sqlStr.append("  p.problem_id = ? ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    ps = conn.prepareStatement(sqlStr.toString());
    ps.setInt(1, this.roundId);
    ps.setInt(2, this.divisionId);
    ps.setInt(3, this.problemId);

    rs = ps.executeQuery();
    if (rs.next())
    {
      this.levelId = rs.getInt(1);
      this.className = rs.getString(2);
      this.pointValue = rs.getDouble(3);
    }
    rs.close();
    ps.close();
   
  }

  private void popStats(Connection conn) throws Exception
  {

    StringBuffer sqlStr = new StringBuffer(256);
    sqlStr = new StringBuffer(256);
    sqlStr.append("select ");
    sqlStr.append("  sum(case when cp.submission_points > 0 then 1 else 0 end), ");
    sqlStr.append("  sum(case when cp.final_points > 0 then 1 else 0 end), ");
    sqlStr.append("  sum(cp.final_points) ");
    sqlStr.append("from ");
    sqlStr.append("  coder c, ");
    sqlStr.append("  coder_problem cp, ");
    sqlStr.append("  room_result rr ");
    sqlStr.append("where ");
    sqlStr.append("  cp.round_id = ? and ");
    sqlStr.append("  cp.division_id = ? and ");
    sqlStr.append("  cp.problem_id = ? and ");
    sqlStr.append("  cp.round_id = rr.round_id and ");
    sqlStr.append("  cp.coder_id = rr.coder_id and ");
    sqlStr.append("  c.coder_id = rr.coder_id and ");
    sqlStr.append("  c.status = 'A' and ");
    sqlStr.append("  rr.attended = 'Y' ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    ps = conn.prepareStatement(sqlStr.toString());
    ps.setInt(1, this.roundId);
    ps.setInt(2, this.divisionId);
    ps.setInt(3, this.problemId);

    rs = ps.executeQuery();
    if (rs.next())
    {
      this.subs = rs.getInt(1);
      this.succ = rs.getInt(2);
      this.totalPoints = rs.getDouble(3);
    }
    rs.close();
    ps.close();

  }

  private void popHigh(Connection conn) throws Exception
  {

    StringBuffer sqlStr = new StringBuffer(256);
    sqlStr.append("select ");
    sqlStr.append("  cp.final_points, ");
    sqlStr.append("  trunc(round(cp.time_elapsed/1000,0)/60,0) || ' mins ' || ");
    sqlStr.append("  round(cp.time_elapsed/1000,0) - trunc(round(cp.time_elapsed/1000,0)/60,0)*60 || ' secs', ");
    sqlStr.append("  c.handle ");
    sqlStr.append("from ");
    sqlStr.append("  coder c, ");
    sqlStr.append("  coder_problem cp ");
    sqlStr.append("where ");
    sqlStr.append("  cp.round_id = ? and  ");
    sqlStr.append("  cp.division_id = ? and  ");
    sqlStr.append("  cp.problem_id = ? and ");
    sqlStr.append("  cp.end_status_id = 150 and ");
    sqlStr.append("  c.coder_id = cp.coder_id and ");
    sqlStr.append("  c.status = 'A' ");
    sqlStr.append("order by 1 desc ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    ps = conn.prepareStatement(sqlStr.toString());
    ps.setInt(1, this.roundId);
    ps.setInt(2, this.divisionId);
    ps.setInt(3, this.problemId);

    rs = ps.executeQuery();
    if (rs.next())
    {
      this.highScore = rs.getString(1);
      this.bestTime = rs.getString(2);
      this.highCoder = rs.getString(3);
    }
    rs.close();
    ps.close();
   
  }

  private String alphaNumber(int in) {
    if (in == 1)
      return "One";
    else if (in == 2)
      return "Two";
    else if (in == 3)
      return "Three";
    else
      return "";
  }

  public String getUsedAs() {
    return "Division " + alphaNumber(this.divisionId) + " - Level " + alphaNumber(this.levelId);
  }

  public String getClassName() {
    return this.className;
  }

  public String getPointValue() {
    return df3.format(this.pointValue);
  }

  public String getSubRate() {
    return this.subs + " / " + this.coders + " (" + df.format((double)this.subs/(double)this.coders) + ")";
  }

  public String getSuccRate() {
    if (this.subs == 0)
      return this.succ + " / " + this.subs + " (" + df.format(0) + ")";
    else
      return this.succ + " / " + this.subs + " (" + df.format((double)this.succ/(double)this.subs) + ")";
  }

  public String getHighScore() {
    //if (this.highScore == null) {
    //   return "NONE";
    //} else {
       return this.highScore;
    //}
  }

  public String getHighCoder() {
    //if (this.highCoder == null) {
    //   return "NONE";
    //} else {
       return this.highCoder;
    //}
  }

  public String getBestTime() {
    if (this.bestTime == null) {
       return "NONE";
    } else {
       return this.bestTime;
    }
  }

  public String getAvgPoints() {
    if (this.totalPoints == 0)
      return df2.format(this.totalPoints);
    else
      return df2.format((double)this.totalPoints / (double)this.succ);
  }

  public String getAvgPointsText() {
    if (this.succ == 0)
      return "No correct submissions";
    else if (this.succ == 1)
      return getAvgPoints() + " (for " + this.succ + " correct submission)";
    else
      return getAvgPoints() + " (for " + this.succ + " correct submissions)";
  }

  public String getHighScoreText() {
    return "<b>" + getHighCoder() + "</b> for " + getHighScore() + " points (" + getBestTime().trim() + ")";
  }

  public String getStatsHTML() {
    StringBuffer sb = new StringBuffer(128);

    sb.append("Used as: " + getUsedAs() + ": ");
    sb.append("<blockquote><table cellspacing=\"2\"> \n");
    sb.append("  <tr> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      <b>Value</b> \n");
    sb.append("    </td> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      " + getPointValue() + " \n");
    sb.append("    </td> \n");
    sb.append("  </tr> \n");
    sb.append("  <tr> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      <b>Submission Rate</b> \n");
    sb.append("    </td> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      " + getSubRate() + " \n");
    sb.append("    </td> \n");
    sb.append("  </tr> \n");
    sb.append("  <tr> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      <b>Success Rate</b> \n");
    sb.append("    </td> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      " + getSuccRate() + " \n");
    sb.append("    </td> \n");
    sb.append("  </tr> \n");
    sb.append("  <tr> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      <b>High Score</b> \n");
    sb.append("    </td> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      " + getHighScoreText() + " \n");
    sb.append("    </td> \n");
    sb.append("  </tr> \n");
    sb.append("  <tr> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      <b>Average Score</b> \n");
    sb.append("    </td> \n");
    sb.append("    <td class=\"bodyText\" style=\"background: #eee;\"> \n");
    sb.append("      " + getAvgPointsText() + " \n");
    sb.append("    </td> \n");
    sb.append("  </tr> \n");
    sb.append("</table></blockquote> \n");

    return sb.toString();
  }


}
