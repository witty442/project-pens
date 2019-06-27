package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.StoreBean;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;

public class StoreDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static StoreBean getStoreName(String refCode ,String storeNo) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getStoreName(conn, refCode, storeNo);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		
	}
	
	public static StoreBean getStoreName(String refCode ,String storeNo,String storeType) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getStoreName(conn, refCode, storeNo,storeType);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		
	}
	public static StoreBean getStoreName(Connection conn ,String refCode ,String storeCode,String storeType) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StoreBean m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeCode+"' and reference_code ='"+refCode+"' \n");
			
			if( !Utils.isNull(storeType).equalsIgnoreCase("")){
				if(storeType.equalsIgnoreCase("lotus") || storeType.equalsIgnoreCase(Constants.STORE_TYPE_LOTUS_CODE)){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_LOTUS_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("bigc") || storeType.equalsIgnoreCase(Constants.STORE_TYPE_BIGC_CODE)){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_BIGC_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("tops") || storeType.equalsIgnoreCase(Constants.STORE_TYPE_TOPS_CODE)){
					sql.append(" and pens_value LIKE '"+Constants.STORE_TYPE_TOPS_CODE+"%' \n");
				}else if(storeType.equalsIgnoreCase("MTT")
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_MTT_CODE_1)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_KING_POWER)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_HISHER_CODE)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_KING_POWER_2)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_KING_POWER_3)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_KING_POWER_4)
						|| storeType.equalsIgnoreCase(Constants.STORE_TYPE_KING_POWER_5)
				){
					sql.append(" and ( pens_value LIKE '"+Constants.STORE_TYPE_MTT_CODE_1+"%' \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER+"%'  \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_HISHER_CODE+"%'  \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_2+"%'  \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_3+"%'  \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_4+"%'  \n");
					sql.append("     OR pens_value LIKE '"+Constants.STORE_TYPE_KING_POWER_5+"%' ) \n");
				}
			}
			
		   // logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new StoreBean();
				m.setStoreCode(rs.getString("pens_value"));
				m.setStoreName(rs.getString("pens_desc"));
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return m;
	} 
	
	public static StoreBean getStoreName(Connection conn ,String refCode ,String storeCode) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		StoreBean m = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select *  from PENSBME_MST_REFERENCE WHERE  pens_value ='"+storeCode+"' and reference_code ='"+refCode+"' \n");
			
		   // logger.debug("SQL:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				m = new StoreBean();
				m.setStoreCode(rs.getString("pens_value"));
				m.setStoreName(rs.getString("pens_desc"));
				m.setStoreEngName(rs.getString("interface_value"));
			}
		
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
		}
		return m;
	} 
	 public static List<StoreBean> getStoreList(Connection conn,String groupStore) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StoreBean m = null;
			List<StoreBean> items = new ArrayList<StoreBean>();
			try {
				sql.append("\n select pens_value ,pens_desc FROM ");
				sql.append("\n PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='Store' ");
				if("DUTYFREE".equalsIgnoreCase(groupStore)){
					//Case DutyFree more 1 group
					sql.append(genFilterByStoreType(conn, "DUTYFREE", "pens_value"));
				}else{
				   sql.append("\n and pens_value like '"+groupStore+"%'");
				}
				sql.append("\n and status ='Active'  ");
				sql.append("\n order by to_number(REPLACE(pens_value, '-', '')) ");
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
			    while (rst.next()) {
					m = new StoreBean();
					m.setStoreCode(Utils.isNull(rst.getString("pens_value")));
					m.setStoreName(Utils.isNull(rst.getString("pens_desc")));
					items.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return items;
		}
	 
	 public static List<StoreBean> getStoreList(Connection conn,String groupStore,String storeCodeSqlIn) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			StoreBean m = null;
			List<StoreBean> items = new ArrayList<StoreBean>();
			try {
				sql.append("\n select pens_value ,pens_desc FROM ");
				sql.append("\n PENSBME_MST_REFERENCE WHERE 1=1 ");
				sql.append("\n and reference_code ='Store' ");
				if("DUTYFREE".equalsIgnoreCase(groupStore)){
					//Case DutyFree more 1 group
					sql.append(genFilterByStoreType(conn, "DUTYFREE", "pens_value"));
				}else{
				   sql.append("\n and pens_value like '"+groupStore+"%'");
				}
				sql.append("\n and pens_value in("+storeCodeSqlIn+")");
				sql.append("\n and status ='Active'  ");
				sql.append("\n order by to_number(REPLACE(pens_value, '-', ''))");
				logger.debug("sql:"+sql);
				
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
			    while (rst.next()) {
					m = new StoreBean();
					m.setStoreCode(Utils.isNull(rst.getString("pens_value")));
					m.setStoreName(Utils.isNull(rst.getString("pens_desc")));
					items.add(m);
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
				} catch (Exception e) {}
			}
			return items;
		}
	 
	 public static StringBuffer genFilterByStoreType(Connection conn,String storeType,String columnName) throws Exception{
			StringBuffer sqlCond = new StringBuffer("");
			String sqlSelect = "";
			PreparedStatement ps = null;
			ResultSet rs = null;
			int no = 0;
			try{
				sqlSelect  ="select pens_value from PENSBI.PENSBME_MST_REFERENCE where reference_code ='Customer' ";
				sqlSelect +="and pens_desc3 ='"+storeType+"'";
				
				ps = conn.prepareStatement(sqlSelect);
				rs = ps.executeQuery();
				sqlCond.append("\n  and ( ");
				while(rs.next()){
					if(no==0){
					   sqlCond.append("    "+columnName+" like  '"+Utils.isNull(rs.getString("pens_value"))+"%' \n");
					}else{
					   sqlCond.append(" OR "+columnName+" like  '"+Utils.isNull(rs.getString("pens_value"))+"%' \n");
					}
					no++;
				}
				sqlCond.append("      ) \n");
			    return sqlCond;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps !=null){
					ps.close();
				}
				if(rs != null){
					rs.close();
				}
			}
		}
}
