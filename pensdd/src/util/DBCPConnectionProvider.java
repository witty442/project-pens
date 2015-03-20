package util;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.hibernate.cfg.Configuration;

public class DBCPConnectionProvider {

	private static boolean init = false;
	private Configuration hibernateConfig;

	private static DataSource ds;

	public DBCPConnectionProvider() throws Exception {

		if (init == false) {

			hibernateConfig = new Configuration();
			hibernateConfig.configure();

			String driver = hibernateConfig.getProperty("connection.driver_class");
			String url = hibernateConfig.getProperty("connection.url");
			String username = hibernateConfig.getProperty("connection.username");
			String password = hibernateConfig.getProperty("connection.password");
			String pool_maxactive = hibernateConfig.getProperty("pool_maxactive");
			String pool_maxidle = hibernateConfig.getProperty("pool_maxidle");
			String pool_maxwait = hibernateConfig.getProperty("pool_maxwait");
			String validationQuery = hibernateConfig.getProperty("validationQuery");

			Properties dbcpProperties = new Properties();
			dbcpProperties.setProperty("driverClassName", driver);
			dbcpProperties.setProperty("url", url);
			dbcpProperties.setProperty("username", username);
			dbcpProperties.setProperty("password", password);
			dbcpProperties.setProperty("maxActive", pool_maxactive);
			dbcpProperties.setProperty("maxIdle", pool_maxidle);
			dbcpProperties.setProperty("maxWait", pool_maxwait);
			dbcpProperties.setProperty("removeAbandoned", "true");
			dbcpProperties.setProperty("removeAbandonedTimeout", "400");
			dbcpProperties.setProperty("logAbandoned", "true");
			dbcpProperties.setProperty("validationQuery", validationQuery);
			
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
