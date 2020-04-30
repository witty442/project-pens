package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.location.LocationBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class StockReport {
	protected static Logger logger = Logger.getLogger("PENS");
	private static Map<String, String> COLUMNNAME_MAP = new HashMap<String, String>();
	static{
		COLUMNNAME_MAP.put("BRAND", "แบรนด์");
		COLUMNNAME_MAP.put("REGION", "ภาคการขาย");
		COLUMNNAME_MAP.put("SALES_CODE", "พนักงานขาย");
		COLUMNNAME_MAP.put("CUSTOMER_NUMBER", "ร้านค้า");
		COLUMNNAME_MAP.put("ITEM_NO", "SKU");
		COLUMNNAME_MAP.put("ZONE", "ภาคตามการดูแล");
		COLUMNNAME_MAP.put("BILL_STORE_COUNT", "จำนวนร้านที่เปิดบิลขาย");
		COLUMNNAME_MAP.put("CHECK_STORE_COUNT", "จำนวนร้านค้าที่นับสต๊อก");
	}
	public static StockBean searchReport(String contextPath ,StockBean o,boolean excel,User user){
	    logger.debug("excel:"+excel);
		if(o.getItemsList() !=null && o.getItemsList().size()>0){
			logger.debug("itemList:"+o.getItemsList().size());
			return searchReportModelCaseSort(contextPath,o,excel);
		}
		
		if( !Utils.isNull(o.getDispLastUpdate()).equals("")){
		   return searchReportLatestModel(contextPath,o,excel,user);
		}else{
		   return searchReportModel(contextPath,o,excel,user);
		}
	}
	public static StockBean searchReportModel(String contextPath,StockBean o,boolean excel,User user){
		StockBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String[] columnNameArr = null;
		String[] dispColumnNameArr = null;
		StringBuffer html = null;
		int r = 0;
		String columnAllSql = "";
		String columnAllGroupBySql = "";
		List<StockBean> itemList = new ArrayList<StockBean>();
		double totalPriQty = 0,totalSecQty =0;
		double totalBillStoreCount = 0,totalCheckStoreCount =0;
		String viewName ="xxpens_om_check_order_v";
		boolean reportShowColumnInListBox = false; //true show column only in(listBox)
		try{
			if( !Utils.isNull(o.getDispLastUpdate()).equals("")){
				viewName ="xxpens_om_check_order_vl";
			}
			//check display only column in Listbox
			if(o.getReportType().equalsIgnoreCase("SALES_CODE,BILL_STORE_COUNT,CHECK_STORE_COUNT")){
				reportShowColumnInListBox = true;
			}
			
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			String[] columnAll = genSelectColumnName(columnNameArr,o);
			columnAllSql = columnAll[0];
			columnAllGroupBySql = columnAll[1];
			
	        //add expire date
			 if( !reportShowColumnInListBox && o.getDispType().equalsIgnoreCase("pri_qty,sec_qty")){
			     columnAllSql +=",M.expire_date,M.avg_qty";
			     columnAllGroupBySql +=",M.expire_date,M.avg_qty";
			 }
			 //
			 if( !reportShowColumnInListBox && !Utils.isNull(o.getDispRequestDate()).equals("")){
				 columnAllSql ="M.request_date,"+columnAllSql;
			     columnAllGroupBySql ="M.request_date,"+columnAllGroupBySql;
			 }
			//split disp column arr
			dispColumnNameArr = o.getDispType().split("\\,");
			
			sql.append("\n  SELECT "+columnAllSql);
			if(!reportShowColumnInListBox ){
			  sql.append(","+genSelectColumnNameDispType(dispColumnNameArr));
			}
			
			//Get AVG Prev Month 6
			/*String yyyymm = "";
			String mm = "";
			Date startDateInit = Utils.parse(o.getStartDate(), Utils.DD_MMM_YYYY);
			Calendar c = Calendar.getInstance();
			c.setTime(startDateInit);
			for(int i=0;i<1;i++){
			   c.add(Calendar.MONTH, -1);
			   mm = (c.get(Calendar.MONTH)+1)+"";
			   mm = mm.length()==1?"0"+mm:mm;
			   yyyymm = ""+c.get(Calendar.YEAR)+ mm;
			   
			   logger.debug("yyyymm:"+yyyymm);
			   sql.append(genSqlAvgByMonth(o, (i+1), yyyymm));
			}*/
			
			sql.append("\n  FROM "+viewName+" M , PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  WHERE M.SALESREP_ID = Z.SALESREP_ID ");
		    //SalesChannel
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.sales_channel_name = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			//SalesZone
			if( !Utils.isNull(o.getSalesZone()).equals("")){
		    	sql.append("\n  AND Z.zone ='"+Utils.isNull(o.getSalesZone())+"'");
			}
			//Region
			if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
				sql.append("\n and M.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
			}
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
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
			    sql.append("\n and M.sales_code = '"+user.getUserName().toUpperCase()+"'");
			}
			
			//request date
			//TypeSerch Month
			if(Utils.isNull(o.getTypeSearch()).equals("month")){
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MMM_YYYY);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and M.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and M.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}else{
				//TypeSearch Day From To
				if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
					Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					logger.debug("startDate:"+startDate);
					String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
					
					sql.append("\n and M.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
					sql.append("\n and M.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
				}
			}
			sql.append("\n GROUP BY "+columnAllGroupBySql );
			sql.append("\n ORDER BY "+columnAllGroupBySql);
			
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString());
			}
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr,reportShowColumnInListBox));
			  }
			  
			  for(int i=0;i<columnNameArr.length;i++){
				   if("REGION".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesChannelNo(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesChannelName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
						item.setItemCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setItemName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesZone(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesZoneName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
						item.setCustomerCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setCustomerName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesrepCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesrepName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("BRAND".equalsIgnoreCase(columnNameArr[i])){
						item.setBrand(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setBrandName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("BILL_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
						 item.setBillStoreCount(Utils.decimalFormat(rst.getDouble("BILL_STORE_COUNT"), Utils.format_current_no_disgit));
					}else if("CHECK_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
						 item.setCheckStoreCount(Utils.decimalFormat(rst.getDouble("CHECK_STORE_COUNT"), Utils.format_current_no_disgit));
					}
			  }//for
			  if( !reportShowColumnInListBox){
				  if( !Utils.isNull(o.getDispRequestDate()).equals("")){
				     item.setRequestDate(DateUtil.stringValue(rst.getDate("REQUEST_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  }
				  item.setPriQty(Utils.decimalFormat(rst.getDouble("PRI_QTY"), Utils.format_current_no_disgit)); 
				  item.setSecQty(Utils.decimalFormat(rst.getDouble("SEC_QTY"), Utils.format_current_no_disgit)); 
				  
				  /** Gen Order Qty Or not  **/
				  if(o.getDispType().equalsIgnoreCase("pri_qty,sec_qty,order_qty")){
					  item.setOrderQty(Utils.decimalFormat(rst.getDouble("ORDER_QTY"), Utils.format_current_no_disgit));
				  }else{
					  item.setExpireDate(DateUtil.stringValue(rst.getDate("EXPIRE_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  }
				  
				  item.setAvgQty(Utils.decimalFormat(rst.getDouble("AVG_QTY"), Utils.format_current_no_disgit)); 
				  /*item.setAvgQty1(Utils.decimalFormat(rst.getDouble("AVG_QTY_1"), Utils.format_current_no_disgit)); 
				  item.setAvgQty2(Utils.decimalFormat(rst.getDouble("AVG_QTY_2"), Utils.format_current_no_disgit)); 
				  item.setAvgQty3(Utils.decimalFormat(rst.getDouble("AVG_QTY_3"), Utils.format_current_no_disgit)); 
				  item.setAvgQty4(Utils.decimalFormat(rst.getDouble("AVG_QTY_4"), Utils.format_current_no_disgit)); 
				  item.setAvgQty5(Utils.decimalFormat(rst.getDouble("AVG_QTY_5"), Utils.format_current_no_disgit)); 
				  item.setAvgQty6(Utils.decimalFormat(rst.getDouble("AVG_QTY_6"), Utils.format_current_no_disgit)); */
				  //add to List
				  itemList.add(item);
				  
				  //Summary
				  totalPriQty +=rst.getDouble("PRI_QTY");
				  totalSecQty +=rst.getDouble("SEC_QTY");
				  
			  }else{
				  //Summary ByReportType
				  totalBillStoreCount +=rst.getDouble("BILL_STORE_COUNT");
				  totalCheckStoreCount +=rst.getDouble("CHECK_STORE_COUNT");
				  
			  }
			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item,reportShowColumnInListBox));
			 
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // Get Total
				if(reportShowColumnInListBox){
			       html.append(genTotalTableByReportType(o,excel,columnNameArr, totalBillStoreCount,totalCheckStoreCount));
				}else{
				   html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty));
				}
			  // gen end Table
			  html.append("</table>");
			}
			
			o.setItemsList(itemList);
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
	

	public static StockBean searchReportLatestModel(String contextPath,StockBean o,boolean excel,User user){
		StockBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String[] columnNameArr = null;
		String[] dispColumnNameArr = null;
		StringBuffer html = null;
		int r = 0;
		String columnAllSql = "";
		String columnAllGroupBySql = "";
		List<StockBean> itemList = new ArrayList<StockBean>();
		double totalPriQty = 0 ,totalSecQty =0;
		double totalBillStoreCount = 0,totalCheckStoreCount =0;
		String viewName ="xxpens_om_check_order_v";
		boolean reportShowColumnInListBox = false; //true show column only in(listBox)
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//check display only column in Listbox
			if(o.getReportType().equalsIgnoreCase("SALES_CODE,BILL_STORE_COUNT,CHECK_STORE_COUNT")){
				reportShowColumnInListBox = true;
			}
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			String[] columnAll = genSelectColumnName(columnNameArr,o);
			columnAllSql = columnAll[0];
			columnAllGroupBySql = columnAll[1];
			
	        //add expire date
			 if( !reportShowColumnInListBox && o.getDispType().equalsIgnoreCase("pri_qty,sec_qty")){
			     columnAllSql +=",M.expire_date,M.avg_qty";
			     columnAllGroupBySql +=",M.expire_date,M.avg_qty";
			 }
			 //
			 if( !reportShowColumnInListBox && !Utils.isNull(o.getDispRequestDate()).equals("")){
				 columnAllSql ="M.request_date,"+columnAllSql;
			     columnAllGroupBySql ="M.request_date,"+columnAllGroupBySql;
			 }
			//split disp column arr
			dispColumnNameArr = o.getDispType().split("\\,");
			
			sql.append("\n  SELECT "+columnAllSql);
			if(!reportShowColumnInListBox){
			   sql.append(","+genSelectColumnNameDispType(dispColumnNameArr));
			}
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
			/******************************************************/
			
			sql.append("\n  , "+viewName+" M , PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n  WHERE M.SALESREP_ID = Z.SALESREP_ID ");
			sql.append("\n  and M.cust_account_id = a.cust_account_id ");
			sql.append("\n  and M.inventory_item_id = a.inventory_item_id");
			sql.append("\n  and M.request_date = a.request_date ");
		     //SalesChannel
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n and M.sales_channel_name = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			 //SalesZone
			if( !Utils.isNull(o.getSalesZone()).equals("")){
		    	sql.append("\n  AND Z.zone ='"+Utils.isNull(o.getSalesZone())+"'");
			}
			//Region
			if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
				sql.append("\n and M.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
			}
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
			
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				sql.append("\n and M.sales_code = '"+user.getUserName().toUpperCase()+"'");
			}
			
			sql.append("\n GROUP BY "+columnAllGroupBySql );
			sql.append("\n ORDER BY "+columnAllGroupBySql);
			
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString());
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new StockBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr,reportShowColumnInListBox));
			  }
			  
			  for(int i=0;i<columnNameArr.length;i++){
				   if("REGION".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesChannelNo(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesChannelName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
				   }else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesZone(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesZoneName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
						item.setItemCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setItemName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
						item.setCustomerCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setCustomerName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
						item.setSalesrepCode(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setSalesrepName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("BRAND".equalsIgnoreCase(columnNameArr[i])){
						item.setBrand(Utils.isNull(rst.getString(columnNameArr[i])));
						item.setBrandName(Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")));
					}else if("BILL_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
						 item.setBillStoreCount(Utils.decimalFormat(rst.getDouble("BILL_STORE_COUNT"), Utils.format_current_no_disgit));
					}else if("CHECK_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
						 item.setCheckStoreCount(Utils.decimalFormat(rst.getDouble("CHECK_STORE_COUNT"), Utils.format_current_no_disgit));
					}
			  }//for
			  
			  if(!reportShowColumnInListBox){
				  if( !Utils.isNull(o.getDispRequestDate()).equals("")){
				     item.setRequestDate(DateUtil.stringValue(rst.getDate("REQUEST_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  }
				  item.setPriQty(Utils.decimalFormat(rst.getDouble("PRI_QTY"), Utils.format_current_no_disgit)); 
				  item.setSecQty(Utils.decimalFormat(rst.getDouble("SEC_QTY"), Utils.format_current_no_disgit)); 
				  
				  /** Gen Order Qty Or not  **/
				  if(o.getDispType().equalsIgnoreCase("pri_qty,sec_qty,order_qty")){
					  item.setOrderQty(Utils.decimalFormat(rst.getDouble("ORDER_QTY"), Utils.format_current_no_disgit));
				  }else{
					  item.setExpireDate(DateUtil.stringValue(rst.getDate("EXPIRE_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				  }
				  
				  item.setAvgQty(Utils.decimalFormat(rst.getDouble("AVG_QTY"), Utils.format_current_no_disgit)); 
				  
				  totalPriQty +=rst.getDouble("PRI_QTY");
				  totalSecQty +=rst.getDouble("SEC_QTY");
			  }else{
				//Summary ByReportType
				  totalBillStoreCount +=rst.getDouble("BILL_STORE_COUNT");
				  totalCheckStoreCount +=rst.getDouble("CHECK_STORE_COUNT");
			  }
			  
			  //add to List
			  itemList.add(item);
			  
			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item,reportShowColumnInListBox));
			 
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // Get Total
			 if(reportShowColumnInListBox){
			     html.append(genTotalTableByReportType(o,excel,columnNameArr, totalBillStoreCount,totalCheckStoreCount));
			 }else{
				 html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty)); 
			 }
			  // gen end Table
			  html.append("</table>");
			}
			
			o.setItemsList(itemList);
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
	public static StockBean searchReportModelCaseSort(String contextPath,StockBean o,boolean excel){
		StockBean item = null;
		String[] columnNameArr = null;
		StringBuffer html = null;
		int r = 0;
		List<StockBean> itemList = o.getItemsList();
		double totalPriQty = 0,totalSecQty =0;
		double totalBillStoreCount = 0,totalCheckStoreCount =0;
		boolean reportShowColumnInListBox = false; //true show column only in(listBox)
		try{
			logger.debug("searchReportModelCaseSort");
			logger.debug("columnNameSort:"+o.getColumnNameSort());
			logger.debug("orderSortType:"+o.getOrderSortType());
			
			//check display only column in Listbox
			if(o.getReportType().equalsIgnoreCase("SALES_CODE,BILL_STORE_COUNT,CHECK_STORE_COUNT")){
				reportShowColumnInListBox = true;
			}
			
			//sort by Column
			if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("BRAND")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockBean.Comparators.BRAND_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.BRAND_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("REQUEST_DATE")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockBean.Comparators.REQUEST_DATE_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.REQUEST_DATE_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("REGION")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockBean.Comparators.SALES_CHANNEL_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.SALES_CHANNEL_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("ZONE")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockBean.Comparators.SALES_ZONE_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.SALES_ZONE_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("SALES_CODE")){
				if("DESC".equals(o.getOrderSortType())){
				   Collections.sort(itemList, StockBean.Comparators.SALES_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.SALES_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("CUSTOMER_NUMBER")){
				if("DESC".equals(o.getOrderSortType())){
					   Collections.sort(itemList, StockBean.Comparators.CUSTOMER_DESC);
				}else{
				   Collections.sort(itemList, StockBean.Comparators.CUSTOMER_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("ITEM_NO")){
				if("DESC".equals(o.getOrderSortType())){
				 Collections.sort(itemList, StockBean.Comparators.SKU_DESC);
				}else{
					Collections.sort(itemList, StockBean.Comparators.SKU_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("PRI_QTY")){
				if("DESC".equals(o.getOrderSortType())){
					 Collections.sort(itemList, StockBean.Comparators.PRI_QTY_DESC);
				}else{
					Collections.sort(itemList, StockBean.Comparators.PRI_QTY_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("SEC_QTY")){
				if("DESC".equals(o.getOrderSortType())){
					Collections.sort(itemList, StockBean.Comparators.SEC_QTY_DESC);
				}else{
					Collections.sort(itemList, StockBean.Comparators.SEC_QTY_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("EXPIRE_DATE")){
				if("DESC".equals(o.getOrderSortType())){
					 Collections.sort(itemList, StockBean.Comparators.EXPIRE_DATE_DESC);
				}else{
					 Collections.sort(itemList, StockBean.Comparators.EXPIRE_DATE_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("AVG_QTY")){
				if("DESC".equals(o.getOrderSortType())){
					 Collections.sort(itemList, StockBean.Comparators.AVG_QTY_DESC);
				}else{
					 Collections.sort(itemList, StockBean.Comparators.AVG_QTY_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("BILL_STORE_COUNT")){
				if("DESC".equals(o.getOrderSortType())){
					 Collections.sort(itemList, StockBean.Comparators.BILL_STORE_COUNT_DESC);
				}else{
					 Collections.sort(itemList, StockBean.Comparators.BILL_STORE_COUNT_ASC);
				}
			}else if(Utils.isNull(o.getColumnNameSort()).equalsIgnoreCase("CHECK_STORE_COUNT")){
				if("DESC".equals(o.getOrderSortType())){
					 Collections.sort(itemList, StockBean.Comparators.CHECK_STORE_COUNT_DESC);
				}else{
					 Collections.sort(itemList, StockBean.Comparators.CHECK_STORE_COUNT_ASC);
				}
			}
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			
			for (int row=0;row<itemList.size();row++) {
			  item = itemList.get(row);
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr,reportShowColumnInListBox));
				 
			  }

			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item,reportShowColumnInListBox));
			  
			  //Summary
			  if(!reportShowColumnInListBox){
			     totalPriQty +=Utils.convertStrToDouble(item.getPriQty());
			     totalSecQty +=Utils.convertStrToDouble(item.getSecQty());
			  }else{
				 totalBillStoreCount +=Utils.convertStrToDouble(item.getBillStoreCount());
				 totalCheckStoreCount +=Utils.convertStrToDouble(item.getCheckStoreCount());
			  }
			}//while
			
			//Check Execute Found data
			if(r>0){
				// Get Total
				 if(reportShowColumnInListBox){
				     html.append(genTotalTableByReportType(o,excel,columnNameArr, totalBillStoreCount,totalCheckStoreCount));
				 }else{
					 html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty)); 
				 }
				// gen end Table
				html.append("</table>");
			}
			
			o.setItemsList(itemList);
			o.setDataStrBuffer(html);
		}catch(Exception e){
			//logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			try {
				
			} catch (Exception e) {}
		}
	  return o;
	}
	//SQL SLow Wait 
	 private static StringBuffer genSqlAvgByMonth(StockBean o ,int c,String yyyymm){
		 StringBuffer sql = new StringBuffer();
		 try {
			 sql.append("\n ,( ");
			 sql.append("\n  SELECT  NVL(SUM(INVOICED_AMT),0) AS INVOICED_AMT ");
			 sql.append("\n  FROM PENSBI.XXPENS_BI_SALES_ANALYSIS_V V ");
			 sql.append("\n  ,PENSBI.XXPENS_BI_MST_ITEM P ");
			 sql.append("\n  ,PENSBI.XXPENS_BI_MST_CUSTOMER C ");
			 sql.append("\n  WHERE V.inventory_item_id = P.inventory_item_id  ");
			 sql.append("\n  AND V.customer_id = C.customer_id  ");
			  //SalesChannel
				if( !Utils.isNull(o.getCustCatNo()).equals("")){
					if("Credit Sales".equals(Utils.isNull(o.getCustCatNo()))){
					   sql.append("\n  AND V.Customer_Category = 'ORDER - CREDIT SALES' ");
					}
				}
				 //SalesZone
				if( !Utils.isNull(o.getSalesZone()).equals("")){
			    	sql.append("\n  AND V.sales_zone ='"+Utils.isNull(o.getSalesZone())+"' ");
				}
				//Region
				if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
					sql.append("\n AND V.sales_channel = '"+Utils.isNull(o.getSalesChannelNo())+"' ");
				}
				if( !Utils.isNull(o.getSalesrepCode()).equals("")){
					sql.append("\n AND V.salesrep_id in(  ");
					sql.append("\n  select salesrep_id from PENSBI.XXPENS_BI_MST_SALESREP  "); 
					sql.append("\n  where .salesrep_code = '"+Utils.isNull(o.getSalesrepCode())+"' ");
					sql.append("\n ) ");
				}
				if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
					// Brand 504 must show 503494,503544,503681 (Case Special case )
					if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
						sql.append("\n and ( V.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")  ");
						sql.append("\n     or P.inventory_item_codein('503494','503544','503681' ) ) ");
					}else{
						sql.append("\n and V.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")  ");
					}
				}
				if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
					sql.append("\n  AND C.customer_code in( "+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")  ");
				}
				if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
					sql.append("\n AND P.inventory_item_code in( "+SQLHelper.converToTextSqlIn(o.getCustomerCode())+") ");
				}
				sql.append("\n AND TO_CHAR(V.INVOICE_DATE,'YYYYMM') = '"+yyyymm+"' ");
				
				//where join by display column
				/*dataList.add(new PopupBean("reportType","SKU","ITEM_NO"));
				dataList.add(new PopupBean("reportType","ร้านค้า,SKU","CUSTOMER_NUMBER,ITEM_NO"));
				dataList.add(new PopupBean("reportType","พนักงานขาย,ร้านค้า,SKU","SALES_CODE,CUSTOMER_NUMBER,ITEM_NO"));
				dataList.add(new PopupBean("reportType","ภาค,แบรนด์,SKU","REGION,BRAND,ITEM_NO"));
				dataList.add(new PopupBean("reportType","ภาค,พนักงานขาย,แบรนด์,SKU","REGION,SALES_CODE,BRAND,ITEM_NO"));
				
				dataList.add(new PopupBean("reportType","ภาคตามสายดูแล,แบรนด์,SKU","ZONE,BRAND,ITEM_NO"));
				dataList.add(new PopupBean("reportType","ภาคตามสายดูแล,พนักงานขาย,แบรนด์,SKU","ZONE,SALES_CODE,BRAND,ITEM_NO"));*/
				
				if(o.getReportType().equals("ITEM_NO")){//SKU
					sql.append("\n and P.inventory_item_code = M.item_no");
					sql.append("\n and V.invoice_date = M.request_date");
					
					sql.append("\n GROUP BY P.inventory_item_code");
				}
				
			 sql.append("\n ) as avg_qty_"+c);
			 
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }
		return sql;
	 }
	private static String[] genSelectColumnName(String[] columnNameArr,StockBean o) throws Exception{
		String[] sqlAll = new String[2];
		StringBuffer sql = new StringBuffer("");
		String columnGroupBy ="";
		for(int i=0;i<columnNameArr.length;i++){
		  if("Brand".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n M.BRAND,");
			  sql.append("\n (select x.brand_desc from XXPENS_BI_MST_BRAND X WHERE X.brand_no = M.brand) as BRAND_NAME,");
			  columnGroupBy +="\n M.brand,";
		  }else if("REGION".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n M.REGION,");
			  sql.append("\n M.REGION_NAME,");
			  columnGroupBy +="\n M.REGION ,";
			  columnGroupBy +="\n M.REGION_NAME,";
		  }else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n Z.ZONE,");
			  sql.append("\n Z.ZONE_NAME,");
			  columnGroupBy +="\n Z.ZONE ,";
			  columnGroupBy +="\n Z.ZONE_NAME,";
		  }else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n M.SALES_CODE,");
			  sql.append("\n M.SALESREP_FULL_NAME as SALES_CODE_NAME,");
			  
			  columnGroupBy +="\n M.SALES_CODE,";
			  columnGroupBy +="\n M.SALESREP_FULL_NAME,";
		  }else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n M.CUSTOMER_NUMBER,");
			  sql.append("\n M.PARTY_NAME as CUSTOMER_NUMBER_NAME,");
			  
			  columnGroupBy +="\n M.CUSTOMER_NUMBER,";
			  columnGroupBy +="\n M.PARTY_NAME,";
		  }else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n M.ITEM_NO,");
			  sql.append("\n M.ITEM_NAME AS ITEM_NO_NAME,");
			  
			  columnGroupBy +="\n M.ITEM_NO,";
			  columnGroupBy +="\n M.ITEM_NAME,";
		  }else if("BILL_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n (");
			  sql.append("\n SELECT count(DISTINCT V.customer_id) as c");
			  sql.append("\n from PENSBI.XXPENS_BI_SALES_ANALYSIS_V V ");
			  sql.append("\n ,PENSBI.XXPENS_BI_MST_ITEM P ");
			  sql.append("\n ,PENSBI.XXPENS_BI_MST_CUSTOMER C ");
			  sql.append("\n ,PENSBI.XXPENS_BI_MST_SALESREP S");
			  sql.append("\n WHERE V.inventory_item_id = P.inventory_item_id  ");
			  sql.append("\n AND V.customer_id = C.customer_id  ");
			  sql.append("\n AND V.salesrep_id = S.salesrep_id ");
			  sql.append("\n AND S.salesrep_code = M.SALES_CODE ");
			 //TypeSerch Month
				if(Utils.isNull(o.getTypeSearch()).equals("month")){
					if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
						Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MMM_YYYY);
						logger.debug("startDate:"+startDate);
						String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MMM_YYYY);
						String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						
						sql.append("\n and V.invoice_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
						sql.append("\n and V.invoice_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
					}
				}else{
					//TypeSearch Day From To
					if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
						Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						logger.debug("startDate:"+startDate);
						String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
						
						sql.append("\n and V.invoice_date e >= to_date('"+startDateStr+"','dd/mm/yyyy')");
						sql.append("\n and V.invoice_date  <= to_date('"+endDateStr+"','dd/mm/yyyy')");
					}
				}
				//SalesChannel
				if( !Utils.isNull(o.getCustCatNo()).equals("")){
					if("Credit Sales".equals(Utils.isNull(o.getCustCatNo()))){
					   sql.append("\n  AND V.Customer_Category = 'ORDER - CREDIT SALES' ");
					}
				}
				 //SalesZone
				if( !Utils.isNull(o.getSalesZone()).equals("")){
			    	sql.append("\n  AND V.sales_zone ='"+Utils.isNull(o.getSalesZone())+"' ");
				}
				//Region
				if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
					sql.append("\n AND V.sales_channel = '"+Utils.isNull(o.getSalesChannelNo())+"' ");
				}
				if( !Utils.isNull(o.getSalesrepCode()).equals("")){
					sql.append("\n AND V.salesrep_id in(  ");
					sql.append("\n  select salesrep_id from PENSBI.XXPENS_BI_MST_SALESREP  "); 
					sql.append("\n  where .salesrep_code = '"+Utils.isNull(o.getSalesrepCode())+"' ");
					sql.append("\n ) ");
				}
				if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
					// Brand 504 must show 503494,503544,503681 (Case Special case )
					if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
						sql.append("\n and ( V.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")  ");
						sql.append("\n     or P.inventory_item_codein('503494','503544','503681' ) ) ");
					}else{
						sql.append("\n and V.brand in( "+SQLHelper.converToTextSqlIn(o.getBrand())+")  ");
					}
				}
				if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
					sql.append("\n  AND C.customer_code in( "+SQLHelper.converToTextSqlIn(o.getCustomerCode())+")  ");
				}
				if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
					sql.append("\n AND P.inventory_item_code in( "+SQLHelper.converToTextSqlIn(o.getCustomerCode())+") ");
				}
				sql.append("\n ) as BILL_STORE_COUNT,");
		  }else if("CHECK_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
			  sql.append("\n (count(distinct M.CUSTOMER_NUMBER)) as CHECK_STORE_COUNT,");
			  
		  }//if
		}//for
		
		//remove "," in last position
		 String sqlStr = sql.toString().substring(0,sql.toString().length()-1);
	     columnGroupBy = columnGroupBy.substring(0,columnGroupBy.length()-1);
		
		sqlAll[0] = sqlStr;
		sqlAll[1] = columnGroupBy;
	  return sqlAll;
	}
	private static String genSelectColumnNameDispType(String[] columnNameArr) throws Exception{
		String sql = "";
		for(int i=0;i<columnNameArr.length;i++){
			 if("order_qty".equalsIgnoreCase(columnNameArr[i])){
				sql +="\n 0 as "+columnNameArr[i]+" ,"; 
			 }else{
			    sql +="\n NVL(SUM("+columnNameArr[i]+"),0) as "+columnNameArr[i]+" ,"; 
			 }
		}//for
		
		//remove "," in last position
	    sql = sql.substring(0,sql.length()-1);
	    return sql;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(String contextPath,StockBean head,boolean excel,String[] columnNameArr
			,boolean reportShowColumnInListBox) throws Exception{
		String icoZise=  "width='20px' height='20px'";
		StringBuffer h = new StringBuffer("");
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
		}
		String width="100%";
		if(columnNameArr.length<2){
			width="60%";
		}
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		if(!reportShowColumnInListBox && !Utils.isNull(head.getDispRequestDate()).equals("")){
			h.append(" <th rowspan='2' nowrap >");
			h.append(" วันที่ตรวจนับ");
			 if( excel ==false){
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('REQUEST_DATE','ASC') />");
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('REQUEST_DATE','DESC') />");
			}
			h.append("</th> \n");
		}
		
		for(int i=0;i<columnNameArr.length;i++){
		  if( excel ==false){
			  if(!reportShowColumnInListBox){
			     h.append(" <th rowspan='2'  nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]));
			  }else{
				 h.append(" <th nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]));
			  }
			   if( excel ==false){
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('"+columnNameArr[i]+"','ASC') />");
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('"+columnNameArr[i]+"','DESC') />");
			   }
			   h.append(" </th> \n");
		  }else{
			  if(!reportShowColumnInListBox){
			     h.append(" <th  rowspan='2'  nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]) +"</th>");
			  }else{
				 h.append(" <th   nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]) +"</th>"); 
			  }
			  //split name 
			  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
			  if(!reportShowColumnInListBox){
				  if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  rowspan='2'  nowrap>ชื่อ SKU</th>");
				  }else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  rowspan='2'  nowrap>ชื่อ ร้านค้า</th>");
				  }else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  rowspan='2'  nowrap>ชื่อ พนักงาน</th>");
				  }
			  }else{
				  if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  nowrap>ชื่อ SKU</th>");
				  }else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  nowrap>ชื่อ ร้านค้า</th>");
				  }else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					  h.append(" <th  nowrap>ชื่อ พนักงาน</th>");
				  }
			  }
		  }
		}//for
		if(!reportShowColumnInListBox){
			h.append(" <th colspan='2' rowspan='1' nowrap>ยอดตรวจนับ</th> \n");
			if(head.getDispType().equalsIgnoreCase("pri_qty,sec_qty,order_qty")){
			   h.append(" <th rowspan='2' nowrap>ยอดสั่งซื้อ");
			   if( excel ==false){
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('ORDER_QTY','ASC') />");
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('ORDER_QTY','DESC') />");
			   }
			   h.append(" </th> \n");
			}else{
			   h.append(" <th rowspan='2' nowrap>วันที่หมดอายุ");
			   if( excel ==false){
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('EXPIRE_DATE','ASC') />");
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('EXPIRE_DATE','DESC') />");
			   }
			   h.append(" </th> \n");
			}
				h.append(" <th rowspan='2' nowrap >");
				h.append(" ยอดขายเฉลี่ย 3 เดือน");
				 if( excel ==false){
					h.append("  &nbsp;&nbsp;");
					h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('AVG_QTY','ASC') />");
					h.append("  &nbsp;&nbsp;");
					h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('AVG_QTY','DESC') />");
				}
				h.append("</th> \n");
				
				//Get AVG Prev Month 6
			/*	String monthYear = "";
				Date startDateInit = Utils.parse(head.getStartDate(), Utils.DD_MMM_YYYY);
				Calendar c = Calendar.getInstance();
				c.setTime(startDateInit);
				for(int i=0;i<6;i++){
				   c.add(Calendar.MONTH, -1);
				   monthYear = Utils.stringValue(c.getTime(), Utils.MMM_YY,Utils.local_th);
				   
				   logger.debug("monthYear"+monthYear);
				   
					h.append(" <th rowspan='2' nowrap >");
					h.append(" ยอดขาย "+monthYear);
					 if( excel ==false){
						h.append("  &nbsp;&nbsp;");
						h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('AVG_QTY_"+(i+1)+"','ASC') />");
						h.append("  &nbsp;&nbsp;");
						h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('AVG_QTY_"+(i+1)+"','DESC') />");
					}
					h.append("</th> \n");
				}//for
	*/		
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append(" <th nowrap>หีบ");
			 if( excel ==false){
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('PRI_QTY','ASC') />");
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('PRI_QTY','DESC') />");
			 }
			h.append("</th> \n");
			h.append(" <th  nowrap>เศษ");
			 if( excel ==false){
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('SEC_QTY','ASC') />");
			    h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('SEC_QTY','DESC') />");
			 }
			h.append("</th> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genRowTable(StockBean head,boolean excel,String[] columnNameArr,StockBean item,boolean reportShowColumnInListBox) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="";
		String classNameCenter ="";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		h.append("<tr> \n");
		
		if( !reportShowColumnInListBox && !Utils.isNull(head.getDispRequestDate()).equals("")){
		   h.append(" <td class='"+className+"' width='8%'>"+item.getRequestDate()+"</td> \n");
		}
		for(int i=0;i<columnNameArr.length;i++){
			//logger.debug("columnName["+columnNameArr[i]+"]");
			if(excel){
				className = "text";
				classNameCenter ="text";
				classNameNumber = "num_currency";
			}else{
				if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])
				 || "SALES_CODE".equalsIgnoreCase(columnNameArr[i])
				 || "CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])
				 || "ZONE".equalsIgnoreCase(columnNameArr[i])
				 ){
					className ="td_text";
					classNameCenter="td_text_center";
				}
			}
           
			if(excel==false){
				if("REGION".equalsIgnoreCase(columnNameArr[i])){
					 h.append("<td class='"+className+"' width='10%'>"+item.getSalesChannelName()+"</td> \n");
				}else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='20%'>"+item.getItemCode()+"-"+item.getItemName()+"</td> \n");
				}else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getSalesZone()+"-"+item.getSalesZoneName()+"</td> \n");
				}else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getCustomerCode()+"-"+item.getCustomerName()+"</td> \n");
				}else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getSalesrepCode()+"-"+item.getSalesrepName()+"</td> \n");
				}else if("BRAND".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='8%'>"+item.getBrand()+"-"+item.getBrandName()+"</td> \n");
				}else if("BILL_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getBillStoreCount()+"</td> \n");
				}else if("CHECK_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getCheckStoreCount()+"</td> \n");
				}
			}else{
				 //split name 
				if("REGION".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getSalesChannelName()+"</td> \n");
				}else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='5%'>"+item.getItemCode()+"</td> \n");
					h.append("<td class='"+className+"' width='15%'>"+item.getItemName()+"</td> \n");
				}else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='10%'>"+item.getSalesZone()+"-"+item.getSalesZoneName()+"</td> \n");
				}else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='4%'>"+item.getCustomerCode()+"</td> \n");
					h.append("<td class='"+className+"' width='6%'>"+item.getCustomerName()+"</td> \n");
				}else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='3%'>"+item.getSalesrepCode()+"</td> \n");
					h.append("<td class='"+className+"' width='7%'>"+item.getSalesrepName()+"</td> \n");
				}else if("BRAND".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+className+"' width='8%'>"+item.getBrand()+"-"+item.getBrandName()+"</td> \n");
				}else if("BILL_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getBillStoreCount()+"</td> \n");
				}else if("CHECK_STORE_COUNT".equalsIgnoreCase(columnNameArr[i])){
					h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getCheckStoreCount()+"</td> \n");
				}
			}
		}
		if(!reportShowColumnInListBox){
			h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getPriQty()+"</td> \n");
			h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getSecQty()+"</td> \n");
		    if(head.getDispType().equalsIgnoreCase("pri_qty,sec_qty,order_qty")){
			   h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getOrderQty()+"</td> \n");
			}else{
			  h.append("<td class='"+classNameCenter+"' width='8%'>"+item.getExpireDate()+"</td> \n");
			}
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty()+"</td> \n");
		  /*  h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty1()+"</td> \n");
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty2()+"</td> \n");
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty3()+"</td> \n");
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty4()+"</td> \n");
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty5()+"</td> \n");
		    h.append("<td class='"+classNameNumber+"' width='8%'>"+item.getAvgQty6()+"</td> \n");*/
		}
		h.append("</tr> \n");
		
		return h;
	}
	
	private static StringBuffer genTotalTableByReportType(StockBean head,boolean excel,String[] columnNameArr
			,double totalBillStoreCount,double totalCheckStoreCount) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="hilight_text";
		String classNameNumber = "td_number";
		int colspan=0;
		if(excel){
			className ="colum_head";
			classNameNumber = "num_currency_bold";
		}
		h.append("<tr class='"+className+"'> \n");
		colspan = 1;
		if(excel){
			colspan =2;
		}
			
		h.append(" <td class='"+className+"' align='right' colspan="+colspan+">Total</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalBillStoreCount, Utils.format_current_no_disgit)+"</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalCheckStoreCount, Utils.format_current_no_disgit)+"</td> \n");
		h.append("</tr> \n");
		return h;
	}
	
	private static StringBuffer genTotalTable(StockBean head,boolean excel,String[] columnNameArr
			,double totalPriQty,double totalSecQty) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="hilight_text";
		String classNameNumber = "td_number";
		int colspan=0;
		if(excel){
			className ="colum_head";
			classNameNumber = "num_currency_bold";
		}
		
		h.append("<tr class='"+className+"'> \n");
		if(!Utils.isNull(head.getDispRequestDate()).equals("")){
			colspan = columnNameArr.length+1;
			if(excel){
			    colspan=1;
				
				for(int i=0;i<columnNameArr.length;i++){
				  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
				  colspan++;
				  if("ITEM_NO".equalsIgnoreCase(columnNameArr[i]) 
					|| "CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])
					|| "SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					  colspan++;
				  }
				}
			}
			h.append(" <td class='"+className+"' align='right' colspan="+colspan+">Total</td> \n");
		}else{
			colspan = columnNameArr.length;
			if(excel){
				for(int i=0;i<columnNameArr.length;i++){
				  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
				  colspan++;
				  if("ITEM_NO".equalsIgnoreCase(columnNameArr[i]) 
					|| "CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])
					|| "SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
					  colspan++;
				  }
				}
			}
			h.append(" <td class='"+className+"' align='right' colspan="+colspan+">Total</td> \n");
		}
		
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalPriQty, Utils.format_current_no_disgit)+"</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalSecQty, Utils.format_current_no_disgit)+"</td> \n");

		h.append("<td ></td> \n");
		h.append("<td ></td> \n");
		
		h.append("</tr> \n");
		
		return h;
	}
}
