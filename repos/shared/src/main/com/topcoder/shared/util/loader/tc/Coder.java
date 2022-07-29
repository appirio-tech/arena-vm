package com.topcoder.shared.util.loader.tc;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.loader.BaseDataRetriever;
import com.topcoder.shared.util.loader.BasicQuery;
import com.topcoder.shared.util.loader.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public class Coder extends BaseDataRetriever {

    private static final String insert;
    private static final String update;

    static {
        insert =
                "INSERT INTO coder " +
                        " (coder_id " +
                        " ,state_code " +
                        " ,country_code " +
                        " ,first_name " +
                        " ,last_name " +
                        " ,address1 " +
                        " ,address2 " +
                        " ,city " +
                        " ,zip " +
                        " ,middle_name " +
                        " ,activation_code " +
                        " ,member_since " +
                        " ,quote " +
                        " ,language_id " +
                        " ,coder_type_id " +
                        " ,handle " +
                        " ,status " +
                        " ,email " +
                        " ,comp_country_code " +
                        " ,last_site_hit_date)" +
                        " VALUES (" +
                        " ?,?,?,?,?,?,?,?,?,?," +
                        " ?,?,?,?,?,?,?,?,?,?)";

        update = "UPDATE coder " +
                " SET state_code = ? " +
                " ,country_code = ? " +
                " ,first_name = ? " +
                " ,last_name = ? " +
                " ,address1 = ? " +
                " ,address2 = ? " +
                " ,city = ? " +
                " ,zip = ? " +
                " ,middle_name = ? " +
                " ,activation_code = ? " +
                " ,member_since = ? " +
                " ,quote = ? " +
                " ,language_id = ? " +
                " ,coder_type_id = ? " +
                " ,handle = ? " +
                " ,status = ? " +
                " ,email = ? " +
                " ,comp_country_code = ? " +
                " ,last_site_hit_date = ? " +
                " WHERE coder_id = ? ";

    }

    private static final String select =
            "SELECT c.coder_id" +
                    " ,a.state_code " +
                    " ,a.country_code " +
                    " ,u.first_name " +
                    " ,u.last_name " +
                    " ,a.address1 " +
                    " ,a.address2 " +
                    " ,a.city " +
                    " ,a.zip " +
                    " ,u.middle_name " +
                    " ,u.activation_code " +
                    " ,c.member_since " +
                    " ,c.quote " +
                    " ,c.language_id " +
                    " ,c.coder_type_id " +
                    " ,u.handle " +
                    " ,u.status " +
                    " ,e.address " +
                    " ,c.comp_country_code" +
                    " ,u.last_site_hit_date" +
                    " FROM coder c " +
                    " ,user u " +
                    " ,email e " +
                    " ,user_address_xref x " +
                    " ,address a " +
                    " WHERE c.coder_id = u.user_id " +
                    " AND u.user_id = e.user_id " +
                    " and e.primary_ind = 1 " +
                    " and a.address_id = x.address_id " +
                    " and a.address_type_id = 2 " +
                    " and x.user_id = u.user_id " +
                    " AND (c.modify_date > ? OR a.modify_date > ? OR e.modify_date > ? OR u.modify_date > ?)" +
                    " AND NOT EXISTS " +
                    " (SELECT 'pops' " +
                    " FROM user_group_xref ugx " +
                    " WHERE ugx.login_id= c.coder_id " +
                    " AND ugx.group_id = 2000115)" +
                    " AND NOT EXISTS " +
                    " (SELECT 'pops' " +
                    " FROM group_user gu " +
                    " WHERE gu.user_id = c.coder_id " +
                    " AND gu.group_id = 13)";

    private static final String coders =
            "SELECT coder_id " +
                    " FROM coder ";

    public void run() throws Exception {
        log.debug("start coder load");
        PreparedStatement psSel = null;
        PreparedStatement psSel2 = null;

        ResultSet rs = null;
        ResultSet rs2 = null;

        try {

            Timestamp lastLoad = getLastUpdateItem(targetConn, CODER_LOG_TYPE);

            psSel = sourceConn.prepareStatement(select);

            psSel.setTimestamp(1, lastLoad);
            psSel.setTimestamp(2, lastLoad);
            psSel.setTimestamp(3, lastLoad);
            psSel.setTimestamp(4, lastLoad);
            rs = psSel.executeQuery();
            log.debug("before set");
            rs.setFetchSize(psSel.getMaxRows());
            rs.setFetchDirection(ResultSet.FETCH_FORWARD);
            
            log.debug("after set");

            psSel2 = targetConn.prepareStatement(coders);

            rs2 = psSel2.executeQuery();
            HashSet coderSet = new HashSet(psSel2.getMaxRows());
            log.debug("before coder set load");
            while (rs2.next()) {
                coderSet.add(new Long(rs2.getLong(1)));
            }
            log.debug("aftercoder set load");

            ArrayList updates = new ArrayList(1000);
            ArrayList inserts = new ArrayList(1000);

            Query q;
            int count = 0;
            while (rs.next()) {
                long coderId = rs.getLong("coder_id");

                if (coderSet.contains(new Long(coderId))) {
                    q = new BasicQuery(update);
                    q.addArg(rs.getString("state_code"));
                    q.addArg(rs.getString("country_code"));
                    q.addArg(rs.getString("first_name"));
                    q.addArg(rs.getString("last_name"));
                    q.addArg(rs.getString("address1"));
                    q.addArg(rs.getString("address2"));
                    q.addArg(rs.getString("city"));
                    q.addArg(rs.getString("zip"));
                    q.addArg(rs.getString("middle_name"));
                    q.addArg(rs.getString("activation_code"));
                    q.addArg(rs.getTimestamp("member_since"));
                    q.addArg(rs.getString("quote"));
                    q.addArg(rs.getInt("language_id"));
                    q.addArg(rs.getInt("coder_type_id"));
                    q.addArg(rs.getString("handle"));
                    q.addArg(rs.getString("status"));
                    q.addArg(rs.getString("address"));
                    q.addArg(rs.getString("comp_country_code"));
                    q.addArg(rs.getTimestamp("last_site_hit_date"));
                    q.addArg(coderId);
                    updates.add(q);
                } else {
                    q = new BasicQuery(insert);
                    q.addArg(coderId);
                    q.addArg(rs.getString("state_code"));
                    q.addArg(rs.getString("country_code"));
                    q.addArg(rs.getString("first_name"));
                    q.addArg(rs.getString("last_name"));
                    q.addArg(rs.getString("address1"));
                    q.addArg(rs.getString("address2"));
                    q.addArg(rs.getString("city"));
                    q.addArg(rs.getString("zip"));
                    q.addArg(rs.getString("middle_name"));
                    q.addArg(rs.getString("activation_code"));
                    q.addArg(rs.getTimestamp("member_since"));
                    q.addArg(rs.getString("quote"));
                    q.addArg(rs.getInt("language_id"));
                    q.addArg(rs.getInt("coder_type_id"));
                    q.addArg(rs.getString("handle"));
                    q.addArg(rs.getString("status"));
                    q.addArg(rs.getString("address"));
                    q.addArg(rs.getString("comp_country_code"));
                    q.addArg(rs.getTimestamp("last_site_hit_date"));
                    inserts.add(q);
                }
                count++;
/*
                if (log.isDebugEnabled() && count%25==0) {
*/
                if (log.isDebugEnabled()) {
                    log.debug(count + " rows selected for coder load");
                }
            }

            processingQueue.addAll(inserts);
            processingQueue.addAll(updates);

        } catch (SQLException sqle) {
            DBMS.printSqlException(true, sqle);
            throw new Exception("Load of 'coder' table failed.\n" +
                    sqle.getMessage());
        } finally {
            DBMS.close(rs);
            DBMS.close(rs2);
            DBMS.close(psSel);
            DBMS.close(psSel2);
        }
    }

}
