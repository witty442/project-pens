package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

public class BMECControlDAO {
	private static Logger logger = Logger.getLogger("PENS");
	public static String TYPE_ONHAND_DATE_LOTUS_AS_OF ="ONHAND_DATE_LOTUS_AS_OF";
	
	 public static String getOnhandDateLotusAsOf(Connection conn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			String date ="";
			try {
				sql.append("\n select con_value FROM ");
				sql.append("\n PENSBI.PENSBME_C_CONTROL WHERE 1=1 ");
				sql.append("\n and con_type ='"+TYPE_ONHAND_DATE_LOTUS_AS_OF+"'");
			
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				
				if (rst.next()) {
					date = Utils.isNull(rst.getString("con_value"));
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return date;
		}
}
