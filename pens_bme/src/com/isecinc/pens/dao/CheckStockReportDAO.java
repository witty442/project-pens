package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CheckStockReportBean;
import com.isecinc.pens.bean.StockQuery;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.dao.query.StockQuerySQL;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class CheckStockReportDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	
	public static StringBuffer searchCheckStockSummary(CheckStockReportBean o,boolean excel) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer h = null;
		Connection conn = null;
		String trClassName ="lineE";
		int r = 0;
		try {
			sql.append("\n SELECT A.* FROM (");
			sql.append("\n select substr(M.material_master,1,6) as mat_style ,M.material_master");
			sql.append("\n ,NVL(FINISHING.finishing_qty,0) as finishing_qty"); 
			sql.append("\n ,NVL(TRANSFER.transfer_qty,0) as transfer_qty");
			sql.append("\n ,NVL(ISSUED.issue_qty,0) as issue_qty");
			sql.append("\n ,NVL(ONHAND.onhand_qty,0) as onhand_qty");
			sql.append("\n ,(NVL(FINISHING.finishing_qty,0)+NVL(TRANSFER.transfer_qty,0) -NVL(ISSUED.issue_qty,0)) sum_trans_qty");
			sql.append("\n ,(( NVL(FINISHING.finishing_qty,0)+NVL(TRANSFER.transfer_qty,0) -NVL(ISSUED.issue_qty,0))");
			sql.append("\n    -  NVL(ONHAND.onhand_qty,0)) as diff_qty");
			sql.append("\n from(");
			sql.append("\n   SELECT material_master");  
			sql.append("\n   from PENSBI.PENSBME_REQ_FINISHING H");
			sql.append("\n  ,PENSBI.PENSBME_REQ_FINISHING_BARCODE D");
			sql.append("\n   WHERE H.request_no = D.request_no");
			sql.append(genWhereSQLSummary("  ","FINISHING", o));
			sql.append("\n   AND H.status ='F' AND D.status ='F'");
			sql.append("\n   UNION ");
			sql.append("\n   SELECT D.material_master "); 
			sql.append("\n   from PENSBI.PENSBME_TRANSFER_FINISHING D WHERE 1=1 ");
			sql.append(genWhereSQLSummary("  ","TRANSFER", o));
			sql.append("\n   UNION");
			sql.append("\n   SELECT D.material_master");  
			sql.append("\n   from PENSBI.PENSBME_STOCK_ISSUE H");
			sql.append("\n   ,PENSBI.PENSBME_STOCK_ISSUE_ITEM D");
			sql.append("\n    WHERE 1=1 ");
			sql.append("\n   AND H.issue_req_no = D.issue_req_no");
			sql.append("\n   AND H.status ='I' AND D.status ='I'");
			sql.append(genWhereSQLSummary("  ","ISSUED", o));
			sql.append("\n   UNION ");
			sql.append("\n   SELECT material_master "); 
			sql.append("\n   from PENSBI.PENSBME_STOCK_FINISHED D WHERE 1=1 ");
			sql.append(genWhereSQLSummary("  ","ONHAND", o));
			sql.append("\n )M");
			sql.append("\n LEFT OUTER JOIN");
			sql.append("\n (");
			sql.append("\n   SELECT D.material_master  ,count(*) as finishing_qty");
			sql.append("\n   from PENSBI.PENSBME_REQ_FINISHING H");
			sql.append("\n   ,PENSBI.PENSBME_REQ_FINISHING_BARCODE D");
			sql.append("\n   WHERE 1=1 ");
			sql.append("\n   AND H.request_no = D.request_no");
			sql.append(genWhereSQLSummary("  ","FINISHING", o));
			sql.append("\n   AND H.status ='F' AND D.status ='F'");
			sql.append("\n   group by D.material_master");
			sql.append("\n )FINISHING");
			sql.append("\n ON M.material_master= FINISHING.material_master");
			sql.append("\n LEFT OUTER JOIN");
			sql.append("\n (");
			sql.append("\n   SELECT material_master , nvl(sum(transfer_qty),0) as transfer_qty"); 
			sql.append("\n   from PENSBI.PENSBME_TRANSFER_FINISHING D WHERE 1=1");
			sql.append(genWhereSQLSummary("  ","TRANSFER", o));
			sql.append("\n   GROUP BY material_master");
			sql.append("\n )TRANSFER");
			sql.append("\n ON M.material_master= TRANSFER.material_master");
			sql.append("\n LEFT OUTER JOIN");
			sql.append("\n (");
			sql.append("\n   SELECT D.material_master  , nvl(sum(issue_qty),0) as issue_qty ");
			sql.append("\n   from PENSBI.PENSBME_STOCK_ISSUE H");
			sql.append("\n   ,PENSBI.PENSBME_STOCK_ISSUE_ITEM D");
			sql.append("\n   WHERE 1=1 ");
			sql.append("\n   AND H.issue_req_no = D.issue_req_no");
			sql.append("\n   AND H.status ='I' AND D.status ='I'");
			sql.append(genWhereSQLSummary("  ","ISSUED", o));
			sql.append("\n   GROUP BY D.material_master");
			sql.append("\n )ISSUED");
			sql.append("\n ON M.material_master= ISSUED.material_master");
			sql.append("\n LEFT OUTER JOIN ");
			sql.append("\n (");
			sql.append("\n   SELECT material_master ,(nvl(sum(onhand_qty),0)-nvl(sum(issue_qty),0)) as onhand_qty ");
			sql.append("\n   from PENSBI.PENSBME_STOCK_FINISHED D WHERE 1=1");
			sql.append(genWhereSQLSummary("  ","ONHAND", o));
			sql.append("\n   group by material_master");
			sql.append("\n )ONHAND");
			sql.append("\n ON M.material_master= ONHAND.material_master");
			sql.append("\n )A ");
			if( !Utils.isNull(o.getDispDiffQtyNoZero()).equals("")){
				sql.append("\n WHERE A.diff_qty <> 0 ");
			}
			sql.append("\n ORDER BY A.material_master");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			  r++;
			  if(r==1){
				  h = new StringBuffer();
				  h.append(genHeaderSearchCheckStockSummary(excel));
			  }
			  if(r%2==0){
				  trClassName = "lineO";
			  }
			  h.append("<tr class='"+trClassName+"'> \n");
			  h.append("<td class='num'>"+r+"</td> \n");
			  h.append("<td class='text'>"+Utils.isNull(rst.getString("mat_style"))+"</td> \n");
			  h.append("<td class='text'>"+Utils.isNull(rst.getString("material_master"))+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("finishing_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("transfer_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("issue_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("sum_trans_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("onhand_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("<td class='num'>"+Utils.decimalFormat(rst.getDouble("diff_qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("</tr>");
			   
			}//while
			if(h != null)
			h.append("</table>");
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}
	public static StringBuffer searchCheckStockDetail(CheckStockReportBean o,boolean excel) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer h = null;
		Connection conn = null;
		String trClassName ="lineE";
		int r = 0;
		try {
			sql.append("\n select substr(A.material_master,1,6) as mat_style ,A.material_master");
			sql.append("\n ,A.transaction_no,A.transaction_date ,A.qty");
			sql.append("\n FROM(");
			sql.append("\n   SELECT D.material_master,H.request_no as transaction_no,H.confirm_date as transaction_date,count(*) as qty");
			sql.append("\n   from PENSBI.PENSBME_REQ_FINISHING H");
			sql.append("\n   ,PENSBI.PENSBME_REQ_FINISHING_BARCODE D");
			sql.append("\n   WHERE 1=1 ");
			sql.append("\n   AND H.request_no = D.request_no");
			sql.append(genWhereSQLDetail("  ","FINISHING", o));
			sql.append("\n   AND H.status ='F' AND D.status ='F'");
			sql.append("\n   group by D.material_master,H.request_no,H.confirm_date");
			
			sql.append("\n   UNION ALL ");
			
			sql.append("\n   SELECT material_master,D.transfer_no as transaction_no,D.transfer_date as transaction_date,nvl(sum(transfer_qty),0) as qty"); 
			sql.append("\n   from PENSBI.PENSBME_TRANSFER_FINISHING D WHERE 1=1");
			sql.append(genWhereSQLDetail("  ","TRANSFER", o));
			sql.append("\n   GROUP BY material_master,D.transfer_no,D.transfer_date");
			
			sql.append("\n   UNION ALL ");
			
			sql.append("\n   SELECT D.material_master,H.issue_req_no as transaction_no,H.status_date as transaction_date,nvl(sum(issue_qty),0) as qty ");
			sql.append("\n   from PENSBI.PENSBME_STOCK_ISSUE H");
			sql.append("\n   ,PENSBI.PENSBME_STOCK_ISSUE_ITEM D");
			sql.append("\n   WHERE 1=1 ");
			sql.append("\n   AND H.issue_req_no = D.issue_req_no");
			sql.append("\n   AND H.status ='I' AND D.status ='I'");
			sql.append(genWhereSQLDetail("  ","ISSUED", o));
			sql.append("\n   GROUP BY D.material_master,H.issue_req_no,H.status_date");
			
			sql.append("\n )A ");
			sql.append("\n ORDER BY A.material_master,A.transaction_date");
			
			logger.debug("sql:"+sql);
		
			conn = DBConnection.getInstance().getConnectionApps();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			  r++;
			  if(r==1){
				  h = new StringBuffer();
				  h.append(genHeaderSearchCheckStockDetail(excel));
			  }
			  if(r%2==0){
				  trClassName = "lineO";
			  }
			  h.append("<tr class='"+trClassName+"'> \n");
			  /*h.append("<td class='num' width='5%'>"+r+"</td> \n");*/
			  h.append("<td class='text' width='20%'>"+Utils.isNull(rst.getString("mat_style"))+"</td> \n");
			  h.append("<td class='text' width='20%'>"+Utils.isNull(rst.getString("material_master"))+"</td> \n");
			  h.append("<td class='text' width='20%'>"+Utils.stringValue(rst.getDate("transaction_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th)+"</td> \n");
			  h.append("<td class='text' width='20%'>"+Utils.isNull(rst.getString("transaction_no"))+"</td> \n");
			  h.append("<td class='num' width='20%'>"+Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit)+"</td> \n");
			  h.append("</tr>");
			}//while
			if(h != null){
			  h.append("</table>");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}
	public static StringBuffer genWhereSQLSummary(String space,String grouType,CheckStockReportBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		sql.append("\n"+space+"AND warehouse ='"+o.getWarehouse()+"'");
		if( !Utils.isNull(o.getStyle()).equals("")){
			   sql.append("\n"+space+"AND substr(D.material_master,1,6) ='"+Utils.isNull(o.getStyle())+"'");
			}
		if( !Utils.isNull(o.getMaterialMaster()).equals("")){
		   sql.append("\n"+space+"AND D.material_master ='"+Utils.isNull(o.getMaterialMaster())+"'");
		}
		if( !Utils.isNull(o.getInitDate()).equals("")){
			Date initDate = Utils.parse(o.getInitDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String initDateC = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH);
			if("FINISHING".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND H.confirm_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}else if("TRANSFER".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND transfer_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}else if("ISSUED".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND H.status_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}
		}
		return sql;
	}
	public static StringBuffer genWhereSQLDetail(String space,String grouType,CheckStockReportBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		sql.append("\n"+space+"AND warehouse ='"+o.getWarehouse()+"'");
		if( !Utils.isNull(o.getStyle()).equals("")){
			   sql.append("\n"+space+"AND substr(D.material_master,1,6) ='"+Utils.isNull(o.getStyle())+"'");
			}
		if( !Utils.isNull(o.getMaterialMaster()).equals("")){
		   sql.append("\n"+space+"AND D.material_master ='"+Utils.isNull(o.getMaterialMaster())+"'");
		}
		if( !Utils.isNull(o.getInitDate()).equals("")){
			Date initDate = Utils.parse(o.getInitDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String initDateC = Utils.stringValue(initDate, Utils.DD_MM_YYYY_WITH_SLASH);
			if("FINISHING".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND H.confirm_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}else if("TRANSFER".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND transfer_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}else if("ISSUED".equalsIgnoreCase(grouType)){
				 sql.append("\n"+space+"AND H.status_date >= to_date('"+initDateC+"','dd/mm/yyyy')");
			}
		}
		return sql;
	}
	
	public static StringBuffer genHeaderSearchCheckStockSummary(boolean excel){
		StringBuffer h = new StringBuffer("");
		if(excel){
		   h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("  <td align='left' colspan='9'><b>รายงานตรวจสต๊อก W2 (Summary)</b></td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		   h.append("<table border='1'> \n");
		}else{
			h.append("<table id='tblProduct' align='center' border='0' cellpadding='3' cellspacing='1' class='tableSearch' width='100%'>");
		}
		  h.append("<tr> \n");
		  h.append("<th>No</th> \n");
		  h.append("<th>รุ่น</th> \n");
		  h.append("<th>สีไซร์</th> \n");
		  h.append("<th>Finishing Qty</th> \n");
		  h.append("<th>Transfer Qty</th> \n");
		  h.append("<th>Issue Qty</th> \n");
		  h.append("<th>Sum Qty by Transaction</th> \n");
		  h.append("<th>ยอด Onhand</th> \n");
		  h.append("<th>DIFF</th> \n");
		  h.append("</tr> \n");
		return h;
	}
	
	public static StringBuffer genHeaderSearchCheckStockDetail(boolean excel){
		StringBuffer h = new StringBuffer("");
		int colSpan=5;
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append("  <td align='left' colspan='"+colSpan+"'><b>รายงานตรวจสต๊อก W2 (Detail)</b></td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
			
			h.append("<table border='1'> \n");
		}else{
			h.append("<table id='tblProduct' align='center' border='0' cellpadding='3' cellspacing='1' class='tableSearch' width='100%'>");
		}
		  h.append("<tr> \n");
		  h.append(" <th>รุ่น</th> \n");
		  h.append(" <th>สีไซร์</th> \n");
		  h.append(" <th>Transaction Date</th> \n");
		  h.append(" <th>Transaction No</th> \n");
		  h.append(" <th>Qty</th> \n");
		  h.append("</tr> \n");
		return h;
	}

}
