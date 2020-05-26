package com.isecinc.pens.web.reportall.page.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.SalesrepDAO;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.reportall.ReportAllForm;
import com.isecinc.pens.web.reportall.bean.StockReturn;
import com.isecinc.pens.web.reportall.bean.StockReturnLine;
import com.isecinc.pens.web.reportall.page.StockReturnAction;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockConstants;
import com.isecinc.pens.web.stock.StockForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.PageingGenerate;
import com.pens.util.UserUtils;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class StockReturnDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchStockReturnListTotalRec(Connection conn,StockBean o,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c ");
			sql.append("\n FROM apps.xxpens_om_rma_order_mst H");
			sql.append("\n ,apps.xxpens_ar_customer_all_v C");
			sql.append("\n WHERE C.account_number = H.customer_number");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o,user));
		
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
	public static StockBean searchStockReturnList(Connection conn,User user,ReportAllForm aForm,boolean allRec,int currPage,int pageSize,boolean excel ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockBean h = null;
		StringBuffer html = null;
		int r = 0,no=0;
		StockBean o = aForm.getBean().getStockBean();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select ");
			sql.append("\n  substr(H.request_no,2,1) ");
			sql.append("\n ,(select B.sales_channel_desc from PENSBI.XXPENS_BI_MST_SALES_CHANNEL B ");
			sql.append("\n   where B.sales_channel_no=substr(H.request_no,2,1)) as region_name ");
			sql.append("\n ,substr(H.request_no,1,4) as sales_code");
			sql.append("\n ,(select B.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP B ");
			sql.append("\n   where B.salesrep_code=substr(H.request_no,1,4)) as salesrep_full_name ");
			sql.append("\n ,H.customer_number ,C.party_name");
			sql.append("\n ,H.request_no ,H.request_date ");
			sql.append("\n  FROM apps.xxpens_om_rma_order_mst H");
			sql.append("\n ,apps.xxpens_ar_customer_all_v C");
			sql.append("\n WHERE C.account_number = H.customer_number");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o,user));
		
			sql.append("\n    ORDER BY H.request_date desc,H.request_no desc ");
			sql.append("\n   )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
        	sql.append("\n )M  ");
			if( !allRec){
			   sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			
			//Cal No start by Page
			no = Utils.calcStartNoInPage(currPage, StockReturnAction.pageSize);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 //(Connection conn,StockForm aForm,StockBean head,boolean excel)
				 html.append(genHeadTable(conn,aForm,o,excel));
			  }
			   h = new StockBean();
			   h.setSalesChannelName(Utils.isNull(rst.getString("region_name")));
			   h.setSalesrepCode(Utils.isNull(rst.getString("sales_code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("salesrep_full_name")));
			   h.setCustomerCode(rst.getString("customer_number"));  
			   h.setCustomerName(Utils.isNull(rst.getString("party_name"))); 
			   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
			   h.setRequestDate(DateUtil.stringValueChkNull(rst.getDate("request_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   
			   //get row table
				html.append(genRowTable(o,excel,h,no));
				no++;
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // gen end Table
			  html.append("</table>");
			}
			o.setDataStrBuffer(html);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static StringBuffer genWhereCondSql(Connection conn,StockBean o,User user) throws Exception{
		StringBuffer sql = new StringBuffer("");
        sql.append("\n and H.request_no Like 'S%'");//Credit Sales Only
        
        /** ROLE_CR_STOCK =ALL  and admin pass all**/
        if(user.getRoleCRStock().indexOf("ALL") == -1 && !"admin".equalsIgnoreCase(user.getUserName())){
	       boolean isSetCustMapSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
			if (isSetCustMapSalesTT){
				 sql.append("\n  and substr(H.request_no,1,4)  in( ");
				 sql.append("\n  select Z.salesrep_code from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT C ");
				 sql.append("\n  ,pensbi.XXPENS_BI_MST_SALES_ZONE Z ");
				 sql.append("\n  where C.zone = Z.zone");
				 sql.append("\n  and  C.user_name='"+user.getUserName()+"'");
				 sql.append("\n  )");
			}else{
				//check user login is Sales (S404)
				SalesrepBean salesrepBean = SalesrepDAO.getSalesrepBeanByCode(conn, user.getUserName().toUpperCase());
				if( salesrepBean != null && !Utils.isNull(salesrepBean.getSalesrepFullName()).equals("")){
					 sql.append("\n  and substr(H.request_no,1,4) = '"+user.getUserName().toUpperCase()+"' ");
				}else{
					// not permit
					sql.append("\n  and 1=2 /** for not permit query **/");
				}
			}
        }
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and substr(H.request_no,2,1) = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and substr(H.request_no,1,4) = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}
	
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and H.customer_number = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			sql.append("\n  and substr(H.request_no,1,4) in(");
			sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
			sql.append("\n    where zone = "+Utils.isNull(o.getSalesZone()) );
			sql.append("\n  )");
		}
		
		if( !Utils.isNull(o.getStartDate()).equalsIgnoreCase("") && !Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n and H.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
		}else if(!Utils.isNull(o.getStartDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.request_date = to_date('"+startDateStr+"','dd/mm/yyyy')");
		}else if(!Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.request_date = to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
		return sql;
	}
	public StockReturn searchStockReturnReport(Connection conn,StockBean mCriteria, User user)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StockReturn m = null;
		StringBuilder sql = new StringBuilder();
		String[] addressArr = null;
		try {
			sql.append("\n select H.request_no");
			sql.append("\n ,substr(H.request_no,1,4) as sales_code");
			sql.append("\n ,(select B.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP B ");
			sql.append("\n   where B.salesrep_code=substr(H.request_no,1,4)) as salesrep_full_name ");
			sql.append("\n ,H.customer_number ,C.party_name ,CS.address,CS.amphur,CS.province");
			sql.append("\n ,H.request_no ,H.request_date ,H.comments ,H.net_amount,H.vat_amount,H.total_amount");
			sql.append("\n ,H.reason");
			sql.append("\n FROM apps.xxpens_om_rma_order_mst H");
			sql.append("\n ,apps.xxpens_ar_cust_sales_all CS");
			sql.append("\n ,apps.xxpens_ar_customer_all_v C");
			sql.append("\n WHERE C.account_number = H.customer_number");
			sql.append("\n AND C.cust_account_id = CS.cust_account_id");
			sql.append("\n AND H.request_no ='"+ mCriteria.getRequestNo() + "'");
			sql.append("\n ORDER BY H.request_date desc  \n");

			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				m = new StockReturn();
				m.setRequestNumber(rst.getString("request_no"));
			    m.setRequestDate(DateUtil.stringValue(rst.getDate("request_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			    m.setDescription(Utils.isNull(rst.getString("comments")));
				m.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
				m.setCustomerName(Utils.isNull(rst.getString("party_name")));
				
				//split for display report
			    addressArr = Utils.isNull(rst.getString("address")).split("\\,");
			    if(addressArr.length==3){
			       m.setAddress1(addressArr[0]+addressArr[1]);
			       m.setAddress2(addressArr[2]+" "+Utils.isNull(rst.getString("amphur"))+" "+Utils.isNull(rst.getString("province")));
			    }else{
			       m.setAddress1(Utils.isNull(rst.getString("address")));
				   m.setAddress2(Utils.isNull(rst.getString("amphur"))+" "+Utils.isNull(rst.getString("province")));
			    }
			    m.setSalesCode(Utils.isNull(rst.getString("sales_code")));
			    m.setSalesName(Utils.isNull(rst.getString("salesrep_full_name")));
			    
			    m.setTotalAllNonVatAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			    m.setTotalAllVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			    m.setTotalAllAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			   
			}// while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return m;
 }
	
  public List<StockReturnLine> searchStockReturnLineReport(Connection conn,User user,StockBean mCriteria) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<StockReturnLine> lineList = new ArrayList<StockReturnLine>();
		StringBuilder sql = new StringBuilder();
		int no = 0;
		try {
			sql.append("\n select H.request_no");
			sql.append("\n ,D.line_no,D.trx_number,P.segment1,P.description");
			sql.append("\n ,D.PRI_REQ_QTY ,D.SEC_REQ_QTY ,D.SEC_REQ_UOM ,D.PRI_REQ_CONV");
			sql.append("\n ,D.discount ,D.amount ,D.reason_code ");
			sql.append("\n ,(select c.description from apps.fnd_lookup_values c ");
			sql.append("\n   where c.lookup_type ='CREDIT_MEMO_REASON' and c.attribute15 = 'Y' ");
			sql.append("\n   and c.lookup_code = D.reason_code )as reason_desc ");
			sql.append("\n FROM apps.xxpens_om_rma_order_mst H");
			sql.append("\n ,apps.xxpens_om_rma_order_dt D"); 
			sql.append("\n ,apps.xxpens_om_item_mst_v P ");
			sql.append("\n WHERE H.request_no =D.request_no");
			sql.append("\n AND D.inventory_item_id = P.inventory_item_id");
			sql.append("\n AND H.request_no ='"+ mCriteria.getRequestNo() + "'");
			sql.append("\n ORDER BY D.line_no asc \n");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  StockReturnLine m = new StockReturnLine();
			  no++;
			  m.setNo(no);
			  m.setRequestNumber(rst.getString("request_no"));
			  m.setLineId(rst.getInt("line_no"));
			  m.setProductCode(rst.getString("segment1"));
			  m.setProductName(rst.getString("description"));
			  m.setArInvoiceNo(rst.getString("trx_number"));
			  m.setUom1Qty(Utils.decimalFormat(rst.getDouble("PRI_REQ_QTY"),Utils.format_current_2_disgit));
			  m.setUom2Qty(Utils.decimalFormat(rst.getDouble("SEC_REQ_QTY"),Utils.format_current_2_disgit));
			  m.setUom2(Utils.isNull(rst.getString("SEC_REQ_UOM")));
			  m.setUom1Pac(Utils.decimalFormat(rst.getDouble("PRI_REQ_CONV"),Utils.format_number_no_disgit));
			  m.setDiscount(Utils.decimalFormat(rst.getDouble("discount"),Utils.format_current_2_disgit));
			  m.setTotalAmount(Utils.decimalFormat(rst.getDouble("amount"),Utils.format_current_2_disgit));
			  m.setReasonDesc(Utils.isNull(rst.getString("reason_desc")));
			  
			  lineList.add(m);
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
		return lineList;
	}
	  

	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(Connection conn,ReportAllForm aForm,StockBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			int colspan=7;
			h.append(ExcelHeader.EXCEL_HEADER);
			
			/*h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+"><b> รายการบิลยกเลิก ที่ Sales appl.(แบบสรุป)</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+">ประเภทขาย:Van Sales  | ภาคการขาย :"+GeneralDAO.getSalesChannelName(conn,head.getSalesChannelNo())+" </td>\n");
			h.append("</tr> \n");
			h.append("<td colspan="+colspan+">ภาคตามการดูแล:"+GeneralDAO.getSalesZoneDesc(conn,head.getSalesZone())+"   | พนักงานขาย :"+head.getSalesrepCode()+"-"+GeneralDAO.getSalesrepName(conn,head.getSalesrepCode())+" </td>\n");
			h.append("</tr> \n");
			h.append("</tr> \n");
			h.append("<td colspan="+colspan+">รหัสร้านค้า:"+head.getCustomerCode()+"-"+GeneralDAO.getCustName(conn,head.getCustomerCode())+" </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <td colspan="+colspan+"> \n");
			h.append(" วันที่พิมพ์ : &nbsp;"+DateUtil.getCurrentDateTime(DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH)+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");*/
		}else{
			//Gen Paging
		   int totalPage = aForm.getTotalPage();
		   int totalRecord = aForm.getTotalRecord();
		   int currPage =  aForm.getCurrPage();
		   int startRec = aForm.getStartRec();
		   int endRec = aForm.getEndRec();
		   int no = startRec;
			
		   
		  //pageing Generate
		   h.append(PageingGenerate.genPageing(totalPage, totalRecord, currPage, startRec, endRec, no));

		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append(" <th >No</th> \n");
		h.append(" <th >Request Number</th> \n");
		h.append(" <th >วันที่ทำรายการ</th>");
		h.append(" <th >รหัสร้านค้า</th>");
		h.append(" <th >ชื่อร้านค้า</th>");
		if( !excel){
		   h.append(" <th >พิมพ์</th>");
		}
		h.append("</tr> \n");
	
		return h;
	}
	private static  StringBuffer genRowTable(StockBean head,boolean excel,StockBean item,int no) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number ";
		h.append("<tr> \n");
		if(excel){
			classText = "text";
			classTextCenter = "text";
			classCurrency = "currency";
		}
		h.append("<td class='"+classTextCenter+"' width='5%'>"+no+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='10%'>"+item.getRequestNo()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getRequestDate()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='10%'>"+item.getCustomerCode()+"</td> \n");
		h.append("<td class='"+classText+"' width='20%'>"+item.getCustomerName()+"</td> \n");
		if( !excel){
		   h.append("<td class='"+classTextCenter+"' width='10%'><a href=javascript:printReport('"+item.getRequestNo()+"')>พิมพ์</a></td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	
}
