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
import com.isecinc.pens.report.salesanalyst.helper.FileUtil;
import com.isecinc.pens.web.location.LocationBean;

import util.DBConnection;
import util.DateToolsUtil;
import util.ExcelHeader;
import util.Utils;

public class StockCreditExpireReport {
	protected static Logger logger = Logger.getLogger("PENS");
	private static Map<String, String> COLUMNNAME_MAP = new HashMap<String, String>();
	static{
		COLUMNNAME_MAP.put("BRAND", "�ù��");
		COLUMNNAME_MAP.put("REGION", "�Ҥ��â��");
		COLUMNNAME_MAP.put("SALES_CODE", "��ѡ�ҹ���");
		COLUMNNAME_MAP.put("CUSTOMER_NUMBER", "��ҹ���");
		COLUMNNAME_MAP.put("ITEM_NO", "SKU");
		COLUMNNAME_MAP.put("ZONE", "�Ҥ�����ô���");
	}
	public static StockBean searchReport(String contextPath ,StockBean o,boolean excel,User user){
	    logger.debug("excel:"+excel);
		if(o.getItemsList() !=null && o.getItemsList().size()>0){
			logger.debug("itemList:"+o.getItemsList().size());
			return searchReportModelCaseSort(contextPath,o,excel);
		}
		return searchReportLatestModel(contextPath,o,excel,user);
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
		double totalPriQty = 0;
		double totalSecQty =0;
		String viewName ="xxpens_om_check_order_v";
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			String[] columnAll = genSelectColumnName(columnNameArr);
			columnAllSql = columnAll[0];
			columnAllGroupBySql = columnAll[1];
			
	        //add expire date
			columnAllSql +=",M.expire_date,M.avg_qty";
			columnAllGroupBySql +=",M.expire_date,M.avg_qty";
			 
		    //add request_date
		    columnAllSql ="M.request_date,"+columnAllSql;
			columnAllGroupBySql ="M.request_date,"+columnAllGroupBySql;
			 
			//split disp column arr
			dispColumnNameArr = "pri_qty,sec_qty".split("\\,");
			
			sql.append("\n  SELECT "+columnAllSql);
			sql.append(","+genSelectColumnNameDispType(dispColumnNameArr));
			sql.append("\n  FROM (");
			sql.append("\n    SELECT A.* FROM (");
			/*****************************************************/
			sql.append("\n    SELECT M.* ");
			sql.append("\n    ,(M.expire_date - M.request_date) as product_age_day");
			sql.append("\n    ,(select day from PENSBI.M_STOCK_BRAND_SAVEZONE S where S.brand = M.brand) as product_savezone_day");
			sql.append("\n    FROM ");
			sql.append("\n      (select max(a.request_date) request_date");
			sql.append("\n      ,a.cust_account_id");
			sql.append("\n      ,a.inventory_item_id");
			sql.append("\n      from xxpens_om_check_order_v a ");
			sql.append("\n      where 1=1 ");
	        //request date
			//TypeSearch Day From To
			if( !Utils.isNull(o.getStartDate()).equals("") && !Utils.isNull(o.getEndDate()).equals("")){
				Date startDate = Utils.parse(o.getStartDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				logger.debug("startDate:"+startDate);
				String startDateStr = Utils.stringValue(startDate, Utils.DD_MM_YYYY_WITH_SLASH);
				Date endDate = Utils.parse(o.getEndDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n      and a.request_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
				sql.append("\n      and a.request_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
			}
			sql.append("\n       group by a.cust_account_id ,a.inventory_item_id");
			sql.append("\n      ) a");
			/******************************************************/
			sql.append("\n    , "+viewName+" M , PENSBI.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n    WHERE M.SALESREP_ID = Z.SALESREP_ID ");
			sql.append("\n    and M.cust_account_id = a.cust_account_id ");
			sql.append("\n    and M.inventory_item_id = a.inventory_item_id");
			sql.append("\n    and M.request_date = a.request_date ");
		     //SalesChannel
			if( !Utils.isNull(o.getCustCatNo()).equals("")){
				sql.append("\n   and M.sales_channel_name = '"+Utils.isNull(o.getCustCatNo())+"'");
			}
			 //SalesZone
			if( !Utils.isNull(o.getSalesZone()).equals("")){
		    	sql.append("\n    AND Z.zone ='"+Utils.isNull(o.getSalesZone())+"'");
			}
			//Region
			if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
				sql.append("\n   and M.region = '"+Utils.isNull(o.getSalesChannelNo())+"'");
			}
			if( !Utils.isNull(o.getSalesrepCode()).equals("")){
				sql.append("\n   and M.sales_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
			}
			if( !Utils.isNull(o.getBrand()).equals("") && !Utils.isNull(o.getBrand()).equals("ALL")){
				// Brand 504 must show 503494,503544,503681 (Case Special case )
				if(Utils.isNull(o.getBrand()).indexOf("504") != -1 ){
					sql.append("\n   and ( M.brand in( "+Utils.converToTextSqlIn(o.getBrand())+")");
					sql.append("\n       or M.item_no in('503494','503544','503681') )");
				}else{
					sql.append("\n   and M.brand in( "+Utils.converToTextSqlIn(o.getBrand())+")");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("") && !Utils.isNull(o.getCustomerCode()).equals("ALL")){
				sql.append("\n   and M.customer_number in( "+Utils.converToTextSqlIn(o.getCustomerCode())+")");
			}
			if( !Utils.isNull(o.getItemCode()).equals("") && !Utils.isNull(o.getItemCode()).equals("ALL")){
				sql.append("\n   and M.item_no in( "+Utils.converToTextSqlIn(o.getItemCode())+")");
			}
			
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				sql.append("\n   and M.sales_code = '"+user.getUserName().toUpperCase()+"'");
			}
			sql.append("\n    )A ");
			
			//Condition product_age_day < product_savezone_day
			sql.append("\n   WHERE A.product_age_day < A.product_savezone_day ");
			
			sql.append("\n )M ");
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
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr));
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
					}
			  }//for
			  if( !Utils.isNull(o.getDispRequestDate()).equals("")){
			     item.setRequestDate(Utils.stringValue(rst.getDate("REQUEST_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  }
			  item.setPriQty(Utils.decimalFormat(rst.getDouble("PRI_QTY"), Utils.format_current_no_disgit)); 
			  item.setSecQty(Utils.decimalFormat(rst.getDouble("SEC_QTY"), Utils.format_current_no_disgit)); 
			  item.setExpireDate(Utils.stringValue(rst.getDate("EXPIRE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  item.setAvgQty(Utils.decimalFormat(rst.getDouble("AVG_QTY"), Utils.format_current_no_disgit)); 
			  
			  //add to List
			  itemList.add(item);
			  
			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item));
			  //Summary
			  totalPriQty +=rst.getDouble("PRI_QTY");
			  totalSecQty +=rst.getDouble("SEC_QTY");
			  
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // Get Total
			   html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty));
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
		double totalPriQty = 0;
		double totalSecQty =0;
		try{
			logger.debug("searchReportModelCaseSort");
			logger.debug("columnNameSort:"+o.getColumnNameSort());
			logger.debug("orderSortType:"+o.getOrderSortType());
			
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
			}
			
			//split column array
			columnNameArr = o.getReportType().split("\\,");
			
			for (int row=0;row<itemList.size();row++) {
			  item = itemList.get(row);
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(contextPath,o,excel,columnNameArr));
			  }

			  //Gen Row Table
			  html.append(genRowTable(o,excel,columnNameArr,item));
			  //Summary
			  totalPriQty +=Utils.convertStrToDouble(item.getPriQty());
			  totalSecQty +=Utils.convertStrToDouble(item.getSecQty());
			}//while
			
			//Check Execute Found data
			if(r>0){
				// Get Total
				html.append(genTotalTable(o,excel,columnNameArr, totalPriQty,totalSecQty));
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
	private static String[] genSelectColumnName(String[] columnNameArr) throws Exception{
		String[] sqlAll = new String[2];
		String sql = "";
		String columnGroupBy ="";
		for(int i=0;i<columnNameArr.length;i++){
		  if("Brand".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.BRAND,";
			  sql +="\n (select x.brand_desc from XXPENS_BI_MST_BRAND X WHERE X.brand_no = M.brand) as BRAND_NAME,";
			  columnGroupBy +="\n M.brand,";
		  }else if("REGION".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.REGION,";
			  sql +="\n M.REGION_NAME,";
			  columnGroupBy +="\n M.REGION ,";
			  columnGroupBy +="\n M.REGION_NAME,";
		  }else if("ZONE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n Z.ZONE,";
			  sql +="\n Z.ZONE_NAME,";
			  columnGroupBy +="\n Z.ZONE ,";
			  columnGroupBy +="\n Z.ZONE_NAME,";
		  }else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SALES_CODE,";
			  sql +="\n M.SALESREP_FULL_NAME as SALES_CODE_NAME,";
			  
			  columnGroupBy +="\n M.SALES_CODE,";
			  columnGroupBy +="\n M.SALESREP_FULL_NAME,";
		  }else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.CUSTOMER_NUMBER,";
			  sql +="\n M.PARTY_NAME as CUSTOMER_NUMBER_NAME,";
			  
			  columnGroupBy +="\n M.CUSTOMER_NUMBER,";
			  columnGroupBy +="\n M.PARTY_NAME,";
		  }else if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.ITEM_NO,";
			  sql +="\n M.ITEM_NAME AS ITEM_NO_NAME,";
			  
			  columnGroupBy +="\n M.ITEM_NO,";
			  columnGroupBy +="\n M.ITEM_NAME,";
		  }//if
		}//for
		
