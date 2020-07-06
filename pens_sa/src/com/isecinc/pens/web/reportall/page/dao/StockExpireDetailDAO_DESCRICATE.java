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

public class StockExpireDetailDAO_DESCRICATE {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchStockReturnListTotalRec(Connection conn,StockBean o,User user) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM(");
			sql.append("\n select H.request_no");
			if( Utils.isNull(o.getCompareDate()).equals("compareStockDate")){
				sql.append("\n   ,(H.expire_date - H.request_date) as remain_day");
			}else{
				sql.append("\n   ,(H.expire_date - sysdate) as remain_day");
			}
			sql.append("\n  ,(select S.HALF_SHELF_LIFE_DAY from");
			sql.append("\n    PENSBI.XXPENS_BI_MST_ITEM_SHELF_LIFE S");
			sql.append("\n    where S.inventory_item_code = H.ITEM_NO");
			sql.append("\n  ) as HALF_SHELF_LIFE_DAY ");
			
			sql.append("\n  FROM ");
			/*****************************************************/
			sql.append("\n (select max(a.request_date) request_date");
			sql.append("\n  ,a.cust_account_id");
			sql.append("\n  ,a.inventory_item_id");
			sql.append("\n  from xxpens_om_check_order_v a ");
			sql.append("\n  where 1=1 ");
	        //request date
			//TypeSerch Month
			if(Utils.isNull(o.getTypeSearch()).equals("month")){
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MMM_YYYY);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and a.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and a.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}else{
				//TypeSearch Day From To
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and a.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and a.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}
			sql.append("\n  group by a.cust_account_id ,a.inventory_item_id");
			sql.append("\n  ) a");
			
