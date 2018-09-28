package com.isecinc.core;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import util.Debug;

public class Database {

	private static Logger logger = Logger.getLogger("PENS");
	public static Debug debug = new Debug(false);
	
	/**
	 * Query
	 * 
	 * @param <T>
	 * @param sql
	 * @param parameters
	 * @param classes
	 * @param conn
	 * @return List<T>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> query(String sql, Object[] parameters, Class<T> classes, Connection conn)
			throws Exception {
		List<T> list = new ArrayList<T>();
		PreparedStatement pstmt = null;
		ResultSet rst = null;
		try {
			debug.debug(sql);
			Constructor<?> constructor = classes.getDeclaredConstructor(new Class[] { ResultSet.class });
			pstmt = conn.prepareStatement(sql);
			int i = 1;
			if (parameters != null) {
				for (Object p : parameters) {
					//logger.debug("Parameter:"+p.toString());
					if (p != null) pstmt.setObject(i++, p);
				}
			}
			rst = pstmt.executeQuery();
			while (rst.next()) {
				list.add((T) constructor.newInstance(new Object[] { rst }));
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e) {}
			try {
				pstmt.close();
			} catch (Exception e) {}

		}
		return list;
	}

	/**
	 * Get Current Year
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCurrentYear() throws Exception {
		try {
			return new SimpleDateFormat("yyyy", new Locale("th", "TH")).format(new Date());
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get Current Month
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getCurrentMonth() throws Exception {
		try {
			return new SimpleDateFormat("MM", new Locale("th", "TH")).format(new Date());
		} catch (Exception e) {
			throw e;
		}
	}
}
