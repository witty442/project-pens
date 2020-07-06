package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.CustomerBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.CustomerDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.web.location.LocationBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.UserUtils;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class StockCallCardCreditReport_BK {
	protected static Logger logger = Logger.getLogger("PENS");

	public static StockBean searchReport(String contextPath ,StockBean o,User user,boolean excel){
	    logger.debug("excel:"+excel);
	    return searchReportModel(contextPath,o,user,excel);
	}
	
	public static StockBean searchReportModel(String contextPath,StockBean o,User user,boolean excel){
		StockBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		String requestDate = "";
		Map<String, String> productMap = new HashMap<String, String>();
		Map<String, String> requestMAP = new HashMap<String, String>();
		Map<String, StockBean> dataMAP = new HashMap<String, StockBean>();
		String keyDataMap = "";
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//check user login is map cust sales TT
			boolean isUserMapCustSalesTT = GeneralDAO.isUserMapCustSalesTT(user);

			//prepare parameter Fix CConstant 
			logger.debug("startDate:"+o.getStartDate());
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			logger.debug("startDateStr:"+startDateStr);
			
			sql.append("\n SELECT M.* FROM (");
			sql.append("\n  SELECT request_date,item_no,item_name ,'S' as r_type ");
			sql.append("\n  ,sum(pri_qty) as qty ");
			sql.append("\n  FROM xxpens_om_check_order_v M, PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  WHERE M.SALESREP_ID = Z.SALESREP_ID ");
			sql.append("\n  AND M.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n  AND Z.zone in('0','1','2','3','4') ");
			
			//debug 
			//sql.append("\n  AND M.item_no = '501597'");
			
			//SalesZone By User Login
			if(isUserMapCustSalesTT){
				sql.append("\n and Z.zone in( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT where user_name ='"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
				// Brand 504 must show 503494,503544,503681 (Case Special case )
				if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
					sql.append("\n and ( M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
					sql.append("\n     or M.item_no in('503494','503544','503681') )");
				}else{
					sql.append("\n and M.brand in("+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
				sql.append("\n and M.customer_number in("+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
				sql.append("\n and M.item_no in("+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
			}
			
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
			    sql.append("\n and Z.salesrep_code = '"+user.getUserName().toUpperCase()+"'");
			}
			
			sql.append("\n GROUP BY request_date,item_no,item_name" );
			sql.append("\n ) M WHERE M.qty <> 0 ");
			sql.append("\n UNION ALL ");
			
			//Sales Analyst
			sql.append("\n SELECT ");
			sql.append("\n M.INVOICE_DATE as request_date , P.inventory_item_code as item_no ");
			sql.append("\n , P.inventory_item_desc as item_name ,'A' as r_type");
			sql.append("\n , SUM((INVOICED_QTY+Promotion_QTY)) as qty");
			sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V M ,");
			sql.append("\n PENSBI.XXPENS_BI_MST_ITEM P ,");
			sql.append("\n PENSBI.XXPENS_BI_MST_SALESREP S,");
			sql.append("\n PENSBI.XXPENS_BI_MST_CUSTOMER C ,");
			sql.append("\n PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n AND M.inventory_item_id = P.inventory_item_id");
			sql.append("\n AND M.customer_id = C.customer_id");
			sql.append("\n AND M.salesrep_id = S.salesrep_id");
			sql.append("\n AND M.salesrep_id = Z.salesrep_id");
			sql.append("\n AND M.INVOICE_DATE >= to_date('"+startDateStr+"','dd/mm/yyyy')");//start initdate check
			
			//debug 
			//sql.append("\n AND M.inventory_item_id  = 665170");
			
			sql.append("\n AND Z.zone in('0','1','2','3','4') ");
			//SalesZone By User Login
			if(isUserMapCustSalesTT){
				sql.append("\n and Z.zone in( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT where user_name ='"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			
			if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
				// Brand 504 must show 503494,503544,503681 (Case Special case )
				if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
					sql.append("\n and ( M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
					sql.append("\n     or P.inventory_item_code in('503494','503544','503681') )");
				}else{
					sql.append("\n and M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
				sql.append("\n and C.customer_code in("+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
				sql.append("\n and P.inventory_item_code in( "+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
			}
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
			    sql.append("\n and Z.salesrep_code = '"+user.getUserName().toUpperCase()+"'");
			}
			
			sql.append("\n GROUP BY M.INVOICE_DATE ,P.inventory_item_code,P.inventory_item_desc");

			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString());
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockBean();
			
			  //set bean
			  item.setRequestDate(requestDate);
			  item.setItemCode(Utils.isNull(rst.getString("item_no")));
			  item.setItemName(Utils.isNull(rst.getString("item_name")));
			  item.setOrderQty(Utils.decimalFormat(rst.getDouble("qty"), Utils.format_current_2_disgit));
			  
			  //set for display
			  productMap.put(Utils.isNull(rst.getString("item_no")), Utils.isNull(rst.getString("item_name")));
			  requestDate = DateUtil.stringValue(rst.getDate("request_date"), DateUtil.YYYY_MM_DD_WITHOUT_SLASH,DateUtil.local_th);
			  requestMAP.put(requestDate, requestDate);
			  
			  //set data Map  key= recordType_reuest_date_product
			  keyDataMap = Utils.isNull(rst.getString("r_type"))+"_"+requestDate+"_"+Utils.isNull(rst.getString("item_no"));
			  
			  dataMAP.put(keyDataMap, item);
			}//while
			
			/*** Sort column and Row Map  ***/
			//Sort productCode asc to List
			List<StockBean> productList = new ArrayList<StockBean>();
			Map<String, String> productMapSortMap = new TreeMap<String, String>(productMap);
			Iterator its = productMapSortMap.keySet().iterator();
			StockBean b =null;
			while(its.hasNext()){
				String key = (String)its.next();
				String value = productMapSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new StockBean();
				b.setItemCode(key);
				b.setItemName(value);
				productList.add(b);
			}
			
			//Sort RequestDate asc To List
			List<StockBean> requestDateList = new ArrayList<StockBean>();
			Map<String, String> requestDateSortMap = new TreeMap<String, String>(requestMAP);
			its = requestDateSortMap.keySet().iterator();
			
			while(its.hasNext()){
				String key = (String)its.next();
				String value = requestDateSortMap.get(key);
				//logger.debug("no["+r+"]key["+key+"]value["+value+"]");
				b = new StockBean();
				b.setRequestDate(value);
				requestDateList.add(b);
			}
			
			//Gen Html Table Show
			if(dataMAP != null && !dataMAP.isEmpty()){
			   html = new StringBuffer("");
			   //check sumColumn qty=0 and hide ,sumRow=0 and hide
			   Map<String, String> sumRequestDateToMap = sumRequestDateToMap(requestDateList, dataMAP);
					   
			   //Gen Header Table
			   html.append(genHeadTable(contextPath, o, excel, productList,requestDateList,sumRequestDateToMap));
			   
			   //Gen Row Table
			   html.append(genRowTable(contextPath, o, excel, dataMAP, productList, requestDateList,sumRequestDateToMap));
			  
			   html.append("</table>");
			   html.append("</div>");
			}
			
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	
	public static List<StockBean> getDetailList(String recType,StockBean o,User user){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<StockBean> dataList = new ArrayList<StockBean>();
		StringBuffer sql = new StringBuffer();
		try{
			Date requestDate = DateUtil.parse(o.getRequestDate(), DateUtil.YYYY_MM_DD_WITHOUT_SLASH,DateUtil.local_th);
			String requestDateStr = DateUtil.stringValue(requestDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			conn = DBConnection.getInstance().getConnectionApps();
			//S
			if(recType.equalsIgnoreCase("S")){
				sql.append("\n SELECT M.expire_date,NVL(sum(M.pri_qty),0) as pri_qty,nvl(sum(M.sec_qty),0) as sec_qty ");
				sql.append("\n FROM ");
				/*****************************************************/
				sql.append("\n (select max(a.request_date) request_date");
				sql.append("\n  ,a.cust_account_id");
				sql.append("\n  ,a.inventory_item_id");
				sql.append("\n  from apps.xxpens_om_check_order_v a ");
				sql.append("\n  where 1=1 ");
		        //request date
				sql.append("\n  and a.request_date = to_date('"+requestDateStr+"','dd/mm/yyyy')");
				sql.append("\n  group by a.cust_account_id ,a.inventory_item_id");
				sql.append("\n ) a");
				/******************************************************/
				sql.append("\n  , apps.xxpens_om_check_order_v M , PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
				sql.append("\n  WHERE M.SALESREP_ID = Z.SALESREP_ID ");
				sql.append("\n  and M.cust_account_id = a.cust_account_id ");
				sql.append("\n  and M.inventory_item_id = a.inventory_item_id");
				sql.append("\n  and M.request_date = a.request_date ");
			   
				if( !Utils.isNull(o.getSalesrepCode()).equals("")){
					sql.append("\n and M.sales_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
				}
				if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
					// Brand 504 must show 503494,503544,503681 (Case Special case )
					if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
						sql.append("\n and ( M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
						sql.append("\n     or M.item_no in('503494','503544','503681') )");
					}else{
						sql.append("\n and M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
					}
				}
				if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
					sql.append("\n and M.customer_number in( "+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")");
				}
				if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
					sql.append("\n and M.item_no in( "+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
				}
				
				sql.append("\n GROUP BY M.expire_date");
			}else{
				//A SalesAnalysis
				sql.append("\n SELECT ");
				sql.append("\n M.INVOICE_DATE,M.invoice_no ");
				sql.append("\n , SUM((INVOICED_QTY+Promotion_QTY)) as qty");
				sql.append("\n FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V M ,");
				sql.append("\n PENSBI.XXPENS_BI_MST_ITEM P ,");
				sql.append("\n PENSBI.XXPENS_BI_MST_SALESREP S,");
				sql.append("\n PENSBI.XXPENS_BI_MST_CUSTOMER C ,");
				sql.append("\n PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
				sql.append("\n WHERE 1=1 ");
				sql.append("\n AND M.inventory_item_id = P.inventory_item_id");
				sql.append("\n AND M.customer_id = C.customer_id");
				sql.append("\n AND M.salesrep_id = S.salesrep_id");
				sql.append("\n AND M.salesrep_id = Z.salesrep_id");
				sql.append("\n AND M.INVOICE_DATE = to_date('"+requestDateStr+"','dd/mm/yyyy')");
				
				//debug 
				//sql.append("\n AND M.inventory_item_id  = 665170");
				sql.append("\n AND Z.zone in('0','1','2','3','4') ");
				if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
					// Brand 504 must show 503494,503544,503681 (Case Special case )
					if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
						sql.append("\n and ( M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
						sql.append("\n     or P.inventory_item_code in('503494','503544','503681') )");
					}else{
						sql.append("\n and M.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")");
					}
				}
				if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
					sql.append("\n and C.customer_code in("+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")");
				}
				if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
					sql.append("\n and P.inventory_item_code in( "+SQLHelper.converToTextSqlIn(o.getItemCode())+")");
				}
				sql.append("\n GROUP BY M.INVOICE_DATE ,M.invoice_no");
			}
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				StockBean item = new StockBean();
				if(recType.equalsIgnoreCase("S")){
				    item.setExpireDate(DateUtil.stringValueChkNull(rs.getDate("expire_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				    item.setPriQty(Utils.decimalFormat(rs.getDouble("PRI_QTY"), Utils.format_current_no_disgit)); 
				    item.setSecQty(Utils.decimalFormat(rs.getDouble("SEC_QTY"), Utils.format_current_no_disgit)); 
				}else{
					item.setInvoiceNo(rs.getString("invoice_no"));
					item.setInvoiceDate(DateUtil.stringValueChkNull(rs.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
					item.setPriQty(Utils.decimalFormat(rs.getDouble("QTY"), Utils.format_current_no_disgit)); 
				}
				dataList.add(item);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
				if(rs != null){
					rs.close();rs=null;
				}
				if(ps != null){
					ps.close();ps=null;
				}
			}catch(Exception ee){}
		}
		return dataList;
	}
	
	//set data Map  key=  recordType+_"+reuest_date+_+product
	private static Map<String, String> sumRequestDateToMap(List<StockBean> requestDateList, Map<String, StockBean> dataMAP) throws Exception{
		Map<String, String> sumRequestDateMap = new HashMap<String, String>();
		String requestDate = "";
		double sumRequestDateAll = 0;
		
		//sum request_date Type S(Stock)
		for(int i=0;i<requestDateList.size();i++){
			requestDate = requestDateList.get(i).getRequestDate();
			Iterator itsAll = dataMAP.keySet().iterator();
			while(itsAll.hasNext()){
				String key = (String)itsAll.next();
				StockBean valueBean = dataMAP.get(key);
				if(key.startsWith("S_"+requestDate)){
				   logger.debug("sum key["+key+"]value["+valueBean.getOrderQty()+"]");
				   sumRequestDateAll += Utils.convertStrToDouble(valueBean.getOrderQty());
				}
				
			}//while
			logger.debug("sum requestDateS["+requestDate+"]["+sumRequestDateAll+"]");
			
			sumRequestDateMap.put(requestDate+"_S", sumRequestDateAll+"");
			sumRequestDateAll = 0;//reset 
		} //for 
		
		//sum request_date Type A(SalesAnaylyst)
		sumRequestDateAll = 0;
		for(int i=0;i<requestDateList.size();i++){
			requestDate = requestDateList.get(i).getRequestDate();
			Iterator itsAll = dataMAP.keySet().iterator();
			while(itsAll.hasNext()){
				String key = (String)itsAll.next();
				StockBean valueBean = dataMAP.get(key);
				if(key.startsWith("A_"+requestDate)){
				   logger.debug("sum key["+key+"]value["+valueBean.getOrderQty()+"]");
				   sumRequestDateAll += Utils.convertStrToDouble(valueBean.getOrderQty());
				}
				
			}//while
			logger.debug("sum requestDateA["+requestDate+"]["+sumRequestDateAll+"]");
			
			sumRequestDateMap.put(requestDate+"_A", sumRequestDateAll+"");
			sumRequestDateAll = 0;//reset 
		} //for 
		return sumRequestDateMap;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(String contextPath,StockBean head,boolean excel
			,List<StockBean> productList
			,List<StockBean> requestDateList
			,Map<String, String> sumRequestDateToMap) throws Exception{
		StringBuffer hAll = new StringBuffer("");
		StringBuffer h = new StringBuffer("");
		StringBuffer headReport = new StringBuffer("");
		int colSpan = 0;
		
		//find CustomerName
		CustomerBean criBean = new CustomerBean();
		criBean.setCustomerCode(head.getCustomerCode());
		CustomerBean reBean = CustomerDAO.findCustomer(criBean);
		if(reBean != null){
			head.setCustomerName(reBean.getCustomerName());
		}
		//Calc width
		String width="60%";
		int tdWidth = 5;
		if(head.getReportType().equalsIgnoreCase("2")){
			if( (requestDateList.size()*2) >=4 ){
				width="100%";
				//calc td width
				if( !head.getDispOrderOnly().equals("")){
				   tdWidth = Math.round(90/(requestDateList.size()));
				}else{
				   tdWidth = Math.round(90/(requestDateList.size()*2));
				}
			}else{
				//calc td width
				if( !head.getDispOrderOnly().equals("")){
				   tdWidth = Math.round(60/(requestDateList.size()));
				}else{
				   tdWidth = Math.round(60/(requestDateList.size()*2));
				}
			}//if
		}else{
			if(productList.size() >= 4){
				width="100%";
				//calc td width
				if( !head.getDispOrderOnly().equals("")){
				   tdWidth = Math.round(90/productList.size());
				}else{
				   tdWidth = Math.round(90/productList.size());
				}
			}else{
				//calc td width
				if( !head.getDispOrderOnly().equals("")){
				   tdWidth = Math.round(60/productList.size());
				}else{
				   tdWidth = Math.round(60/productList.size());	
				}
			}//if 2
		}//if 1
		
		//h.append("<div style='height:300px;width:600px;' \n>");//NEW CODE
		//h.append("<table id='tblProduct' class='table table-condensed table-striped'> \n");
		
		h.append("<div id ='scroll' align='center' \n>");
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoSet'> \n");

		h.append("<thead> \n");
		h.append("<tr> \n");
		h.append(" <td width='10%' class='td_bg_lineH'>");
		if(head.getReportType().equalsIgnoreCase("1")){
			h.append("วันที่  / SKU");
		}else{
			h.append("SKU / วันที่");
		}
		h.append("</td> \n");
		colSpan++;
			
		if(head.getReportType().equalsIgnoreCase("2")){
			for(int i=0;i<requestDateList.size();i++){
				
				StockBean item = requestDateList.get(i);
				//convert date to normal
				String requestDateTemp = item.getRequestDate().substring(6,8)+"/"+item.getRequestDate().substring(4,6)+"/"+item.getRequestDate().substring(0,4); 
				
				if( head.getDispOrderOnly().equals("")){ //No check display order only
					//1.Type S
					if( Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_S")) != 0){
						colSpan++;
					   h.append("<td width='"+tdWidth+"%' class='td_bg_lineS'>"+requestDateTemp+"</td> \n");
					}
				}//if
				
				//2.Type A
				if(Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_A")) != 0){
					colSpan++;
				   h.append("<td width='"+tdWidth+"%' class='td_bg_lineA'>"+requestDateTemp+"</td> \n");
				}
			}
		}else{
			String productName = "";
			for(int i=0;i<productList.size();i++){
				colSpan++;
				StockBean item = productList.get(i);
				productName = item.getItemName();
				if( !excel){
					if(productName.length()>=25){
			           productName = productName.substring(0,25)+"<br/>"+productName.substring(25,productName.length());
					}
				}
				h.append("<td width='"+tdWidth+"%' class='td_bg_lineH'>"+item.getItemCode()+"<br/> "+productName+"</td> \n");
			}
		}
		h.append("</tr> \n");
		h.append("</thead> \n");
		logger.debug("colSpan:"+colSpan);
		if(excel){
			String a= "@";
			headReport.append(ExcelHeader.EXCEL_HEADER);
			headReport.append("<style> \n");
			headReport.append(" .td_bg_lineH { \n");
			headReport.append("   mso-number-format:'"+a+"'; \n");
			headReport.append(" } \n");
			
			headReport.append(" .td_bg_lineS { \n");
			headReport.append("   background-color: #AED6F1; \n");
			headReport.append("   mso-number-format:'"+a+"'; \n");
			headReport.append("   color:red; \n");
			headReport.append("   text-align: center; \n");
			headReport.append(" } \n");
			
			headReport.append(" .td_bg_lineS_num { \n");
			headReport.append("   background-color: #AED6F1; \n");
			headReport.append("   mso-number-format:\\#\\,\\#\\#0; \n");
			headReport.append("   color:red; \n");
			headReport.append(" } \n");
			
			headReport.append(" .td_bg_lineA { \n");
			headReport.append("   background-color: #F2D7D5; \n");
			headReport.append("   mso-number-format:'"+a+"'; \n");
			headReport.append("   text-align: center; \n");
			headReport.append(" } \n");
			
			headReport.append(" .td_bg_lineA_num { \n");
			headReport.append("   background-color: #F2D7D5; \n");
			headReport.append("   mso-number-format:\\#\\,\\#\\#0; \n");
			headReport.append("   mso-number-format:'"+a+"'; \n");
			headReport.append(" } \n");
			headReport.append("</style> \n");
			
			headReport.append("<table id='tblProduct' align='center' border='1' cellpadding='1' cellspacing='1' > \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b>Call Card ร้านค้าเครดิต</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b>ร้านค้า : "+head.getCustomerCode()+" - "+head.getCustomerName()+"</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b>แบรนด์ : "+head.getBrand()+"</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("<tr> \n");
			headReport.append(" <td colspan="+colSpan+"><b>พิมพ์วันที่ &nbsp;&nbsp;"+DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,DateUtil.local_th)+"</b> </td>");
			headReport.append("</tr> \n");
			headReport.append("</table> \n");
			
			hAll.append(headReport);
			hAll.append(h);
		}else{
		   hAll.append(h);
		}
		return hAll;
	}
	
	private static StringBuffer genRowTable(String contextPath,StockBean head,boolean excel
			,Map<String, StockBean> dataMAP
			,List<StockBean> productList
			,List<StockBean> requestDateList
			,Map<String, String> sumRequestDateToMap) throws Exception{
		StringBuffer h = new StringBuffer("");
		String key = "";
		StockBean itemS = null;
		StockBean item = null;
		StockBean itemResult = null;
		String requestDateTemp = "";
		String linkPre = "",linkPost="";
		h.append("<tbody> \n");
		if(head.getReportType().equalsIgnoreCase("2")){ //แสดง SKU แนวนอน
		  for(int i=0;i<productList.size();i++){
			item = productList.get(i);
			
			h.append("<tr> \n");
			h.append(" <td class=''>"+item.getItemCode()+"-"+item.getItemName()+"</td> \n");
			for(int n=0;n<requestDateList.size();n++){
				itemS = requestDateList.get(n);
				//1 Record Type =S Stock
				if( head.getDispOrderOnly().equals("") && Utils.convertStrToDouble(sumRequestDateToMap.get(itemS.getRequestDate()+"_S")) != 0){
					key=  "S_"+itemS.getRequestDate()+"_"+item.getItemCode();
					itemResult = dataMAP.get(key);
					
					//set linkDetail
					//linkPre = !excel?"<a href=javascript:openDetail('S','"+itemS.getRequestDate()+"','"+item.getItemCode()+"');>":"";
					//linkPost = !excel?"</a>":"";
					
					if(itemResult != null){
					   h.append(" <td class='td_bg_lineS_num'>"+linkPre+Utils.isNull(itemResult.getOrderQty())+linkPost+"</td> \n");
					}else{
					   h.append(" <td class='td_bg_lineS_num'>"+linkPre+"0"+linkPost+"</td> \n");
					}
				}//if
				
				//2 Record Type =A SalesAnalyst
				if( Utils.convertStrToDouble(sumRequestDateToMap.get(itemS.getRequestDate()+"_A")) != 0){
					key=  "A_"+itemS.getRequestDate()+"_"+item.getItemCode();
					itemResult = dataMAP.get(key);
					
					//set linkDetail
					//linkPre = !excel?"<a href=javascript:openDetail('A','"+itemS.getRequestDate()+"','"+item.getItemCode()+"');>":"";
					//linkPost = !excel?"</a>":"";
					
					if(itemResult != null){
					   h.append(" <td class='td_bg_lineA_num'>"+linkPre+Utils.isNull(itemResult.getOrderQty())+linkPost+"</td> \n");
					}else{
					   h.append(" <td class='td_bg_lineA_num'>"+linkPre+"0"+linkPost+"</td> \n");
					}
				}//if
			}//for 2
			h.append("</tr> \n");
			
		  }//for 1
		  
		  
		 //////////////////////////////////////////////////
		  
		}else if(head.getReportType().equalsIgnoreCase("1")){ //แสดง SKU แนวตั้ง
			  for(int i=0;i<requestDateList.size();i++){
				item = requestDateList.get(i);
				if( head.getDispOrderOnly().equals("") && Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_S")) != 0){
					//1 Record Type =S Stock
					h.append("<tr> \n");
					//convert to normal date
					requestDateTemp = item.getRequestDate().substring(6,8)+"/"+item.getRequestDate().substring(4,6)+"/"+item.getRequestDate().substring(0,4); 
					h.append(" <td class='td_bg_lineS'>"+requestDateTemp+"</td> \n");
					for(int n=0;n<productList.size();n++){
						itemS = productList.get(n);
						//1.Column Type S Stock
						key=  "S_"+item.getRequestDate()+"_"+itemS.getItemCode();
						itemResult = dataMAP.get(key);
						
						//set linkDetail
						//linkPre = !excel?"<a href=javascript:openDetail('S','"+item.getRequestDate()+"','"+itemS.getItemCode()+"');>":"";
						//linkPost = !excel?"</a>":"";
						
						if(itemResult != null){
						   h.append(" <td  class='td_bg_lineS_num'>"+linkPre+Utils.isNull(itemResult.getOrderQty())+linkPost+"</td> \n");
						}else{
						   h.append(" <td  class='td_bg_lineS_num'>"+linkPre+"0"+linkPost+"</td> \n");
						}
					}//for 2
					h.append("</tr> \n");
				}//if check Row all is <> 0
				
				//2 Record Type =A SalesAnalyst
				if(Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_A")) != 0){
					h.append("<tr> \n");
					//convert to normal date
					requestDateTemp = item.getRequestDate().substring(6,8)+"/"+item.getRequestDate().substring(4,6)+"/"+item.getRequestDate().substring(0,4); 
					h.append(" <td class='td_bg_lineA'>"+requestDateTemp+"</td> \n");
					for(int n=0;n<productList.size();n++){
						itemS = productList.get(n);
						//2.Column Type A SalesAnalyst
						key=  "A_"+item.getRequestDate()+"_"+itemS.getItemCode();
						itemResult = dataMAP.get(key);
						
						//set linkDetail
						//linkPre = !excel?"<a href=javascript:openDetail('A','"+item.getRequestDate()+"','"+itemS.getItemCode()+"');>":"";
						//linkPost = !excel?"</a>":"";
						
						if(itemResult != null){
						   h.append(" <td  class='td_bg_lineA_num'>"+linkPre+Utils.isNull(itemResult.getOrderQty())+linkPost+"</td> \n");
						}else{
						   h.append(" <td  class='td_bg_lineA_num'>"+linkPre+"0"+linkPost+"</td> \n");
						}
					}//for 2
					h.append("</tr> \n");
				}//if check Row all is <> 0
			  }//for 1
			}//if
		h.append("</tbody> \n");
		return h;
	}
	
	
}
