/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.controller;

import java.sql.Connection;
import com.securemetric.web.util.MySQLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;


/**
 *
 * @author auyong
 */
public class UserManagement {
    public boolean addUser(String userid, String username, String loginname) throws SQLException {
        
        boolean blnresult = false;
        MySQLConnection mySQLConnection = new MySQLConnection();
        Connection conn = mySQLConnection.openConnection();
        
        try {
                Calendar calendar = Calendar.getInstance();
                java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());

                // the mysql insert statement
                String query = " insert into user (id, username, loginname)"
                  + " values (?, ?, ?)";

                // create the mysql insert preparedstatement
                PreparedStatement preparedStmt = conn.prepareStatement(query);
                preparedStmt.setString (1, userid);
                preparedStmt.setString (2, username);
                preparedStmt.setString (3, loginname);

                // execute the preparedstatement
                preparedStmt.execute();
                System.out.println("DEBUG Message[UserManagement] : Success add "+ username + " into local server database");
                blnresult = true;
        }
        catch (Exception e) {
            System.out.println("DEBUG Message[UserManagement] : Failed add "+ username + " into local server database" + e.getMessage());
            e.printStackTrace();
        }
        finally {
            conn.close();
        }
        
        return blnresult;   
    }
    
     public String readUserByName(String username) throws SQLException {
        
        String loginname = "";
        MySQLConnection mySQLConnection = new MySQLConnection();
        Connection conn = mySQLConnection.openConnection();
        Statement stmt = null;
        ResultSet rs = null;
        try {
                 // create the mysql query statement
                String query = "select loginname from user where username= '"+username+"'";
                // create the java statement
                stmt = conn.createStatement();
                
                PreparedStatement ps = conn.prepareStatement(query);
               // execute the query, and get a java resultset
                rs = stmt.executeQuery(query);
                // iterate through the java resultset
                while (rs.next())
                {
                  loginname = rs.getString("loginname");
                }

        }
        catch (Exception e) {
            System.out.println("DEBUG Message[readUserByName] : Failed add "+ username + " into local server database" + e.getMessage());
            e.printStackTrace();
        }
        finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore

                stmt = null;
            }
        }
        
        return loginname;   
    }
}
