/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;
import com.topcoder.shared.util.DBMS;
import java.rmi.RemoteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 *
 * @author rfairfax
 */
public class FixPolygonArea {
    
    /** Creates a new instance of RecalcPlaced */
    public FixPolygonArea() {
    }
    
    public static void main(String[] args) {
        try {
            go();
        } catch (NamingException ex) {
            Logger.getLogger(FixPolygonArea.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(FixPolygonArea.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public static void go() throws NamingException, RemoteException {
        try {
            Connection c = DBMS.getDirectConnection();
            PreparedStatement ps = null;
            
            long[] testcases = {26962015,26963484,26964345,26965031,26965050,26965399};
            String[] expected = {"seed = 5951349\nM = 131\n20 vertices\n    5.254 4.459\n    3.671 6.1\n    2.516 7.299\n    2.452 7.365\n    1.799 8.042\n    1.088 8.796\n    0.608 9.305\n    0.559 9.359\n    1.149 8.77\n    1.523 8.395\n    1.693 8.224\n    2.254 7.656\n    2.972 6.905\n    6.95 2.743\n    8.152 1.483\n    8.392 1.231\n    8.294 1.33\n    7.396 2.248\n    6.652 3.015\n    5.638 4.062\n\npolygon area = 0.411\npolygon perimeter = 22.577"
                ,"seed = 601284291\nM = 230\n20 vertices\n    7.916 2.104\n    7.689 2.234\n    4.512 4.054\n    4.398 4.125\n    3.617 4.633\n    3.147 4.952\n    2.939 5.098\n    2.789 5.235\n    2.87 5.19\n    5.022 3.977\n    6.661 3.053\n    7.643 2.499\n    7.803 2.405\n    8.931 1.743\n    9.805 1.227\n    9.867 1.177\n    9.588 1.267\n    8.785 1.655\n    8.448 1.819\n    8.351 1.871\n\npolygon area = 1.297\npolygon perimeter = 16.337"
                ,"seed = 545220673\nM = 147\n19 vertices\n    7.541 0.889\n    8.157 0.523\n    8.449 0.343\n    8.649 0.215\n    8.734 0.155\n    8.603 0.216\n    8.398 0.334\n    5.426 2.072\n    1.598 4.313\n    0.949 4.697\n    0.622 4.903\n    1.027 4.685\n    1.804 4.243\n    2.562 3.809\n    3.477 3.28\n    3.971 2.994\n    4.508 2.681\n    4.97 2.411\n    5.755 1.947\n\npolygon area = 0.462\npolygon perimeter = 18.8"
                ,"seed = 566476814\nM = 101\n17 vertices\n    3.198 7.503\n    1.243 7.324\n    0.755 7.348\n    0.597 7.361\n    0.262 7.392\n    0.222 7.416\n    0.528 7.459\n    1.114 7.506\n    1.437 7.531\n    2.395 7.595\n    2.959 7.632\n    3.861 7.691\n    4.299 7.72\n    5.838 7.816\n    7.241 7.901\n    6.241 7.797\n    4.507 7.625\n\npolygon area = 0.77\npolygon perimeter = 14.092"
                ,"seed = 399442550\nM = 163\n20 vertices\n    3.133 9.449\n    2.911 9.964\n    4.332 5.904\n    5.847 1.58\n    6.347 0.171\n    6.399 0.03\n    6.409 0.083\n    6.358 0.271\n    6.164 0.922\n    6.145 0.985\n    5.69 2.441\n    5.642 2.595\n    5.347 3.495\n    5.208 3.915\n    4.817 5.043\n    4.475 6.025\n    4.055 7.167\n    3.726 8.054\n    3.672 8.195\n    3.497 8.603\n\npolygon area = 1.338\npolygon perimeter = 21.072"
                ,"seed = 2189327\nM = 186\n20 vertices\n    1.28 5.729\n    0.354 5.001\n    0.096 4.829\n    0.18 5.239\n    0.652 6.727\n    1.201 8.446\n    1.235 8.527\n    1.397 8.909\n    1.463 8.933\n    1.895 9.085\n    2.234 9.191\n    2.419 9.244\n    2.58 9.285\n    3.269 9.426\n    3.939 9.531\n    4.001 9.54\n    4.967 9.675\n    5.156 9.695\n    5.757 9.757\n    6.492 9.831\n\npolygon area = 10.646\npolygon perimeter = 17.601"};
            
            String sql = "update system_test_case set expected_result = ? where test_case_id = ? ";
            ps = c.prepareStatement(sql);
            
            for(int i = 0; i < testcases.length; i++) {
                ps.clearParameters();
                ps.setBytes(1, DBMS.serializeBlobObject(expected[i]));
                ps.setLong(2, testcases[i]);
                
                ps.executeUpdate();
            }
            
            ps.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
}
