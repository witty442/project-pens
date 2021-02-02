package com.isecinc.pens.web.stockmc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class StockMCUtils {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static List<String[]> getUomList(){
		List<String[]> uomList = new ArrayList<String[]>();
		uomList.add(new String[]{"",""});
		uomList.add(new String[]{"CTN","ËÕº"});
		uomList.add(new String[]{"PAC","á¾ç¤"});
		uomList.add(new String[]{"EA","ªÔé¹"});
		
		return uomList;
	}
	
	public static String getBrandGroup(Connection conn,String brandNo){
		String salesChannelDesc = "";
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT brand_group_no from XXPENS_BI_MST_BRAND_GROUP M  ");
			sql.append("\n  where  brand_no ='"+brandNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				salesChannelDesc =Utils.isNull(rst.getString("brand_group_no"));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return salesChannelDesc;
	}
	
	public static List<String[]> getReasonNList(){
		List<String[]> dataList = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try{
			sql.append("\n  SELECT oos_id ,oos_reason from PENSBI.MC_OOS_REASON M  order by oos_id");
			conn = DBConnection.getInstance().getConnectionApps();
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			//add blank
			dataList.add(new String[]{"",""});
			while (rst.next()) {
				dataList.add(new String[]{rst.getString("oos_id"),rst.getString("oos_reason")});
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return dataList;
	}
	public static List<String[]> getReasonDList(){
		List<String[]> dataList = new ArrayList<String[]>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			sql.append("\n  SELECT derange_id ,derange_reason from PENSBI.MC_DERANGED_REASON M order by derange_id");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			//add blank
			dataList.add(new String[]{"",""});
			while (rst.next()) {
				dataList.add(new String[]{rst.getString("derange_id"),rst.getString("derange_reason")});
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return dataList;
	}
}
