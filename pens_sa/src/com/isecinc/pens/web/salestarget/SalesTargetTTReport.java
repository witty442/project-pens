package com.isecinc.pens.web.salestarget;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;

import util.DBConnection;
import util.ExcelHeader;
import util.SQLHelper;
import util.UserUtils;
import util.Utils;

public class SalesTargetTTReport {
	protected static Logger logger = Logger.getLogger("PENS");
	private static Map<String, String> COLUMNNAME_MAP = new HashMap<String, String>();
	static{
		COLUMNNAME_MAP.put("BRAND", "แบรนด์");
		COLUMNNAME_MAP.put("CUSTOMER_CATEGORY", "ประเภทขาย");
		COLUMNNAME_MAP.put("SALES_CHANNEL", "ภาคการขาย");
		COLUMNNAME_MAP.put("SALES_ZONE", "ภาคตามการดูแล");
		COLUMNNAME_MAP.put("SALESREP_CODE", "พนักงานขาย");
		COLUMNNAME_MAP.put("CUSTOMER_CODE", "ร้านค้า");
		COLUMNNAME_MAP.put("INVENTORY_ITEM_CODE", "SKU");
	}
	
	public static StringBuffer searchReport(User user,SalesTargetBean o,boolean excel,String subPageName){
		return searchReportModel(user ,o, excel,subPageName);
	}
    public static StringBuffer searchReport(User user,SalesTargetBean o,String subPageName){
		return searchReportModel(user,o, false,subPageName);
	}
	public static StringBuffer searchReportModel(User user,SalesTargetBean o,boolean excel,String subPageName){
		SalesTargetBean s = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		String[] columnNameArr = null;
		StringBuffer html = null;
		Map<String, String> ROWVALUE_MAP = new HashMap<String, String>();
		Map<String, String> ROWDESC_MAP = new HashMap<String, String>();
		int r = 0;
		double totalQty =0;
		double totalAmount =0;
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			//Convert To Criteria
			o = SalesTargetDAO.convertCriteria(o);
			//split column arr
			columnNameArr = o.getReportType().split("\\,");
			
			sql.append("\n SELECT A.* FROM (");
			
			sql.append("\n  SELECT "+genSelectColumnName(columnNameArr));
			sql.append("\n  NVL(SUM(M.TARGET_QTY),0) as TARGET_QTY " );
			sql.append("\n ,NVL(SUM(M.TARGET_AMOUNT),0) as TARGET_AMOUNT ");
			sql.append("\n  FROM(");
			sql.append("\n    SELECT M.STATUS,M.sales_channel, M.CUSTOMER_CATEGORY ");
			sql.append("\n    , M.target_month,M.target_quarter,M.target_year ");
			sql.append("\n    , M.salesrep_code,M.brand ,customer_code");
			sql.append("\n    , (select zone from  PENSBI.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n       where Z.salesrep_id =M.salesrep_id ) as SALES_ZONE ");
			sql.append("\n    ,D.INVENTORY_ITEM_CODE ,D.TARGET_QTY ,D.TARGET_AMOUNT");
			sql.append("\n    FROM PENSBI.XXPENS_BI_SALES_TARGET_TEMP M  ");
			sql.append("\n    , PENSBI.XXPENS_BI_SALES_TARGET_TEMP_L D ");
			sql.append("\n    WHERE M.ID= D.ID ");
			sql.append("\n    AND D.TARGET_QTY <> 0 ");
			//subPageName TT show only van and credit
			if("TT".equalsIgnoreCase(subPageName)){
				sql.append("\n AND M.CUSTOMER_CATEGORY IN('ORDER - VAN SALES','ORDER - CREDIT SALES') ");
				//filter by user login
				if ( !"admin".equalsIgnoreCase(user.getUserName())
					&& !UserUtils.userInRoleSalesTarget(user, new String[]{User.MKT}) ){
					 sql.append("\n  and M.salesrep_id in( ");
					 sql.append("\n  select salesrep_id from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT ");
					 sql.append("\n  where  user_name='"+user.getUserName()+"'");
					 sql.append("\n  )");
				}
			}
			sql.append("\n )M ");
			sql.append("\n WHERE 1=1 ");
			sql.append("\n AND M.SALES_ZONE IN(0,1,2,3,4)");
			
			//sql.append("\n  AND M.STATUS ='"+SalesTargetConstants.STATUS_FINISH+"'");
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getStatus()), " and M.STATUS ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesChannelNo()), " and M.sales_channel ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getCustCatNo()), " and M.CUSTOMER_CATEGORY ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetMonth()), " and M.target_month ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetQuarter()), " and M.target_quarter ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetYear()), " and M.target_year ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesrepCode()), " and M.salesrep_code ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesZone()), " and M.sales_zone ="));
			sql.append(SQLHelper.genStrArrCondChkNull(Utils.isNull(o.getBrand()), " and M.brand in "));
			sql.append(SQLHelper.genStrArrCondChkNull(Utils.isNull(o.getCustomerCode()), " and M.customer_code in"));
			
			sql.append("\n GROUP BY "+o.getReportType());
			
			
			/*** Sales MKT TT **/
			//XXPENS_BI_SALES_TARGET_TT not in XXPENS_BI_SALES_TARGET_TEMP 
			sql.append("\n\n UNION ALL ");
			
			sql.append("\n  SELECT "+genSelectColumnName(columnNameArr));
			sql.append("\n  NVL(SUM(M.TARGET_QTY),0) as TARGET_QTY " );
			sql.append("\n ,NVL(SUM(M.TARGET_AMOUNT),0) as TARGET_AMOUNT ");
			sql.append("\n  FROM(");
			sql.append("\n    SELECT M.STATUS,'' as sales_channel, M.CUSTOMER_CATEGORY ");
			sql.append("\n    , M.target_month,M.target_quarter ,M.target_year ");
			sql.append("\n    , '' as salesrep_code, M.brand ,'' as customer_code");
			sql.append("\n    , M.ZONE as SALES_ZONE ");
			sql.append("\n    ,D.INVENTORY_ITEM_CODE ,D.TARGET_QTY ,D.TARGET_AMOUNT");
			sql.append("\n    FROM PENSBI.XXPENS_BI_SALES_TARGET_TT M  ");
			sql.append("\n    ,PENSBI.XXPENS_BI_SALES_TARGET_TT_L D ");
			sql.append("\n    WHERE M.ID = D.ID ");
			sql.append("\n    AND M.status IN('"+SalesTargetConstants.STATUS_POST+"','"+SalesTargetConstants.STATUS_REJECT+"')");
			sql.append("\n    AND (M.period ,M.CUSTOMER_CATEGORY,M.brand ,M.ZONE) ");
			sql.append("\n         NOT IN (  ");
			sql.append("\n                SELECT M.period ,M.CUSTOMER_CATEGORY,M.brand,Z.ZONE");
			sql.append("\n                FROM PENSBI.XXPENS_BI_SALES_TARGET_TEMP M"); 
			sql.append("\n                , PENSBI.XXPENS_BI_MST_SALES_ZONE Z"); 
			sql.append("\n                WHERE M.SALESREP_ID = Z.SALESREP_ID   "); 
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetMonth()), "\t\t  and M.target_month ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetQuarter()), "\t\t  and M.target_quarter ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetYear()), "\t\t  and M.target_year ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesZone()), "\t\t  and Z.zone ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getCustCatNo()), "\t\t and M.CUSTOMER_CATEGORY ="));
			sql.append("\n                )  ");
			sql.append("\n )M ");
			sql.append("\n WHERE 1=1 ");
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getStatus()), " and M.STATUS ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesChannelNo()), " and M.sales_channel ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getCustCatNo()), " and M.CUSTOMER_CATEGORY ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetMonth()), " and M.target_month ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetQuarter()), " and M.target_quarter ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getTargetYear()), " and M.target_year ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesrepCode()), " and M.salesrep_code ="));
			sql.append(SQLHelper.genStrCondChkNull(Utils.isNull(o.getSalesZone()), " and M.sales_zone ="));
			sql.append(SQLHelper.genStrArrCondChkNull(Utils.isNull(o.getBrand()), " and M.brand in "));
			sql.append(SQLHelper.genStrArrCondChkNull(Utils.isNull(o.getCustomerCode()), " and M.customer_code in"));
			
			sql.append("\n GROUP BY "+o.getReportType());
			
			sql.append("\n )A ");
			sql.append("\n ORDER BY "+o.getReportType());
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  s = new SalesTargetBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(excel,columnNameArr));
			  }
			  for(int i=0;i<columnNameArr.length;i++){
				  logger.debug("columnName:"+columnNameArr[i]);
				  ROWVALUE_MAP.put(columnNameArr[i], Utils.isNull(rst.getString(columnNameArr[i])) );
				  
				  ROWDESC_MAP.put(columnNameArr[i]+"_NAME", Utils.isNull(rst.getString(columnNameArr[i]+"_NAME")) );
			  }//for
			  
			  s.setTargetQty(Utils.decimalFormat(rst.getDouble("TARGET_QTY"), Utils.format_current_no_disgit)); 
			  s.setTargetAmount(Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit)); 
			  
			  totalQty +=rst.getDouble("TARGET_QTY");
			  totalAmount += rst.getDouble("TARGET_AMOUNT");
			  
			  //Gen Row Table
			  html.append(genRowTable(excel,columnNameArr,ROWVALUE_MAP,ROWDESC_MAP,s));
			}//while
			
			//Check Execute Found data
			if(r>0){
			  //Get Total
			  html.append(genTotalTable(excel,columnNameArr, totalQty,totalAmount));
			  //gen end Table
			  html.append("</table>");
			}
		}catch(Exception e){
			//logger.error(e.getMessage(),e);
			e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return html;
	}
	
	public static StringBuffer searchReportAll(SalesTargetBean o){
		if(true){
			//type 2 Vy Customer
			return searchReportAllByCust(o);
		}else{
			//type 1
		}
		return null;
	}

	public static StringBuffer searchReportAllByCust(SalesTargetBean o){
		SalesTargetBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		int i=0;
		double totalQty =0;
		double totalAmount =0;
		String keyMap = "";
		List<SalesTargetBean> dataList = new ArrayList<SalesTargetBean>();
		try{
			//create connection
			conn = DBConnection.getInstance().getConnection();
			//Convert To Criteria
			o = SalesTargetDAO.convertCriteria(o);
			
			//Get Customer Map 
			Map<String, SalesTargetBean> customerListDataMap = getCustomerListToMap(conn, o);
			
			//Get CustList
			List<SalesTargetBean> customerList = getCustomerList(conn, o);
			
			sql.append("\n  SELECT INVENTORY_ITEM_CODE,");
			sql.append("\n  NVL(SUM(TARGET_QTY),0) as TARGET_QTY , NVL(SUM(TARGET_AMOUNT),0) as TARGET_AMOUNT ");
			sql.append("\n  FROM XXPENS_BI_SALES_TARGET M WHERE 1=1 ");
			/** Gen Where SQl **/
			sql.append( genWhereCaseSearchReportAllByCust(o));
			sql.append("\n GROUP BY "+o.getReportType());
			sql.append("\n ORDER BY "+o.getReportType());
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			List<SalesTargetBean> customerItemList = null;
			while (rst.next()) {
			   r++;
			   item.setItemCode(Utils.isNull(rst.getString("INVENTORY_ITEM_CODE")));
			   item.setTargetQty(Utils.decimalFormat(rst.getDouble("TARGET_QTY"), Utils.format_current_no_disgit)); 
			   item.setTargetAmount(Utils.decimalFormat(rst.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit)); 
			  
			   if(customerList != null){
				  customerItemList = new ArrayList<SalesTargetBean>();
				  //Set Data To CustomerItemList
				  for(i=0;i<customerList.size();i++){
					  SalesTargetBean cust = customerItemList.get(i);
					  keyMap = item.getItemCode()+"_"+cust.getCustomerCode();
					  
					  SalesTargetBean itemData = customerListDataMap.get(keyMap)!=null?(SalesTargetBean)customerListDataMap.get(keyMap):null;
						if(itemData!= null){
							itemData.setCustomerCode(cust.getCustomerCode());
							itemData.setItemName(cust.getCustomerName());
							//System.out.println("StoreCode["+order.getStoreCode()+"]OrderNo["+order.getOrderNo()+"]item["+order.getItem()+"]qty["+order.getQty()+"]");
	
							//lineQty += Utils.convertStrToInt(order.getQty());
						}
						customerItemList.add(itemData);
				  }//for
			  }//if
			  
			  item.setItemsList(customerItemList);
			  dataList.add(item);
			}//while
			
			//gen result html
			html = genResultHtmlCaseSearchReportAllByCust(customerList,customerListDataMap,dataList);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return html;
	}
	
	private static StringBuffer genResultHtmlCaseSearchReportAllByCust(List<SalesTargetBean> customerList
			,Map<String, SalesTargetBean> customerListDataMap,List<SalesTargetBean> dataList){
		StringBuffer html = new StringBuffer("");
		
		return html;
	}
	
	private static Map<String,SalesTargetBean> getCustomerListToMap(Connection conn,SalesTargetBean o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		Map<String,SalesTargetBean> map = new HashMap<String,SalesTargetBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("\n  select M.CUSTOMER_CODE " );
			sql.append("\n  ,(select X.CUSTOMER_DESC FROM XXPENS_BI_MST_CUSTOMER X WHERE X.CUSTOMER_ID = M.CUSTOMER_ID ) as CUSTOMER_DESC " );
			sql.append("\n  , M.INVENTORY_ITEM_CODE " );
			sql.append("\n  ,(select X.INVENTORY_ITEM_DESC FROM XXPENS_BI_MST_ITEM X WHERE X.INVENTORY_ITEM_ID=M.INVENTORY_ITEM_ID) AS INVENTORY_ITEM_DESC" );
			sql.append("\n  , M.price ,M.target_qty,M.target_amount  " );
			sql.append("\n  FROM XXPENS_BI_SALES_TARGET M WHERE 1=1  ");
			sql.append(genWhereCaseSearchReportAllByCust(o));
			
		    logger.debug("SQL:"+sql.toString());
		    
		   /* logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeCode["+storeCode+"]");
		    logger.debug("item["+item+"]");*/
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			String keyMap = "";
			while(rs.next()){
				SalesTargetBean h = new SalesTargetBean();
				h.setCustomerCode(Utils.isNull(rs.getString("CUSTOMER_CODE")));
				h.setCustomerName(Utils.isNull(rs.getString("CUSTOMER_DESC")));
				h.setItemCode(Utils.isNull(rs.getString("INVENTORY_ITEM_CODE")));
				h.setItemName(Utils.isNull(rs.getString("INVENTORY_ITEM_DESE")));
				h.setPrice(Utils.decimalFormat(rs.getDouble("price"), Utils.format_current_2_disgit));
				h.setTotalTargetQty(Utils.decimalFormat(rs.getDouble("TARGET_QTY"), Utils.format_current_no_disgit));
				h.setTotalTargetAmount(Utils.decimalFormat(rs.getDouble("TARGET_AMOUNT"), Utils.format_current_2_disgit));
				
				keyMap = h.getItemCode()+"_"+h.getCustomerCode();
				map.put(keyMap, h);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return map;
	} 
	
	private static List<SalesTargetBean> getCustomerList(Connection conn,SalesTargetBean o) throws Exception{
		PreparedStatement ps =null;
		ResultSet rs = null;
		List<SalesTargetBean> custList = new ArrayList<SalesTargetBean>();
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("\n  select DISTINCT M.CUSTOMER_CODE " );
			sql.append("\n  ,(select X.CUSTOMER_DESC FROM XXPENS_BI_MST_CUSTOMER X WHERE X.CUSTOMER_ID = M.CUSTOMER_ID ) as CUSTOMER_DESC " );
			sql.append("\n  FROM XXPENS_BI_SALES_TARGET M WHERE 1=1  ");
			sql.append(genWhereCaseSearchReportAllByCust(o));

		    logger.debug("SQL:"+sql.toString());
		    
		   /* logger.debug("orderDate["+orderDate+"]");
		    logger.debug("storeCode["+storeCode+"]");
		    logger.debug("item["+item+"]");*/
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				SalesTargetBean h = new SalesTargetBean();
				h.setCustomerCode(Utils.isNull(rs.getString("CUSTOMER_CODE")));
				h.setCustomerName(Utils.isNull(rs.getString("CUSTOMER_DESC")));
				custList.add(h);
			}
		}catch(Exception e){
	      throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			
		}
		return custList;
	} 
	
	private static StringBuilder genWhereCaseSearchReportAllByCust(SalesTargetBean o){
		StringBuilder sql = new StringBuilder();
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and M.sales_channel = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getCustCatNo()).equals("")){
			sql.append("\n and M.CUSTOMER_CATEGORY = '"+Utils.isNull(o.getCustCatNo())+"'");
		}
		if( !Utils.isNull(o.getTargetMonth()).equals("")){
			sql.append("\n and M.target_month = '"+Utils.isNull(o.getTargetMonth())+"'");
		}
		if( !Utils.isNull(o.getTargetQuarter()).equals("")){
			sql.append("\n and M.target_quater = '"+Utils.isNull(o.getTargetQuarter())+"'");
		}
		if( !Utils.isNull(o.getTargetYear()).equals("")){
			sql.append("\n and M.target_year = '"+Utils.isNull(o.getTargetYear())+"'");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and M.brand = '"+Utils.isNull(o.getBrand())+"'");
		}
		return sql;
	}
	
	private static String genSelectColumnName(String[] columnNameArr) throws Exception{
		String sql = "";
		for(int i=0;i<columnNameArr.length;i++){
		  if("Brand".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.brand ,";
			  sql +="\n (select x.brand_desc from PENSBI.XXPENS_BI_MST_BRAND X WHERE X.brand_no = M.brand) as BRAND_NAME ,";
		  }else  if("CUSTOMER_CATEGORY".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.CUSTOMER_CATEGORY ,";
			  sql +="\n (select x.cust_cat_desc from PENSBI.XXPENS_BI_MST_CUST_CAT X WHERE X.cust_cat_no = M.CUSTOMER_CATEGORY) as CUSTOMER_CATEGORY_NAME ,";
		  }else if("SALES_CHANNEL".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SALES_CHANNEL ,";
			  sql +="\n (select x.SALES_CHANNEL_DESC from PENSBI.XXPENS_BI_MST_SALES_CHANNEL X WHERE X.SALES_CHANNEL_NO = M.SALES_CHANNEL) as SALES_CHANNEL_NAME ,";
		  }else if("SALESREP_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SALESREP_CODE ,";
			  sql +="\n (select x.SALESREP_DESC from PENSBI.XXPENS_BI_MST_SALESREP X WHERE X.SALESREP_CODE = M.SALESREP_CODE) as SALESREP_CODE_NAME ,";
		  }else if("CUSTOMER_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.CUSTOMER_CODE  ,";
			  sql +="\n (select x.customer_desc from PENSBI.XXPENS_BI_MST_CUSTOMER X WHERE X.CUSTOMER_CODE = M.CUSTOMER_CODE) as CUSTOMER_CODE_NAME ,";
		  }else if("INVENTORY_ITEM_CODE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.INVENTORY_ITEM_CODE ,";
			  sql +="\n (select x.INVENTORY_ITEM_DESC from PENSBI.XXPENS_BI_MST_ITEM X WHERE X.INVENTORY_ITEM_CODE = M.INVENTORY_ITEM_CODE) as INVENTORY_ITEM_CODE_NAME ,";
		  }else if("SALES_ZONE".equalsIgnoreCase(columnNameArr[i])){
			  sql +="\n M.SALES_ZONE ,";
			  sql +="\n (select max(x.ZONE_NAME) from PENSBI.XXPENS_BI_MST_SALES_ZONE X WHERE X.ZONE = M.SALES_ZONE) as SALES_ZONE_NAME ,";
		  }//if
		}//for
	  return sql;
	}
	
	/**
	 * 
	 * @param customerList
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTableCaseSearchReportAllByCust(List<SalesTargetBean> customerList) throws Exception{
		StringBuffer h = new StringBuffer("");
	//	html.append(ExcelHeader.EXCEL_HEADER);
		h.append("<table id='tblProduct' align='center' border='1' width='100%' cellpadding='3' cellspacing='1' class='tableSearch'> \n");
		h.append("<tr> \n");
		h.append("<th >รหัสสินค้า</th> \n");
		h.append("<th >ชื่อสินค้า</th> \n");
		for(int i=0;i<customerList.size();i++){
			SalesTargetBean c = customerList.get(i);
		   h.append("<th >"+c.getCustomerCode()+"</th> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genHeadTable(boolean excel,String[] columnNameArr) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
		}
		String width="100%";
		if(columnNameArr.length<2){
			width="60%";
		}
	//	html.append(ExcelHeader.EXCEL_HEADER);
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		for(int i=0;i<columnNameArr.length;i++){
		   h.append("<th >"+COLUMNNAME_MAP.get(columnNameArr[i])+"</th> \n");
		}
		h.append("<th >เป้า (หีบ)</th> \n");
		h.append("<th >เป้า (บาท)</th> \n");
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
	private static StringBuffer genRowTable(boolean excel,String[] columnNameArr,Map<String, String> ROWVALUE_MAP,Map<String, String> ROWDESC_MAP,SalesTargetBean o) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="";
		String classNameNumber = "td_number";
		h.append("<tr> \n");
		for(int i=0;i<columnNameArr.length;i++){
			//logger.debug("columnName["+columnNameArr[i]+"]");
			if(excel){
				className = "text";
				className = "num_currency";
			}else{
				if("INVENTORY_ITEM_CODE".equalsIgnoreCase(columnNameArr[i])
				 || "SALESREP_ID".equalsIgnoreCase(columnNameArr[i])
				 || "CUSTOMER_CODE".equalsIgnoreCase(columnNameArr[i])
				 || "SALESREP_CODE".equalsIgnoreCase(columnNameArr[i])
				 ){
					className ="td_text";
				}
			}
			if( "CUSTOMER_CATEGORY".equalsIgnoreCase(columnNameArr[i])
				|| "SALES_CHANNEL".equalsIgnoreCase(columnNameArr[i])
			 ){
			   h.append("<td class='"+className+"'>"+ROWDESC_MAP.get(columnNameArr[i]+"_NAME")+"</td> \n");
			}else{
			   h.append("<td class='"+className+"'>"+ROWVALUE_MAP.get(columnNameArr[i])+"-"+ROWDESC_MAP.get(columnNameArr[i]+"_NAME")+"</td> \n");
			}
		}
		h.append("<td class='"+classNameNumber+"'>"+o.getTargetQty()+"</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+o.getTargetAmount()+"</td> \n");
		h.append("</tr> \n");
		
		return h;
	}
	
	private static StringBuffer genTotalTable(boolean excel,String[] columnNameArr,double totalQty,double totalAmount) throws Exception{
		String className ="hilight_text";
		String classNameNumber = "td_number";
		if(excel){
			className ="colum_head";
			classNameNumber = "num_currency_bold";
		}
		StringBuffer h = new StringBuffer("");
		h.append("<tr class='"+className+"'> \n");
		h.append("<td colspan='"+columnNameArr.length+"' align='right'>Total</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalQty, Utils.format_current_no_disgit)+"</td> \n");
		h.append("<td class='"+classNameNumber+"'>"+Utils.decimalFormat(totalAmount, Utils.format_current_2_disgit)+"</td> \n");
		h.append("</tr> \n");
		
		return h;
	}
	
}
