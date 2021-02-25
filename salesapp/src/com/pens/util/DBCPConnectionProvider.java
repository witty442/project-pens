package com.pens.util;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;


public class DBCPConnectionProvider {

	private static boolean init = false;

	private static DataSource ds;

	public DBCPConnectionProvider() throws Exception {

		if (init == false) {
			EnvProperties env = EnvProperties.getInstance();
			
			String driver = env.getProperty("connection.driver_class");
			String url = env.getProperty("connection.url");
			String username = env.getProperty("connection.username");
			String password = env.getProperty("connection.password");
			String pool_maxactive = env.getProperty("pool_maxactive");
			String pool_maxidle = env.getProperty("pool_maxidle");
			String pool_maxwait = env.getProperty("pool_maxwait");

			Properties dbcpProperties = new Properties();
			dbcpProperties.setProperty("driverClassName", driver);
			dbcpProperties.setProperty("url", url);
			dbcpProperties.setProperty("username", username);
			dbcpProperties.setProperty("password", password);
			dbcpProperties.setProperty("maxActive", pool_maxactive);
			dbcpProperties.setProperty("maxIdle", pool_maxidle);
			dbcpProperties.setProperty("maxWait", pool_maxwait);

			try {
				ds = BasicDataSourceFactory.createDataSource(dbcpProperties);
				init = true;
			} catch (Exception err) {
				throw err;
			}

		}
	}

	public Connection getConnection(Connection conn) throws Exception {
		if (conn == null) {
			conn = ds.getConnection();
		}
		return conn;
	}
}
