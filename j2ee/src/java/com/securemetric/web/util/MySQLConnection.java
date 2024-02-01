/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.securemetric.web.util;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author auyong
 */
public class MySQLConnection {
    public Connection openConnection() {

	try {
		Class.forName("com.mysql.jdbc.Driver");
	} catch (ClassNotFoundException e) {
		System.out.println("Where is your MySQL JDBC Driver?");
		e.printStackTrace();
		return null;
	}

	Connection connection = null;
        Config config = new Config();
        Properties dbProperties = config.getConfig(Config.TYPE_DB);
        // ResourceBundle resource = ResourceBundle.getBundle("dbconfig");
	try {
                String connString = "jdbc:mysql://"+ dbProperties.getProperty(Config.IP) + ":"+dbProperties.getProperty(Config.PORT)+"/" + dbProperties.getProperty(Config.SCHEMA);
  
               
		connection = DriverManager.getConnection(connString, dbProperties.getProperty(Config.USERNAME), dbProperties.getProperty(Config.PASSWORD));

	} catch (SQLException e) {
		e.printStackTrace();
		return null;
	}
        
        return connection;
  }
}
