package com.isecinc.pens.web.stock;

import java.sql.Connection;
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
import com.isecinc.pens.report.salesanalyst.helper.FileUtil;
import com.isecinc.pens.web.location.LocationBean;

import util.DBConnection;
import util.DateToolsUtil;
import util.ExcelHeader;
import util.UserUtils;
import util.Utils;

public class StockCallCardCreditReport {
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
			boolean isUserMapCustSalesTT = StockCallCardCreditReport.isUserMapCustSalesTT(user);

			//prepare parameter Fix CConstant 
			logger.debug("startDate:"+o.getStartDate());
			Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
			logger.debug("startDateStr:"+startDateStr);
			
			sql.append("\n SELECT M.* FROM (");
			sql.append("\n  SELECT request_date,item_no,item_name ,'S' as r_type ");
			sql.append("\n  ,sum(pri_qty) as qty ");
			sql.append("\n  FROM xxpens_om_check_order_v M, PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  WHERE M.SALESREP_ID = Z.SALESREP_ID ");
			sql.append("\n  AND M.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n  AND Z.zone in('0','1','2','3','4') ");
			
			//SalesZone By User Login
			if(isUserMapCustSalesTT){
				sql.append("\n and Z.zone in( ");
				sql.append("\n   select zone from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT where user_name ='"+user.getUserName()+"'");
				sql.append("\n ) ");
			}
			if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
				// Brand 504 must show 503494,503544,503681 (Case Special case )
				if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
					sql.append("\n and ( M.brand in( "+Utils.converToTextSqlIn(o.getBrand())+")");
					sql.append("\n     or M.item_no in('503494','503544','503681') )");
				}else{
					sql.append("\n and M.brand in("+Utils.converToTextSqlIn(o.getBrand())+")");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
				sql.append("\n and M.customer_number in("+Utils.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
				sql.append("\n and M.item_no in("+Utils.converToTextSqlIn(o.getItemCode())+")");
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
			sql.append("\n , SUM((INVOICED_QTY+Promotion_QTY)-Returned_QTY) as qty");
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
					sql.append("\n and ( M.brand in( "+Utils.converToTextSqlIn(o.getBrand())+")");
					sql.append("\n     or P.inventory_item_code in('503494','503544','503681') )");
				}else{
					sql.append("\n and M.brand in( "+Utils.converToTextSqlIn(o.getBrand())+")");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
				sql.append("\n and C.customer_code in("+Utils.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
				sql.append("\n and P.inventory_item_code in( "+Utils.converToTextSqlIn(o.getItemCode())+")");
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
			  requestDate = Utils.stringValue(rst.getDate("request_date"), Utils.YYYY_MM_DD_WITHOUT_SLASH,Utils.local_th);
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
				tdWidth = Math.round(90/(requestDateList.size()*2));
			}else{
				//calc td width
				tdWidth = Math.round(60/(requestDateList.size()*2));
			}//if
		}else{
			if(productList.size() >= 4){
				width="100%";
				//calc td width
				tdWidth = Math.round(90/productList.size());
			}else{
				//calc td width
				tdWidth = Math.round(60/productList.size());
			}//if 2
		}//if 1
		
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoSet'> \n");
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
			    //1.Type S
				if( Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_S")) != 0){
					colSpan++;
				   h.append("<td width='"+tdWidth+"%' class='td_bg_lineS'>"+requestDateTemp+"</td> \n");
				}
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
				if( !excel)
			       productName = productName.substring(0,25)+"<br/>"+productName.substring(25,productName.length());
				h.append("<td width='"+tdWidth+"%' class='td_bg_lineH'>"+item.getItemCode()+"<br/> "+productName+"</td> \n");
			}
		}
		h.append("</tr> \n");
		
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
			headReport.append(" <td colspan="+colSpan+"><b>พิมพ์วันที่ &nbsp;&nbsp;"+Utils.stringValue(new Date(), Utils.DD_MM_YYYY_HH_MM_SS_WITH_SLASH,Utils.local_th)+"</b> </td>");
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

		if(head.getReportType().equalsIgnoreCase("2")){
		  for(int i=0;i<productList.size();i++){
			item = productList.get(i);
			
			h.append("<tr> \n");
			h.append(" <td class=''>"+item.getItemCode()+"-"+item.getItemName()+"</td> \n");
			for(int n=0;n<requestDateList.size();n++){
				itemS = requestDateList.get(n);
				//1 Record Type =S Stock
				if( Utils.convertStrToDouble(sumRequestDateToMap.get(itemS.getRequestDate()+"_S")) != 0){
					key=  "S_"+itemS.getRequestDate()+"_"+item.getItemCode();
					itemResult = dataMAP.get(key);
					if(itemResult != null){
					   h.append(" <td  class='td_bg_lineS_num'>"+Utils.isNull(itemResult.getOrderQty())+"</td> \n");
					}else{
					   h.append(" <td  class='td_bg_lineS_num'></td> \n");
					}
				}
				
				//2 Record Type =A SalesAnalyst
				if( Utils.convertStrToDouble(sumRequestDateToMap.get(itemS.getRequestDate()+"_A")) != 0){
					key=  "A_"+itemS.getRequestDate()+"_"+item.getItemCode();
					itemResult = dataMAP.get(key);
					if(itemResult != null){
					   h.append(" <td  class='td_bg_lineA_num'>"+Utils.isNull(itemResult.getOrderQty())+"</td> \n");
					}else{
					   h.append(" <td  class='td_bg_lineA_num'></td> \n");
					}
				}
			}//for 2
			h.append("</tr> \n");
			
		  }//for 1
		  
		  
		 //////////////////////////////////////////////////
		  
		}else if(head.getReportType().equalsIgnoreCase("1")){
			  for(int i=0;i<requestDateList.size();i++){
				item = requestDateList.get(i);
				if( Utils.convertStrToDouble(sumRequestDateToMap.get(item.getRequestDate()+"_S")) != 0){
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
						if(itemResult != null){
						   h.append(" <td  class='td_bg_lineS_num'>"+Utils.isNull(itemResult.getOrderQty())+"</td> \n");
						}else{
						   h.append(" <td  class='td_bg_lineS_num'></td> \n");
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
						if(itemResult != null){
						   h.append(" <td  class='td_bg_lineA_num'>"+Utils.isNull(itemResult.getOrderQty())+"</td> \n");
						}else{
						   h.append(" <td  class='td_bg_lineA_num'></td> \n");
						}
					}//for 2
					h.append("</tr> \n");
				}//if check Row all is <> 0
			  }//for 1
			}//if
		return h;
	}
	
	//Return is set map :true
	public static boolean isUserMapCustSalesTT(User user){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		boolean isMap = false;
		try{
			sql.append("\n  SELECT count(*) as c from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M  ");
			sql.append("\n  where user_name='"+user.getUserName()+"'");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnectionApps();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				if(rst.getInt("c")>0){
					isMap = true;
				}
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	 return isMap;
	}
}