		//remove "," in last position
		 sql = sql.substring(0,sql.length()-1);
	     columnGroupBy = columnGroupBy.substring(0,columnGroupBy.length()-1);
		
		sqlAll[0] = sql;
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
	private static StringBuffer genHeadTable(String contextPath,StockBean head,boolean excel,String[] columnNameArr) throws Exception{
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
		//if( !Utils.isNull(head.getDispRequestDate()).equals("")){
			h.append(" <th rowspan='2' nowrap >");
			h.append(" �ѹ����Ǩ�Ѻ");
			 if( excel ==false){
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('REQUEST_DATE','ASC') />");
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('REQUEST_DATE','DESC') />");
			}
			h.append("</th> \n");
		//}
		
		for(int i=0;i<columnNameArr.length;i++){
		  if( excel ==false){
			   h.append(" <th  rowspan='2'  nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]));
			   if( excel ==false){
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('"+columnNameArr[i]+"','ASC') />");
				   h.append("  &nbsp;&nbsp;");
				   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('"+columnNameArr[i]+"','DESC') />");
			   }
			   h.append(" </th> \n");
		  }else{
			  h.append(" <th  rowspan='2'  nowrap>"+COLUMNNAME_MAP.get(columnNameArr[i]) +"</th>");
			  //split name 
			  logger.debug("columnNameArr[i]:"+columnNameArr[i]);
			  if("ITEM_NO".equalsIgnoreCase(columnNameArr[i])){
				  h.append(" <th  rowspan='2'  nowrap>���� SKU</th>");
			  }else if("CUSTOMER_NUMBER".equalsIgnoreCase(columnNameArr[i])){
				  h.append(" <th  rowspan='2'  nowrap>���� ��ҹ���</th>");
			  }else if("SALES_CODE".equalsIgnoreCase(columnNameArr[i])){
				  h.append(" <th  rowspan='2'  nowrap>���� ��ѡ�ҹ</th>");
			  }
		  }
		}
		h.append(" <th colspan='2' rowspan='1' nowrap>�ʹ��Ǩ�Ѻ</th> \n");
		
		   h.append(" <th rowspan='2' nowrap>�ѹ����������");
		   if( excel ==false){
			   h.append("  &nbsp;&nbsp;");
			   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('EXPIRE_DATE','ASC') />");
			   h.append("  &nbsp;&nbsp;");
			   h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('EXPIRE_DATE','DESC') />");
		   }
		   h.append(" </th> \n");
		
			h.append(" <th rowspan='2' nowrap >");
			h.append(" �ʹ�������� 3 ��͹");
			 if( excel ==false){
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('AVG_QTY','ASC') />");
				h.append("  &nbsp;&nbsp;");
				h.append("  <img style=\"cursor:pointer\"" +icoZise +"src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('AVG_QTY','DESC') />");
			}
			h.append("</th> \n");
		
		h.append("</tr> \n");
		h.append("<tr> \n");
		h.append(" <th nowrap>�պ");
		 if( excel ==false){
			h.append("  &nbsp;&nbsp;");
			h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('PRI_QTY','ASC') />");
			h.append("  &nbsp;&nbsp;");
			h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('PRI_QTY','DESC') />");
		 }
		h.append("</th> \n");
		h.append(" <th  nowrap>���");
		 if( excel ==false){
			h.append("  &nbsp;&nbsp;");
			h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-asc.png' href='#' onclick=sort('SEC_QTY','ASC') />");
		    h.append("  &nbsp;&nbsp;");
			h.append("  <img style=\"cursor:pointer\"" +icoZise +" src='"+contextPath+"/icons/img_sort-desc.png' href='#' onclick=sort('SEC_QTY','DESC') />");
		 }
		h.append("</th> \n");
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
	private static StringBuffer genRowTable(StockBean head,boolean excel,String[] columnNameArr,StockBean item) throws Exception{
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
		if( !Utils.isNull(head.getDispRequestDate()).equals("")){
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
				}
			}
		}
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
		h.append("</tr> \n");
		
		return h;
	}
	
	private static StringBuffer genTotalTable(StockBean head,boolean excel,String[] columnNameArr,double totalPriQty,double totalSecQty) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="hilight_text";
		String classNameNumber = "td_number";
		int colspan=0;
		if(excel){
			className ="colum_head";
			classNameNumber = "num_currency_bold";
		}
		
		h.append("<tr class='"+className+"'> \n");
		if( !Utils.isNull(head.getDispRequestDate()).equals("")){
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
