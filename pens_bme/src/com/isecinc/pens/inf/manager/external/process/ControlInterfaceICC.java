package com.isecinc.pens.inf.manager.external.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class ControlInterfaceICC {
	private static Logger logger = Logger.getLogger("PENS");

	 public static void updateTransInterfaceICC(String productType) throws Exception {
			Statement stmt = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n update PENSBME_CONTROL_INTERFACE_ICC set current_trans_product ='"+productType+"' ");
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				int u = stmt.executeUpdate(sql.toString());
				if(u==0){
					 stmt.executeUpdate("insert into PENSBME_CONTROL_INTERFACE_ICC(current_trans_product,action_date)values('"+productType+"',sysdate)");
				}

			} catch (Exception e) {
				throw e;
			} finally {
				try {
		
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
		}
	 
	 public static String getCurrentTransInterfaceICC() throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			String current_trans_product = "";
			try {
				sql.delete(0, sql.length());
				sql.append("\n SELECT current_trans_product FROM PENSBME_CONTROL_INTERFACE_ICC ");

				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no = 0;
				if (rst.next()) {	
					current_trans_product = Utils.isNull(rst.getString("current_trans_product"));
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
			return current_trans_product;
		}
}
