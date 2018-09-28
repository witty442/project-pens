package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.StoreBean;
import com.pens.util.Utils;

public class StoreDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
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
