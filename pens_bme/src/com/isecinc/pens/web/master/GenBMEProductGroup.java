package com.isecinc.pens.web.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.Utils;

public class GenBMEProductGroup {
	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			//genBMEProductGroup("SHOP");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void process(Map<String, String> param) throws Exception{
		Connection conn= null;
		PreparedStatement ps = null;
	    ResultSet rs = null;
	    StringBuffer sqlFind = new StringBuffer();
	    String preAction = param.get("preAction");
	    String custGroup = param.get("custGroup");
	    try{
	    	conn = DBConnection.getInstance().getConnectionApps();
	    	conn.setAutoCommit(false);
	    	
	    	if(preAction.equalsIgnoreCase("deleteAll")){
	    		deleteBMEProductGroup(conn, custGroup);
	    	}
	    	
	    	sqlFind.append("\n SELECT DISTINCT MP.PENS_VALUE as PENS_ITEM ");
	    	sqlFind.append("\n FROM PENSBI.PENSBME_MST_REFERENCE MP");
	    	sqlFind.append("\n WHERE 1=1 ");
	    	
	    	/** for shop only */
	    	if("Shop".equalsIgnoreCase(custGroup)){
	    	   sqlFind.append("\n AND reference_code in('7CItem','LotusItem') ");
	    	   sqlFind.append("\n AND MP.pens_desc6 in ('MAYA' , 'TM21') ");
	    	}else if("LOTUS".equalsIgnoreCase(custGroup)){
	    	   sqlFind.append("\n AND reference_code in('LotusItem') ");
	    	}
	    	//for test
	    	//sqlFind.append("\n AND MP.pens_value= '839282' ");
	    	
	    	ps = conn.prepareStatement(sqlFind.toString());
	    	rs = ps.executeQuery();
	    	while(rs.next()){
	    		//check pensItem isExist
	    		if( !findBMEProductGroupExist(conn, custGroup, Utils.isNull(rs.getString("PENS_ITEM")))){
	    		    try{
	    			   insertBMEProductGroup(conn,custGroup,Utils.isNull(rs.getString("PENS_ITEM")));
	    		    }catch(Exception eee){
	    		    	eee.printStackTrace();
	    		    }
	    		}
	    	}
	    	conn.commit();
	    }catch(Exception e){
	    	logger.error(e.getMessage(),e);
	    	conn.rollback();
	    }finally{
	    	try{
		    	ps.close();
		    	rs.close();
		    	conn.close();
	    	}catch(Exception ee){}
	    }
	}

	
	public static void insertBMEProductGroup(Connection conn,String custGroup,String pensItem) throws Exception {
		PreparedStatement ps = null;
		PreparedStatement psIns = null;
		ResultSet rs = null;
		StringBuilder sqlFind = new StringBuilder();
		StringBuilder sqlIns = new StringBuilder();
		try {
			// Case pens_item -> many barcode select first record to insert
			sqlFind.append("\n SELECT ");
	    	sqlFind.append("\n  MP.PENS_VALUE as PENS_ITEM ");
	    	sqlFind.append("\n ,MP.INTERFACE_VALUE as MATERIAL_MASTER ");
	    	sqlFind.append("\n ,MP.INTERFACE_DESC as BARCODE ");
	    	sqlFind.append("\n FROM PENSBI.PENSBME_MST_REFERENCE MP");
	    	sqlFind.append("\n WHERE MP.PENS_VALUE ='"+pensItem+"'");
	    	/** for shop only */
	    	sqlFind.append("\n AND MP.pens_desc6 in ('MAYA' , 'TM21') ");
	    	logger.debug("sqlFind:"+sqlFind.toString());
	    	ps = conn.prepareStatement(sqlFind.toString());
	    	rs = ps.executeQuery();
	    	
	    	//insert 1 pens_item
	    	sqlIns.append("\n INSERT INTO PENSBI.BME_PRODUCT_GROUP ");
	    	sqlIns.append("\n values(?,?,?,?)  ");
			logger.debug("sqlIns:"+sqlIns);
			
			psIns = conn.prepareStatement(sqlIns.toString());
			if(rs.next()){
				psIns.setString(1,custGroup);
				psIns.setString(2, Utils.isNull(rs.getString("PENS_ITEM")));
				psIns.setString(3, Utils.isNull(rs.getString("MATERIAL_MASTER")));
				psIns.setString(4, Utils.isNull(rs.getString("BARCODE")));
				psIns.execute();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				psIns.close();
				rs.close();
			} catch (Exception e) {}
		}
	}
	
	public static boolean findBMEProductGroupExist(Connection conn,String custGroup,String pensItem) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		boolean r = false;
		try {
			sql.append("\n select count(*) as c FROM PENSBI.BME_PRODUCT_GROUP ");
			sql.append("\n where CUST_GROUP = '"+custGroup+"'");
			sql.append("\n and PENS_ITEM = '"+pensItem+"'");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				if(rs.getInt("c") >0){
					r = true;
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {}
		}
		return r;
	}
	
	public static boolean deleteBMEProductGroup(Connection conn,String custGroup) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		boolean r = false;
		try {
			sql.append("\n DELETE FROM PENSBI.BME_PRODUCT_GROUP WHERE CUST_GROUP ='"+custGroup+"'");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			ps.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
		return r;
	}
}
