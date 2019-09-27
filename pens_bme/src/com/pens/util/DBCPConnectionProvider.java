package com.pens.util;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.log4j.Logger;

public class DBCPConnectionProvider {

	private static boolean init = false;
	private static Logger logger = Logger.getLogger("PENS");
	EnvProperties env = EnvProperties.getInstance();
	private static DataSource ds;

	public DBCPConnectionProvider() throws Exception {

		if (init == false) {
			String driver = env.getProperty("db.driver_class");
			String url = env.getProperty("db.url");
			String username = env.getProperty("db.username");
			String password = env.getProperty("db.password");
			
			String pool_maxactive = env.getProperty("db.pool_maxactive");
			String pool_maxidle = env.getProperty("db.pool_maxidle");
			String pool_maxwait = env.getProperty("db.pool_maxwait");

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
				//logger.debug("DBCPConnectionProvider:"+ds);
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
