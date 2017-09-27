package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class ReferenceDAO {
private static Logger logger = Logger.getLogger("PENS");

public static String JOB_CUTT_DATE_REF_CODE = "JOB_CUTT_DATE";
	
	public static String getValueByRefCode(String refCode,String code) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String value = null;
		Connection conn = null;
		try {
			sql.append("\n select value FROM PENSBME_C_REFERENCE WHERE 1=1 ");
			sql.append("\n and ref_code = '"+refCode+"' \n");
			sql.append("\n and isactive = 'Y' \n");
			if( !Utils.isNull(code).equals("")){
				sql.append("\n and code = '"+code+"' \n");
			}
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				value = Utils.isNull(rst.getString("value"));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			    conn.close();
			} catch (Exception e) {}
		}
		return value;
	}
}
