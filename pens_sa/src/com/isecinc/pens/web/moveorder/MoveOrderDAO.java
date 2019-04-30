package com.isecinc.pens.web.moveorder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import util.Utils;

public class MoveOrderDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchMoveOrderListTotalRec(Connection conn,MoveOrderBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from( ");
			sql.append("\n select A.move_day from ( ");
			sql.append("\n   select (case when status_date is null then trunc(sysdate)- trunc(date_required) ");
			sql.append("\n           else trunc(status_date)-trunc(date_required) end ) as move_day ");
			sql.append("\n    from xxpens_inv_morder_ctl H");
			sql.append("\n    WHERE 1=1 ");
			 //GenWhereSQL
			sql.append(" "+   genWhereCondSql(conn,o));
			sql.append("\n   )A where 1=1 ");
			//Case Check dispReasonOnly no Condition below 
			if( Utils.isNull(o.getDispHaveReason()).equals("") && !Utils.isNull(o.getDispCheckMoveDay()).equals("")){
				sql.append("\n   AND A.move_day >"+o.getMoveDay());
			}
			sql.append("\n )M ");
			
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
	public static List<MoveOrderBean> searchMoveOrderList(Connection conn,MoveOrderBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		MoveOrderBean h = null;
		List<MoveOrderBean> items = new ArrayList<MoveOrderBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select B.* FROM( ");
			sql.append("\n   select ");
			sql.append("\n   H.doc_type");
			sql.append("\n  ,H.request_number,H.date_required");
			sql.append("\n  ,H.sales_code,H.sales_name");
			sql.append("\n  ,H.pd_code,H.pd_name");
			sql.append("\n  ,H.ctn_qty,H.pcs_qty ,H.amount ,H.status_date,H.comments");
			sql.append("\n  ,H.reason,H.reason_name,H.ctn_new,H.pcs_new ");
			sql.append("\n  , (Case when status_date is null then trunc(sysdate)- trunc(date_required) ");
			sql.append("\n    else trunc(status_date)-trunc(date_required) end ) as move_day ");
			sql.append("\n  FROM xxpens_inv_morder_ctl H");
			sql.append("\n  WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+   genWhereCondSql(conn,o));
			sql.append("\n  )B WHERE 1=1");
			//Case Check dispReasonOnly no Condition below 
			if( Utils.isNull(o.getDispHaveReason()).equals("") && !Utils.isNull(o.getDispCheckMoveDay()).equals("")){
				sql.append("\n   AND B.move_day >"+o.getMoveDay());
			}
			sql.append("\n    ORDER BY B.sales_code,B.date_required ,B.request_number asc ");
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
			   h = new MoveOrderBean();
			   h.setDocType(Utils.isNull(rst.getString("doc_type")));
			   h.setRequestNumber(Utils.isNull(rst.getString("request_number")));
			   h.setRequestDate(Utils.stringValue(rst.getDate("date_required"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setSalesrepCode(Utils.isNull(rst.getString("sales_code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("sales_name")));
			   h.setPdCode(Utils.isNull(rst.getString("pd_code")));
			   h.setPdName(Utils.isNull(rst.getString("pd_name")));
			   h.setCtnQty(Utils.decimalFormat(rst.getDouble("ctn_qty"), Utils.format_current_no_disgit));
			   h.setPcsQty(Utils.decimalFormat(rst.getDouble("pcs_qty"), Utils.format_current_no_disgit));
			   h.setAmount(Utils.decimalFormat(rst.getDouble("amount"), Utils.format_current_2_disgit));
			   h.setStatusDate(Utils.stringValueChkNull(rst.getDate("status_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setRemark(Utils.isNull(rst.getString("comments")));
			   h.setReason(Utils.isNull(rst.getString("reason_name")));
			   h.setMoveDay(rst.getInt("move_day"));
			   if(rst.getDate("status_date") != null){
			      h.setRealCtnQty(Utils.decimalFormat(rst.getDouble("ctn_new"), Utils.format_current_no_disgit));
			      h.setRealPcsQty(Utils.decimalFormat(rst.getDouble("pcs_new"), Utils.format_current_no_disgit));
			   }else{
				  h.setRealCtnQty("");
				  h.setRealPcsQty("");
			   }
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
	
	public static StringBuffer genWhereCondSql(Connection conn,MoveOrderBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		
	
		if( !Utils.isNull(o.getCustCatNo()).equals("")){
			sql.append("\n and H.sales_code LIKE '"+Utils.isNull(o.getCustCatNo())+"%'");
		}
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and H.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and H.sales_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}

		if( !Utils.isNull(o.getSalesZone()).equals("")){
		sql.append("\n  and H.sales_code in(");
		sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
		sql.append("\n    where zone = "+Utils.isNull(o.getSalesZone()) );
		sql.append("\n  )");
		}
		if( !Utils.isNull(o.getStartDate()).equalsIgnoreCase("") && !Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			Date endDate = Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.date_required >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n and H.date_required <= to_date('"+endDateStr+"','dd/mm/yyyy')");
		}else if(!Utils.isNull(o.getStartDate()).equalsIgnoreCase("") ){
			Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.date_required = to_date('"+startDateStr+"','dd/mm/yyyy')");;
		}else if(!Utils.isNull(o.getEndDate()).equalsIgnoreCase("") ){
			Date endDate = Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.date_required = to_date('"+endDateStr+"','dd/mm/yyyy')");;
		}
		
		//Case Check dispReasonOnly no Condition below 
		if( !Utils.isNull(o.getDispHaveReason()).equals("")){
			sql.append("\n and H.comments is not null or H.reason is not null ");
			
		}else{
			if( !Utils.isNull(o.getDocType()).equals("") && !Utils.isNull(o.getDocType()).equals("ALL")){
				sql.append("\n and H.doc_type = '"+Utils.isNull(o.getDocType())+"'");
			}
			if( !Utils.isNull(o.getDocStatus()).equals("") && !Utils.isNull(o.getDocStatus()).equals("ALL")){
				if(Utils.isNull(o.getDocStatus()).equals("NO_RECEIVE")){
					sql.append("\n and H.status_date is  null ");
				}
	            if(Utils.isNull(o.getDocStatus()).equals("RECEIVE")){
	            	sql.append("\n and H.status_date is not null ");
				}
			}
		}
		return sql;
	}
}
