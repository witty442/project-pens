package com.isecinc.pens.web.billplan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.Utils;

public class BillPlanDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchBillTListTotalRec(Connection conn,BillPlanBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			if("summary".equalsIgnoreCase(o.getDispType())){
				sql.append("\n select count(*) as c from (");
				sql.append("\n   select zone,code,salesrep_full_name ,shipment_num, ");
				sql.append("\n   creation_date ,sum(quantity_shipped) as quantity_shipped");
				sql.append("\n   from apps.xxpens_inv_bill_t_incomplete_v H");
				sql.append("\n   WHERE 1=1 ");
				 //GenWhereSQL
				sql.append(" "+  genWhereCondSql(conn,o));
				sql.append("\n   group by zone,code,salesrep_full_name ,shipment_num, ");
				sql.append("\n   creation_date ");
				sql.append("\n  )");
			}else{
				sql.append("\n select count(*) as c from apps.xxpens_inv_bill_t_incomplete_v H");
				sql.append("\n    WHERE 1=1 ");
				 //GenWhereSQL
				sql.append(" "+   genWhereCondSql(conn,o));
			}
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				totalRec = rst.getInt("c");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
	public static List<BillPlanBean> searchBillTList(Connection conn,BillPlanBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		BillPlanBean h = null;
		List<BillPlanBean> items = new ArrayList<BillPlanBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select B.* FROM( ");
			
			if("summary".equalsIgnoreCase(o.getDispType())){
				sql.append("\n   select zone,zone_name,code,salesrep_full_name ,shipment_num, ");
				sql.append("\n   creation_date ,sum(quantity_shipped) as quantity_shipped");
				sql.append("\n   from apps.xxpens_inv_bill_t_incomplete_v H");
				sql.append("\n   WHERE 1=1 ");
				 //GenWhereSQL
				sql.append(" "+  genWhereCondSql(conn,o));
				sql.append("\n   group by zone,zone_name,code,salesrep_full_name ,shipment_num, ");
				sql.append("\n   creation_date ");
			}else{
				sql.append("\n  select *");
				sql.append("\n  FROM apps.xxpens_inv_bill_t_incomplete_v H");
				sql.append("\n  WHERE 1=1");
				 //GenWhereSQL
				sql.append(" "+   genWhereCondSql(conn,o));
			}
			sql.append("\n  )B ");
			sql.append("\n   ORDER BY B.zone,B.code,B.creation_date asc ");
			sql.append("\n  )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new BillPlanBean();
			   h.setSalesZone(Utils.isNull(rst.getString("zone"))+"-"+Utils.isNull(rst.getString("zone_name")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("salesrep_full_name")));
			   h.setBillTNo(Utils.isNull(rst.getString("shipment_num")));
			   h.setBillTDate(Utils.stringValue(rst.getDate("creation_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   if("detail".equalsIgnoreCase(o.getDispType())){
				   h.setItem(Utils.isNull(rst.getString("segment1")));
				   h.setItemName(Utils.isNull(rst.getString("description")));
				   h.setPlanQty(Utils.decimalFormat(rst.getDouble("quantity_shipped"), Utils.format_current_no_disgit));
			   }
			   h.setPlanQty(Utils.decimalFormat(rst.getDouble("quantity_shipped"), Utils.format_current_no_disgit));
               
			   items.add(h);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static StringBuffer genWhereCondSql(Connection conn,BillPlanBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		//fix date >= 01062019
		sql.append("\n and H.creation_date >=to_date('01062019','ddmmyyyy')");
		
		sql.append("\n and H.zone in(0,1,2,3,4)");
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and H.code = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}

		if( !Utils.isNull(o.getSalesZone()).equals("")){
		    sql.append("\n  and H.zone = "+Utils.isNull(o.getSalesZone()) );
		}
		
		return sql;
	}
}
