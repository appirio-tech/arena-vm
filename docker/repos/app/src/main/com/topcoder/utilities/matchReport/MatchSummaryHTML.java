package com.topcoder.utilities.matchReport;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.*;
import com.topcoder.shared.util.ApplicationServer;
import com.topcoder.shared.util.DBMS;

public class MatchSummaryHTML
{

  private static DecimalFormat df = new DecimalFormat("0.00");
  private static DecimalFormat df2 = new DecimalFormat("0.00%");

  //////////////////////////////////////////////////////////////////////////////// 
  public static String run(Connection conn, int roundId)
  //////////////////////////////////////////////////////////////////////////////// 
  {
      
    StringBuffer ret = new StringBuffer();
	
	ret.append("{html} \n");
	
    ret.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/css/style.css\" /> \n");
    ret.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/css/coders.css\" /> \n");
    ret.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/css/stats.css\" /> \n");
    ret.append("<link type=\"text/css\" rel=\"stylesheet\" href=\"/css/tcStyles20080219.css\" /> \n");
    ret.append(" \n");
    ret.append("<div class=\"linkBox\"> \n");
    ret.append("<a href=\"http://apps.topcoder.com/wiki/display/tc/Algorithm+Problem+Set+Analysis\">Archive</a><br /> \n");
    ret.append("<a href=\"http://community.topcoder.com/stat?c=round_overview&er=5&rd=" + roundId + "\">Match Overview</a><br /> \n");
	ret.append("UPDATE FORUM ID AND SRM NUMBER AND DATE!!!!! \n");
    ret.append("<a href=\"http://apps.topcoder.com/forums/?module=ThreadList&forumID=XXXXXX\">Discuss this match</a> \n");
    ret.append("</div> \n");
    ret.append("<span class=\"bodySubtitle\">Single Round Match XXX</span><br /> \n");
    ret.append("Wednesday, November 20th, 2013 \n");
    ret.append("<br /><br /> \n");

    ret.append("<h2>Match summary</h2> \n");
    ret.append("\n");
    ret.append("<p> \n");
    ret.append("### MATCH SUMMARY AND COMMENTARY GOES HERE ### \n");
    ret.append("</p> \n");
    ret.append(" \n");
    ret.append("<H1> \n");
    ret.append("The Problems \n");
    ret.append("</H1>\n");
    ret.append("</p> \n");
    ret.append("\n");

    StringBuffer sqlStr = new StringBuffer(512);

    sqlStr.append("select ");
    sqlStr.append("  p.division_id,  ");
    sqlStr.append("  p.problem_id, ");
    sqlStr.append("  p.level_id, ");
    sqlStr.append("  ( ");
    sqlStr.append("    select ");
    sqlStr.append("      p2.level_id ");
    sqlStr.append("    from ");
    sqlStr.append("      problem p2 ");
    sqlStr.append("    where ");
    sqlStr.append("      p2.round_id = p.round_id and ");
    sqlStr.append("      p2.class_name = p.class_name and ");
    sqlStr.append("      p2.division_id <> p.division_id ");
    sqlStr.append("  ) as shared_with, ");
    sqlStr.append("  r.forum_id ");
    sqlStr.append("from ");
    sqlStr.append("  problem p, ");
    sqlStr.append("  round r ");
    sqlStr.append("where ");
    sqlStr.append("  p.round_id = ? ");
    sqlStr.append("  and p.round_id = r.round_id ");
    sqlStr.append("order by  ");
    sqlStr.append("  p.division_id desc, p.level_id ");

    PreparedStatement ps = null;
    ResultSet rs = null;

    try{

      ps = conn.prepareStatement(sqlStr.toString());
      ps.setInt(1, roundId);
      rs = ps.executeQuery();

      while (rs.next())
      {
        int divisionId = rs.getInt(1);
        int problemId = rs.getInt(2);
        int sharedWith = rs.getInt(4);
        int forumId = rs.getInt(5);

        if (divisionId == 1 && sharedWith > 0)
        {
          continue;
        }
        else if (divisionId == 2 && sharedWith > 0)
        {

          Problem p2 = new Problem(conn, roundId, problemId, 2);
          Problem p1 = new Problem(conn, roundId, problemId, 1);

          ret.append("<font size=\"+2\"> \n");
          //ret.append("<b>" + p2.getClassName() + "</b> ");
          ret.append(
            "<b>" + 
            //"<a href=\"/stat?c=problem_statement&amp;pm=" + problemId + "&amp;rd=" + roundId + "\">" + p2.getClassName() + "</a>" +
            //"<a href=\"/stat?c=problem_statement&amp;pm=" + problemId + "&amp;rd=" + roundId + "\" name=\"" + problemId+ "\">" + p2.getClassName() + "</a>" +
			"<a href=\"/stat?c=problem_statement&pm=" + problemId + "&rd=" + roundId + "\" name=\"" + problemId+ "\">" + p2.getClassName() + "</a>" +
            "</b> \n"
          );
          ret.append("</font> \n");
          //ret.append("<A href=\"Javascript:openProblemRating(" + problemId + ")\"><img src=\"/i/rate_it.gif\" hspace=\"10\" border=\"0\" alt=\"rate it\" /></A> \n");
          ret.append("<A href=\"http://community.topcoder.com/tc?module=ProblemRatingQuestions&pid=\"" + problemId + "\"><img src=\"/i/rate_it.gif\" hspace=\"10\" border=\"0\" alt=\"rate it\" /></A> \n");
		  if (forumId != 0) {
              ret.append("<A HREF=\"http://"+ApplicationServer.FORUMS_SERVER_NAME+"/?module=ThreadList&forumID="+forumId+"\" CLASS=\"statText\"><img src=\"/i/interface/btn_discuss_it.gif\" alt=\"discuss it\" border=\"0\" /></A>\n");
          }
          ret.append("<br> \n");
          ret.append(p2.getStatsHTML());
          ret.append("\n");
          ret.append(p1.getStatsHTML());
          ret.append("<p>\n");
          ret.append("### PROBLEM WRITEUP GOES HERE\n");
          ret.append("</p>\n");
          ret.append("\n");
		  
          ret.append("<P><U>Alternative solutions and additional comments.</U></P>\n");
          ret.append("<P>&lt;Place your comments here&gt;</P>\n");

        }
        else
        {

          Problem p = new Problem(conn, roundId, problemId, divisionId);

          ret.append("<font size=\"+2\"> \n");
          //ret.append("<b>" + p.getClassName() + "</b> ");
          ret.append(
            "<b>" + 
            //"<a href=\"/stat?c=problem_statement&amp;pm=" + problemId + "&amp;rd=" + roundId + "\">" + p.getClassName() + "</a>" +
            //"<a href=\"/stat?c=problem_statement&amp;pm=" + problemId + "&amp;rd=" + roundId + "\" name=\"" + problemId+ "\">" + p2.getClassName() + "</a>" +
			"<a href=\"/stat?c=problem_statement&pm=" + problemId + "&rd=" + roundId + "\" name=\"" + problemId+ "\">" + p.getClassName() + "</a>" +
            "</b> \n"
          );
          ret.append("</font> \n");
          //ret.append("<A href=\"Javascript:openProblemRating(" + problemId + ")\"><img src=\"/i/rate_it.gif\" hspace=\"10\" border=\"0\" alt=\"rate it\" /></A> \n");
          ret.append("<A href=\"http://community.topcoder.com/tc?module=ProblemRatingQuestions&pid=\"" + problemId + "\"><img src=\"/i/rate_it.gif\" hspace=\"10\" border=\"0\" alt=\"rate it\" /></A> \n");
		  
		  if (forumId != 0) {
              ret.append("<A HREF=\"http://"+ApplicationServer.FORUMS_SERVER_NAME+"/?module=ThreadList&forumID="+forumId+"\" CLASS=\"statText\"><img src=\"/i/interface/btn_discuss_it.gif\" alt=\"discuss it\" border=\"0\" /></A>\n");
          }
          ret.append("<br> \n");
          ret.append(p.getStatsHTML());
          ret.append("<p>\n");
          ret.append("### PROBLEM WRITEUP GOES HERE\n");
          ret.append("</p>\n");
          ret.append("\n");
		  
		  ret.append("<P><U>Alternative solutions and additional comments.</U></P>\n");
          ret.append("<P>&lt;Place your comments here&gt;</P>\n");

        }

      }
      rs.close();
      ps.close();
	  
	  
	  ret.append("<P> \n");
	  ret.append("<IMG alt=\"Author\" src=\"http://community.topcoder.com/i/m/vexorian.png\" style=\"width:55px;heigth:61px;border:1px solid #CCCCCC;\"></IMG> \n");
	  ret.append("<BR></BR> \n");
	  ret.append("By <A class=\"coderTextYellow\" href=\"http://www.topcoder.com/tc?module=MemberProfile&cr=22652965\">vexorian</A><BR></BR> \n");
	  ret.append("<I>TopCoder Member</I> \n");
	  ret.append("</P> \n");
	  
	  ret.append("{html} \n");
      
      return ret.toString();

    }catch(Exception e){
      e.printStackTrace();
    }

    return "";
  }

  ////////////////////////////////////////////////////////////////////////////////
  public static void main(String args[]) throws SQLException, Exception
  ////////////////////////////////////////////////////////////////////////////////
  {

    int roundId = new Integer(args[0]).intValue();

    Connection conn = DBMS.getDWConnection();

    String ret = MatchSummaryHTML.run(conn, roundId);
    
    System.out.println(ret);

    conn.close();

  }


}

