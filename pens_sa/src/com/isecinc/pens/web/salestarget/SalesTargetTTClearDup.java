package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import util.DBConnection;

public class SalesTargetTTClearDup {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			processClearDupMKT("AUG-19","1");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void processClearDupMKT(String period ,String zone) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			sql.append("\n select id,line_id ,count(*) as c from  PENSBI.XXPENS_BI_SALES_TARGET_TT_L");
			sql.append("\n where id in(");
			sql.append("\n   select id from  PENSBI.XXPENS_BI_SALES_TARGET_TT  ");
			sql.append("\n   where period='"+period+"' ");
			sql.append("\n    where zone ='"+zone+"'");
			//for sub test
			//sql.append("\n  and brand ='503' ");
			/*******************/
			sql.append("\n  )");
			sql.append("\n group by id,line_id ");
			sql.append("\n having  count(*) >1 ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				deleteByIdMKT(conn, rst.getString("id"), rst.getString("line_id"));
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	
	public static void deleteByIdMKT(Connection conn,String id,String lineId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String rowIdNodel = "";
		int i = 0;
		try {
			sql.append("\n select rowid,id,line_id,inventory_item_code,target_qty ");
			sql.append("\n from  PENSBI.XXPENS_BI_SALES_TARGET_TT_L  ");
			sql.append("\n where id = "+id+" and line_id = "+lineId) ;
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   if(i==0){
				   rowIdNodel = rst.getString("rowId");
			   }
			   i++;
			}//while
			
			deleteLineIdNotRowIdMKT(conn, id, lineId, rowIdNodel);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static void deleteLineIdNotRowIdMKT(Connection conn,String id,String lineId,String rowId) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteLineIdNotRowId ID["+id+"]lineId["+lineId+"] no del RowId["+rowId+"]");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.XXPENS_BI_SALES_TARGET_TT_L  \n");
			sql.append(" WHERE ID = "+id+" and line_id ="+lineId+" and rowId <> '"+rowId+"'  \n" );

			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
}
