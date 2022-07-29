package com.topcoder.utilities.contestcreate;

import java.util.*;

import java.io.FileInputStream;
import java.io.IOException;

import java.sql.Connection;
//import java.sql.SQLException;

import com.topcoder.shared.util.DBMS;

public class ContestCreate
        extends Creator {

    static final String TABLE_CONTEST = "CONTEST";
    static final String TABLE_ROUND = "ROUND";
    //static final String TABLE_EVENT           = "EVENT";
    static final String TABLE_ROUND_COMPONENT = "ROUND_COMPONENT";
    static final String TABLE_ROUND_SEGMENT = "ROUND_SEGMENT";

    ContestCreate(Properties props) {
        super(props);
    }

    void load(Connection conn) {
        LoadTask[] attrs = getLoadTasks();

        for (int i = 0; i < attrs.length; i++) {
            attrs[i].apply(conn);
        }
    }


    LoadTask[] getLoadTasks() {
        ArrayList rows = new ArrayList();

        addContest(rows);
        addRound(rows);
        //addEvent(rows);
        addRoundProblems(rows);
        addRoundSegments(rows);

        return (LoadTask[]) rows.toArray(new LoadTask[rows.size()]);
    }

    void addContest(Collection rows) {
        LoadRow row = new LoadRow(TABLE_CONTEST);

        row.addKey("contest_id", extractInteger("contest_id"));
        row.add("name", extractString("contest_name"));
        row.add("start_date", extractDate("contest_start_date"));
        row.add("end_date", extractDate("contest_end_date"));
        row.add("status", extractString("contest_status"));
        row.add("language_id", extractInteger("language_id"));
        row.add("group_id", extractInteger("group_id"));
        row.add("region_code", extractString("region_code"));
        row.add("ad_text", extractString("ad_text"));
        row.add("ad_start", extractDate("ad_start"));
        row.add("ad_end", extractDate("ad_end"));
        row.add("ad_task", extractString("ad_task"));
        row.add("ad_command", extractString("ad_command"));
        row.add("activate_menu", extractInteger("activate_menu"));


        rows.add(row);
    }

    void addRound(Collection rows) {
        LoadRow row = new LoadRow(TABLE_ROUND);

        row.addKey("contest_id", extractInteger("contest_id"));
        row.addKey("round_id", extractInteger("round_id"));
        row.add("name", extractString("round_name"));
        row.add("status", extractString("round_status"));
        row.add("registration_limit", extractInteger("registration_limit"));
        row.add("invitational", extractInteger("invitational"));
        row.add("round_type_id", extractInteger("round_type_id"));
        rows.add(row);
    }
/*
    void addEvent(Collection rows) {
        LoadRow row = new LoadRow(TABLE_EVENT);

        row.addKey("event_id",        extractInteger("round_id"));
        row.add("event_type_id",      extractInteger("event_type_id"));
        row.add("event_desc",         extractString("event_desc"));
        row.add("status",             extractString("event_status"));
        row.add("start_registration", extractDate("start_registration"));
        row.add("end_registration",   addMinutes(extractDate("start_registration"),
                                                 extractInteger("registration_length")));
	row.add("event_limit",        extractInteger("event_limit"));
        rows.add(row);
    }
*/

    void addRoundProblems(Collection rows) {
        MultiLoadRow problems = new MultiLoadRow();

        LoadRow keyinfo = new LoadRow(TABLE_ROUND_COMPONENT);
        keyinfo.addKey("round_id", extractInteger("round_id"));
        problems.setKeyInfo(keyinfo);

        String[] problemlist = extractList("round_problems");
        for (int i = 0; i < problemlist.length; i++) {
            String[] words = ListParser.split(problemlist[i]);

            if (words.length != 6) {
                System.err.println("round_problems list item " + i +
                        " does not contain 6 elements");
            }

            try {
                int problemid = Integer.parseInt(words[0]);
                int submitorder = Integer.parseInt(words[1]);
                int division_id = Integer.parseInt(words[2]);
                int difficulty_id = Integer.parseInt(words[3]);
                double points = Double.parseDouble(words[4]);
                int open_order = Integer.parseInt(words[5]);

                LoadRow row = new LoadRow(TABLE_ROUND_COMPONENT);
                row.addKey("round_id", extractInteger("round_id"));
                row.add("component_id", new Integer(problemid));
                row.add("submit_order", new Integer(submitorder));
                row.add("division_id", new Integer(division_id));
                row.add("difficulty_id", new Integer(difficulty_id));
                row.add("points", new Double(points));
                row.add("open_order", new Integer(open_order));
                problems.addRow(row);

            } catch (Exception e) {
                System.err.println("round_problems list item " + i +
                        " contains invalid values");
            }
        }

        rows.add(problems);
    }

    void addRoundSegments(Collection rows) {
        Date regstart = extractDate("start_registration");
        Date regend = addMinutes(regstart, extractInteger("registration_length"));
        Date codestart = extractDate("coding_start");
        Date codeend = addMinutes(codestart, extractInteger("coding_length"));
        Date intend = addMinutes(codeend, extractInteger("intermission_length"));

        Date chalstart = intend;
        Date chalend = addMinutes(intend, extractInteger("challenge_length"));

        LoadRow rowa = new LoadRow(TABLE_ROUND_SEGMENT);
        LoadRow rowb = new LoadRow(TABLE_ROUND_SEGMENT);
        LoadRow rowc = new LoadRow(TABLE_ROUND_SEGMENT);
        LoadRow rowd = new LoadRow(TABLE_ROUND_SEGMENT);
        LoadRow rowe = new LoadRow(TABLE_ROUND_SEGMENT);

        rowa.addKey("round_id", extractInteger("round_id"));
        rowa.addKey("segment_id", new Integer(1));
        rowa.add("start_time", regstart);
        rowa.add("end_time", regend);
        rowa.add("status", extractString("coding_status"));

        rowb.addKey("round_id", extractInteger("round_id"));
        rowb.addKey("segment_id", new Integer(2));
        rowb.add("start_time", codestart);
        rowb.add("end_time", codeend);
        rowb.add("status", extractString("coding_status"));

        rowc.addKey("round_id", extractInteger("round_id"));
        rowc.addKey("segment_id", new Integer(3));
        rowc.add("start_time", codeend);
        rowc.add("end_time", intend);
        rowc.add("status", extractString("intermission_status"));

        rowd.addKey("round_id", extractInteger("round_id"));
        rowd.addKey("segment_id", new Integer(4));
        rowd.add("start_time", chalstart);
        rowd.add("end_time", chalend);
        rowd.add("status", extractString("challenge_status"));

        rowe.addKey("round_id", extractInteger("round_id"));
        rowe.addKey("segment_id", new Integer(5));
        rowe.add("start_time", chalend);
        rowe.add("end_time", chalend);
        rowe.add("status", extractString("system_test_status"));

        rows.add(rowa);
        rows.add(rowb);
        rows.add(rowc);
        rows.add(rowd);
        rows.add(rowe);
    }





    //  --------------------------------------------------

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("ARGS: [property file]");
            return;
        }


        String propfilename = args[0];
        Properties props = new Properties();


        try {
            System.out.println("Reading contest properties...");
            props.load(new FileInputStream(propfilename));
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return;
        }


        ContestCreate creator = new ContestCreate(props);
        Connection dbconn = null;

        try {
            System.out.println("Connecting to database...");
            dbconn = DBMS.getDirectConnection();
            //dbconn = DBMS.POSTGRES_CONNECT_STRING;
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        creator.load(dbconn);

//  	try {
//  	    System.out.println("Commiting changes");
//  	    dbconn.commit();
//  	    System.out.println("Changes written");
//  	}  catch (SQLException e) {
//  	    System.out.println("Exception" + e.getMessage());
//  	}


    }


}
