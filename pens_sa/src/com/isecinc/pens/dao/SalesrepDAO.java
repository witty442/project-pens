package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class SalesrepDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	
	public static SalesrepBean getSalesrepBeanById(Connection conn,String salesrepId){
		SalesrepBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT * from xxpens_salesreps_v M  ");
			sql.append("\n  where  M.salesrep_id ="+salesrepId+"");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				bean = new SalesrepBean();
				bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				bean.setCode(Utils.isNull(rst.getString("code")));
				bean.setSalesrepFullName(Utils.isNull(rst.getString("salesrep_full_name")));
				bean.setRegion(Utils.isNull(rst.getString("region")));
				bean.setRegionName(Utils.isNull(rst.getString("region_name")));
				bean.setSalesChannel(Utils.isNull(rst.getString("sales_channel")));
				bean.setSalesChannelName(Utils.isNull(rst.getString("sales_channel_name")));
			}//if
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return bean;
	}
	public static SalesrepBean getSalesrepBeanByCode(String salesrepCode) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return getSalesrepBeanByCode(conn,salesrepCode);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public static SalesrepBean getSalesrepBeanByCode(Connection conn,String salesrepCode){
		SalesrepBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT * from xxpens_salesreps_v M  ");
			sql.append("\n  where  M.code ='"+salesrepCode+"'");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				bean = new SalesrepBean();
				bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				bean.setCode(Utils.isNull(rst.getString("code")));
				bean.setSalesrepFullName(Utils.isNull(rst.getString("salesrep_full_name")));
				bean.setRegion(Utils.isNull(rst.getString("region")));
				bean.setRegionName(Utils.isNull(rst.getString("region_name")));
				bean.setSalesChannel(Utils.isNull(rst.getString("sales_channel")));
				bean.setSalesChannelName(Utils.isNull(rst.getString("sales_channel_name")));
			}//if
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return bean;
	}
}
