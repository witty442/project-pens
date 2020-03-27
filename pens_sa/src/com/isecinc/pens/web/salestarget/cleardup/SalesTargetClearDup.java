package com.isecinc.pens.web.salestarget.cleardup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

public class SalesTargetClearDup {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void main(String[] a){
		try{
			processClearDupTEMPHead("APR-20","3","722");
			
			//processClearDupTEMPItem("APR-20","3");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void processClearDupTEMPHead(String period ,String zone,String brand) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		String tableName = "PENSBI.XXPENS_BI_SALES_TARGET_TEMP";
		Map<String, String> salerepMap = new HashMap<String, String>();
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.append("\n select id,salesrep_code from "+tableName+" ");
			sql.append("\n  where (period,customer_category,sales_channel,salesrep_code,brand) in( ");
			sql.append("\n   select period,customer_category,sales_channel,salesrep_code,brand");
			sql.append("\n   from  "+tableName+"  ");
			sql.append("\n   where period='"+period+"' ");
			sql.append("\n   and brand = '"+brand+"'  ");
			
			sql.append("\n   and salesrep_id in(");
			sql.append("\n     SELECT salesrep_id FROM PENSBI.XXPENS_BI_MST_SALES_ZONE");
			sql.append("\n     where zone ='"+zone+"'");
			sql.append("\n   )");
			//for sub test
			//sql.append("\n   and salesrep_code = '"+salesrepCode+"'  ");
			//sql.append("\n   and sales_channel = '3'   ");
			/*******************/
		
			sql.append("\n group by period,customer_category,sales_channel,salesrep_code,brand ");
			sql.append("\n having  count(*) >1 ");
			sql.append("\n )");
			sql.append("\n order by salesrep_code,id asc");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				if(salerepMap.get(rst.getString("salesrep_code")) ==null){
				     logger.debug("salesrepCode["+rst.getString("salesrep_code")+"],ID:"+rst.getInt("id") +" delete");
				
				     deleteHeadAndItemById(conn,tableName, rst.getString("id"));
				}else{
					 logger.debug("salesrepCode["+rst.getString("salesrep_code")+"],ID:"+rst.getInt("id")+" no delete");
					 logger.debug("************************************");
				}
				
				salerepMap.put(rst.getString("salesrep_code"),rst.getString("salesrep_code")) ;
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
	public static void deleteHeadAndItemById(Connection conn,String tableName,String id) throws Exception {
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n delete from  "+tableName+" where id = "+id);
			ps = conn.prepareStatement(sql.toString());
			logger.debug("del Head:"+ps.executeUpdate());
			
			sql = new StringBuilder();
			sql.append("\n delete from  "+tableName+"_L where id = "+id);
			ps = conn.prepareStatement(sql.toString());
			logger.debug("del Item:"+ps.executeUpdate());
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static void processClearDupTEMPItem(String period ,String zone) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			sql.append("\n select id,line_id ,count(*) as c from  PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L");
			sql.append("\n where id in(");
			sql.append("\n   select id from  PENSBI.XXPENS_BI_SALES_TARGET_TEMP  ");
			sql.append("\n   where period='"+period+"' ");
			sql.append("\n   and salesrep_id in(");
			sql.append("\n    SELECT salesrep_id FROM PENSBI.XXPENS_BI_MST_SALES_ZONE");
			sql.append("\n    where zone ='"+zone+"'");
			sql.append("\n    )");
			//for sub test
			//sql.append("\n  and salesrep_code ='V006' ");
			//sql.append("\n  and brand ='503' ");
			/*******************/
			sql.append("\n  )");
			sql.append("\n group by id,line_id ");
			sql.append("\n having  count(*) >1 ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				//deleteByIdTEMP(conn, rst.getString("id"), rst.getString("line_id"));
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
	
	public static void deleteByIdTEMP(Connection conn,String id,String lineId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String rowIdNodel = "";
		int i = 0;
		try {
			sql.append("\n select rowid,id,line_id,inventory_item_code,target_qty ");
			sql.append("\n from  PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L  ");
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
			
			deleteLineIdNotRowIdTEMP(conn, id, lineId, rowIdNodel);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static void deleteLineIdNotRowIdTEMP(Connection conn,String id,String lineId,String rowId) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteLineIdNotRowId ID["+id+"]lineId["+lineId+"] no del RowId["+rowId+"]");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L  \n");
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
