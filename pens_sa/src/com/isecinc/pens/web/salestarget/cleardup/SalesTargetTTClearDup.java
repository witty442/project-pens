package com.isecinc.pens.web.salestarget.cleardup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

public class SalesTargetTTClearDup {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			processClearDupMKT_TT("OCT-19","0","501");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/** Case Dup Head and Item TT_L **/
	public static void processClearDupMKT_TT(String period ,String zone,String brand) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.append("\n select * from PENSBI.XXPENS_BI_SALES_TARGET_TT M");
			sql.append("\n where (M.period,brand ,customer_category,zone)");
			sql.append("\n in(");
					 
			sql.append("\n   select M.period,brand ,customer_category,zone");
			sql.append("\n   from  PENSBI.XXPENS_BI_SALES_TARGET_TT M");
			sql.append("\n   where id in(");
			sql.append("\n    select id from  PENSBI.XXPENS_BI_SALES_TARGET_TT  ");
			sql.append("\n    where period='"+period+"' ");
			sql.append("\n    and zone ='"+zone+"'");
			sql.append("\n    and brand ='"+brand+"' ");
			/*******************/
			sql.append("\n    )");
			sql.append("\n   group by M.period,brand ,customer_category,zone ");
			sql.append("\n   having  count(*) >1 ");
			sql.append("\n )");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				deleteByIdMKT_TT(conn, rst.getString("id"), rst.getString("line_id"));
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
	
	public static void deleteByIdMKT_TT(Connection conn,String id,String lineId) throws Exception {
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
			
			deleteLineIdNotRowIdMKT_TT(conn, id, lineId, rowIdNodel);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static void deleteLineIdNotRowIdMKT_TT(Connection conn,String id,String lineId,String rowId) throws Exception{
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