			sql.append("\n ,apps.xxpens_om_check_order_v H");
			sql.append("\n ,apps.xxpens_om_item_mst_v P");
			sql.append("\n  WHERE P.inventory_item_id = H.inventory_item_id ");
			sql.append("\n  and H.cust_account_id = a.cust_account_id ");
			sql.append("\n  and H.inventory_item_id = a.inventory_item_id");
			sql.append("\n  and H.request_date = a.request_date ");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o,user));
			sql.append("\n    )AA ");
			
			/** display remainDay < HALF_SHELF_LIFE_DAY **/
			if(!Utils.isNull(o.getDispExpireSoon()).equals("")){
				sql.append("\n WHERE AA.remain_day < AA.HALF_SHELF_LIFE_DAY");
		    }  
			
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
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
			sql.append("\n  select AA.* FROM(");
			sql.append("\n    select ");
			sql.append("\n    substr(H.request_no,1,4) as sales_code");
			sql.append("\n   ,(select B.salesrep_desc from PENSBI.XXPENS_BI_MST_SALESREP B ");
			sql.append("\n     where B.salesrep_code=substr(H.request_no,1,4)) as salesrep_full_name ");
			sql.append("\n    ,H.customer_number ,H.party_name");
			sql.append("\n    ,H.request_no ,H.request_date ");
			sql.append("\n    ,P.segment1 as inventory_item_code ");
			sql.append("\n    ,H.pri_qty ,H.sec_qty ,H.sec_uom ,H.expire_date");
			
			if( Utils.isNull(o.getCompareDate()).equals("compareStockDate")){
				sql.append("\n   ,(H.expire_date - H.request_date) as remain_day");
			}else{
				sql.append("\n   ,(H.expire_date - sysdate) as remain_day");
			}
			sql.append("\n  ,(select S.HALF_SHELF_LIFE_DAY from");
			sql.append("\n    PENSBI.XXPENS_BI_MST_ITEM_SHELF_LIFE S");
			sql.append("\n    where S.inventory_item_code = H.item_no");
			sql.append("\n  ) as HALF_SHELF_LIFE_DAY ");
			
			sql.append("\n  FROM ");
			/*****************************************************/
			sql.append("\n (select max(a.request_date) request_date");
			sql.append("\n  ,a.cust_account_id");
			sql.append("\n  ,a.inventory_item_id");
			sql.append("\n  from xxpens_om_check_order_v a ");
			sql.append("\n  where 1=1 ");
	        //request date
			//TypeSerch Month
			if(Utils.isNull(o.getTypeSearch()).equals("month")){
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MMM_YYYY);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and a.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and a.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}else{
				//TypeSearch Day From To
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and a.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and a.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}
			sql.append("\n  group by a.cust_account_id ,a.inventory_item_id");
			sql.append("\n  ) a");
			
			sql.append("\n ,apps.xxpens_om_check_order_v H");
			sql.append("\n ,apps.xxpens_om_item_mst_v P");
			sql.append("\n  WHERE P.inventory_item_id = H.inventory_item_id ");
			sql.append("\n  and H.cust_account_id = a.cust_account_id ");
			sql.append("\n  and H.inventory_item_id = a.inventory_item_id");
			sql.append("\n  and H.request_date = a.request_date ");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o,user));
			sql.append("\n   )AA WHERE 1=1");
			
			/** display remainDay < HALF_SHELF_LIFE_DAY **/
			if(!Utils.isNull(o.getDispExpireSoon()).equals("")){
				sql.append("\n AND AA.remain_day < AA.HALF_SHELF_LIFE_DAY AND AA.remain_day > 0");
		    }  
			 
			sql.append("\n     ORDER BY AA.remain_day");
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
				 html.append(genHeadTable(conn,aForm,o,excel));
			  }
			   h = new StockBean();
			   h.setSalesrepCode(Utils.isNull(rst.getString("sales_code")));
			   h.setSalesrepName(Utils.isNull(rst.getString("salesrep_full_name")));
			   h.setCustomerCode(rst.getString("customer_number"));  
			   h.setCustomerName(Utils.isNull(rst.getString("party_name"))); 
			   h.setRequestNo(Utils.isNull(rst.getString("request_no"))); 
			   h.setRequestDate(DateUtil.stringValueChkNull(rst.getDate("request_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setItemCode(Utils.isNull(rst.getString("inventory_item_code"))); 
			   h.setPriQty(Utils.decimalFormat(rst.getDouble("pri_qty"), Utils.format_current_2_disgit));
			   h.setSecQty(Utils.decimalFormat(rst.getDouble("sec_qty"), Utils.format_current_2_disgit));
			   h.setSecUom(rst.getString("sec_uom"));
			   h.setExpireDate(DateUtil.stringValueChkNull(rst.getDate("expire_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setRemainDay(""+rst.getInt("remain_day"));
			   h.setHalfShelfLifeDay(""+rst.getInt("HALF_SHELF_LIFE_DAY"));
			   
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
		sql.append("\n and H.expire_date is not null");//show found expire_date
        sql.append("\n and H.request_no Like 'S%'");//Credit Sales Only
        //BrandGroup Type
        if(Utils.isNull(o.getBrandGroupType()).equals("NISSIN")){
			sql.append("\n AND P.brand in ('501','502','503','504','505','506')");
	    }else if(Utils.isNull(o.getBrandGroupType()).equals("NON_NISSIN")){
	    	sql.append("\n AND P.brand not in ('501','502','503','504','505','506')");	
	    }
	      
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
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(Connection conn,ReportAllForm aForm,StockBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append(" <style>");
			h.append(" .tr_red { \n");
			h.append("   background-color: red; \n");
			h.append("   color:black; \n");
			h.append(" } \n");
			h.append(" .tr_orange { \n");
			h.append("   background-color: orange; \n");
			h.append("   color:black; \n");
			h.append(" } \n");
			h.append(" </style>");
			
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
		h.append(" <th rowspan='2'>พนักงานขาย</th> \n");
		h.append(" <th rowspan='2'>รหัสร้านค้า</th>");
		h.append(" <th rowspan='2'>ชื่อร้านค้า</th>");
		h.append(" <th rowspan='2'>Request No</th> \n");
		h.append(" <th rowspan='2'>วันที่เช็คสต๊อก</th>");
		h.append(" <th rowspan='2'>รหัสสินค้า</th>");
		h.append(" <th colspan='3'>จำนวนแจ้งคืน</th>");
		h.append(" <th rowspan='2'>วันที่หมดอายุจริง</th>");
		h.append(" <th rowspan='2'>จำนวนวันที่เหลือ</th>");
		if( !excel){
		   h.append(" <th rowspan='2'>รายละเอียด</th>");
		}
		h.append("</tr> \n");
		h.append("<tr> \n");
		h.append(" <th>เต็ม</th>");
		h.append(" <th>เศษ</th>");
		h.append(" <th>หน่วย</th>");
		h.append("</tr> \n");
		return h;
	}
	private static  StringBuffer genRowTable(StockBean head,boolean excel,StockBean item,int no) throws Exception{
		StringBuffer h = new StringBuffer("");
		String trClass = "lineE";
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number ";
		
		//display row color
		//remian_day negative set row color:red
		if(Utils.convertToInt(item.getRemainDay()) < 0){
			trClass = "tr_red";
		}else if(Utils.convertToInt(item.getHalfShelfLifeDay()) != 0 && Utils.convertToInt(item.getRemainDay()) < Utils.convertToInt(item.getHalfShelfLifeDay())){
		  //remian_day < half_shelf_life set row color:orange
			trClass = "tr_orange";
		}
		
		h.append("<tr class='"+trClass+"'> \n");
		if(excel){
			classText = "text";
			classTextCenter = "text";
			classCurrency = "currency";
		}
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getSalesrepCode()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='10%'>"+item.getCustomerCode()+"</td> \n");
		h.append("<td class='"+classText+"' width='20%'>"+item.getCustomerName()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='10%'>"+item.getRequestNo()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getRequestDate()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getItemCode()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='5%'>"+item.getPriQty()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='5%'>"+item.getSecQty()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getSecUom()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getExpireDate()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='5%'>"+item.getRemainDay()+"</td> \n");
		if( !excel){
		/*   h.append("<td class='"+classTextCenter+"' width='10%'><a href=javascript:view('"+item.getRequestNo()+"')><font color='black'><u>รายละเอียด</u></font></a></td> \n");
*/		}
		h.append("</tr> \n");
		return h;
	}
	
	
}
