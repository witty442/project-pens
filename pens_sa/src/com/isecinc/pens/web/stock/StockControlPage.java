package com.isecinc.pens.web.stock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.salestarget.SalesTargetBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class StockControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	

	public static void prepareSearchCreditReport(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		String salesrepCode = "";
		try{
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				salesrepCode = user.getUserName().toUpperCase();
			}
			
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", initPeriod(conn));
			
			//DISP_TYPE_LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","แสดงผลการนับสต๊อก","pri_qty,sec_qty"));
		/*	dataList.add(new PopupBean("reportType"," แสดงผลรวมนับสต๊อก + ยอดสั่งซื้อ","pri_qty,sec_qty,order_qty"));*/
			request.getSession().setAttribute("DISP_TYPE_LIST",dataList);
			
			//REPORT_TYPE_LIST
			/* - SKU		
			 - ร้านค้า , SKU		
			 - พนักงานขาย , ร้านค้า , SKU		
			 - ภาค , แบรนด์ , SKU		
			 - ภาค , พนักงานขาย, แบรนด์ , SKU		
            */
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","SKU","ITEM_NO"));
			dataList.add(new PopupBean("reportType","ร้านค้า,SKU","CUSTOMER_NUMBER,ITEM_NO"));
			dataList.add(new PopupBean("reportType","พนักงานขาย,ร้านค้า,SKU","SALES_CODE,CUSTOMER_NUMBER,ITEM_NO"));
			dataList.add(new PopupBean("reportType","ภาค,แบรนด์,SKU","REGION,BRAND,ITEM_NO"));
			dataList.add(new PopupBean("reportType","ภาค,พนักงานขาย,แบรนด์,SKU","REGION,SALES_CODE,BRAND,ITEM_NO"));
			
			dataList.add(new PopupBean("reportType","ภาคตามสายดูแล,แบรนด์,SKU","ZONE,BRAND,ITEM_NO"));
			dataList.add(new PopupBean("reportType","ภาคตามสายดูแล,พนักงานขาย,แบรนด์,SKU","ZONE,SALES_CODE,BRAND,ITEM_NO"));
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			
			List<PopupBean> dataTempList = searchCustCatNoListModel(conn,StockConstants.PAGE_STOCK_CREDIT, "");
			if(dataTempList != null &&dataTempList.size() ==1){
			  dataList.addAll(dataTempList);
			}else{
			  dataList.add(item);
			  dataList.addAll(dataTempList);
			}
			request.getSession().setAttribute("CUST_CAT_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn,user);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
			//SALESZONE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			dataList.add(item);
			
			List<PopupBean> salesZoneList =searchSalesZoneListModel(conn,salesrepCode);
			dataList.addAll(salesZoneList);
			request.getSession().setAttribute("SALES_ZONE_LIST",dataList);
			
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","","",salesrepCode);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void prepareSearchCreditExpireReport(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		String salesrepCode = "";
		try{
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				salesrepCode = user.getUserName().toUpperCase();
			}
	
			//TYPE_SEARCH_LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","แสดงสินค้าใกล้หมดอายุ","REPORT_PRODUCT_EXPIRE"));
			//dataList.add(new PopupBean("reportType","แสดงสินค้าที่ไม่ได้ตรวจนับ ","REPORT_PRODUCT_NO_CHECK_STOCK"));
			request.getSession().setAttribute("TYPE_SEARCH_LIST",dataList);
			
			//REPORT_TYPE_LIST
			dataList = new ArrayList<PopupBean>();
			dataList.add(new PopupBean("reportType","ร้านค้า,SKU","CUSTOMER_NUMBER,ITEM_NO"));
			dataList.add(new PopupBean("reportType","พนักงานขาย,ร้านค้า,SKU","SALES_CODE,CUSTOMER_NUMBER,ITEM_NO"));
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			
			List<PopupBean> dataTempList = searchCustCatNoListModel(conn,StockConstants.PAGE_STOCK_CREDIT, "");
			if(dataTempList != null &&dataTempList.size() ==1){
			  dataList.addAll(dataTempList);
			}else{
			  dataList.add(item);
			  dataList.addAll(dataTempList);
			}
			request.getSession().setAttribute("CUST_CAT_LIST",dataList);
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn,user);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
			//SALESZONE_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesZone("");
			item.setSalesZoneDesc("");
			dataList.add(item);
			
			List<PopupBean> salesZoneList =searchSalesZoneListModel(conn,salesrepCode);
			dataList.addAll(salesZoneList);
			request.getSession().setAttribute("SALES_ZONE_LIST",dataList);
			
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","","",salesrepCode);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void prepareSearchStockCloseVanReport(HttpServletRequest request,Connection conn,User user,String pageName){

		try{
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", initPeriodStockCloseVan(conn));
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	public static void prepareSearchStockPDVanReport(HttpServletRequest request,Connection conn,User user,String pageName){
		try{
			//init periodList
			request.getSession().setAttribute("PERIOD_LIST", initPeriodStockCloseVan(conn));
			
			//init pdList
			request.getSession().setAttribute("PD_LIST", initPDLISTVan(conn,user.getUserName()));
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static List<PopupBean> initPeriod(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		PopupBean item = new PopupBean();
		String startDate = "";
		String endDate ="";
		StringBuffer sql = new StringBuffer("");
		Statement stmt = null;
		ResultSet rst = null;
		Date requestDate = null;
		try{
			sql.append("select distinct r ,r2 from( \n");
			sql.append("select to_char(request_date,'mm/yyyy') as r \n"
					+ ", to_number(to_char(request_date,'yyyymm') ) as r2 \n"
					+ " from xxpens_om_check_order_v \n"
					+ " ) order by r2 desc \n");
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				item = new PopupBean();
				requestDate = DateUtil.parse("01/"+rst.getString("r"), DateUtil.DD_MM_YYYY_WITH_SLASH);
				cal.setTime(requestDate);
				logger.debug("Cal:"+cal.getTime());
				periodName =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				logger.debug("period:"+periodName);
		        startDate  =  "01-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy");
		        endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"-"+DateUtil.stringValue(cal.getTime(),"MMM-yyyy");
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+startDate +"|"+endDate);
				monthYearList.add(item);	
				
				cal = Calendar.getInstance();
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return monthYearList;
	}
	public static List<PopupBean> initPDLISTVan(Connection conn,String salesrepCode){
		List<PopupBean> pdList = new ArrayList<PopupBean>();
		PopupBean item = new PopupBean();
		StringBuffer sql = new StringBuffer("");
		Statement stmt = null;
		ResultSet rst = null;
		try{
			sql.append("select distinct s.subinventory ,a.description from \n");
			sql.append("apps.xxpens_inv_subinv_access s \n");
			sql.append(",apps.mtl_secondary_inventories a \n");
			sql.append("where s.code ='"+salesrepCode+"' \n");
			sql.append("and a.secondary_inventory_name = s.subinventory \n");
			sql.append("and a.organization_id = 84 \n");
			sql.append("and a.attribute3 = 'สร' \n");
			   
			logger.debug("sql:"+sql.toString());
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				item = new PopupBean();
				item.setPdCode(Utils.isNull(rst.getString("subinventory")));
				item.setPdDesc(Utils.isNull(rst.getString("subinventory"))+"-"+Utils.isNull(rst.getString("description")));
				pdList.add(item);	
			}
			logger.debug("pdList size:"+pdList.size());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pdList;
	}
	public static List<PopupBean> initPeriodStockCloseVan(Connection conn){
		List<PopupBean> monthYearList = new ArrayList<PopupBean>();
		Calendar cal = Calendar.getInstance();
		String periodName = "";
		String periodValue = "";
		PopupBean item = new PopupBean();
		SalesTargetBean period = null;
		try{
			int day = cal.get(Calendar.DAY_OF_MONTH);
			//if(day >1){ //For TEST
			
			/*if(day>=27){ //prod
				//Next Month +2
				item = new PopupBean();
				cal.add(Calendar.MONTH, 2);//Current+1
				periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				period = getPeriodList(conn,periodName).get(0);//get Period View
				item.setKeyName(periodName);
				item.setValue(periodName+"|"+period.getStartDate() +"|"+period.getEndDate());
				monthYearList.add(item);
			}*/
			
			//Cur Month
			item = new PopupBean();
			cal = Calendar.getInstance();
			periodValue =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
			item.setKeyName(periodName);
			item.setValue(periodValue);
			monthYearList.add(item);
			
			//Cur Month -1
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);//Current-1
			periodValue =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
			item.setKeyName(periodName);
			item.setValue(periodValue);
			monthYearList.add(item);
	
			//Cur Month -1
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -2);//Current-2
			periodValue =  DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
			item.setKeyName(periodName);
			item.setValue(periodValue);
			monthYearList.add(item);
			
			//Cur Month -1
			item = new PopupBean();
			cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -3);//Current-3
			periodValue = DateUtil.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
			periodName =  DateUtil.stringValue(cal.getTime(),"MMMMM-yyyy",DateUtil.local_th).toUpperCase();
			item.setKeyName(periodName);
			item.setValue(periodValue);
			monthYearList.add(item);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	 return monthYearList;
	}
	public static List<PopupBean> searchCustCatNoListModel(Connection conn,String pageName,String salesChannelNo){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct C.cust_cat_no,C.cust_cat_desc from XXPENS_BI_MST_CUST_CAT C  ");
			sql.append("\n  where 1=1 and c.cust_cat_desc is not null");
			if( !Utils.isNull(salesChannelNo).equals("")){
			  sql.append("\n  and C.sales_channel_no ='"+salesChannelNo+"'");
			}
			if(StockConstants.PAGE_STOCK_CREDIT.equalsIgnoreCase(pageName)){
				  sql.append("\n  and C.cust_cat_no ='ORDER - CREDIT SALES'");
			}
			sql.append("\n  ORDER BY C.cust_cat_no asc \n");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setCustCatNo(Utils.isNull(rst.getString("cust_cat_desc")));
				item.setCustCatDesc(Utils.isNull(rst.getString("cust_cat_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	
	public static List<PopupBean> searchSalesChannelListModel(Connection conn,User user){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and sales_channel_no in('0','1','2','3','4') ");
			
			//Case Sales Login filter show only salesrepCode 
			if(user.getRoleCRStock().equalsIgnoreCase(User.STOCKCRSALE)){
				sql.append("\n  and sales_channel_no ='"+user.getUserName().substring(1,2)+"'");
			}
			
			sql.append("\n  ORDER BY S.sales_channel_no asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesChannelNo(Utils.isNull(rst.getString("sales_channel_no")));
				item.setSalesChannelDesc(Utils.isNull(rst.getString("sales_channel_desc")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesZoneListModel(Connection conn,String salesrepCode){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.zone,S.zone_name from PENSBI.XXPENS_BI_MST_SALES_ZONE S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and zone in('0','1','2','3','4') ");
			if( !salesrepCode.equals("")){
				sql.append("\n  and S.salesrep_code  = '"+salesrepCode+"' ");
			}
			sql.append("\n  ORDER BY S.zone asc \n");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int no=0;
			while (rst.next()) {
				no++;
				PopupBean item = new PopupBean();
				item.setNo(no);
				item.setSalesZone(Utils.isNull(rst.getString("zone")));
				item.setSalesZoneDesc(Utils.isNull(rst.getString("zone_name")));
				pos.add(item);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo,String salesZone,String salesrepCode) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo,salesZone,salesrepCode);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo,String salesZone,String salesrepCode){
		PopupBean bean = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupBean> pos = new ArrayList<PopupBean>();
		try{
			sql.append("\n  SELECT distinct S.salesrep_code,S.salesrep_id from XXPENS_BI_MST_SALESREP S ");
			sql.append("\n  where 1=1 ");
			if( !Utils.isNull(custCatNo).equals("")){
				if( Utils.isNull(custCatNo).equalsIgnoreCase("CREDIT SALES")){
					sql.append("\n  and S.salesrep_code like 'S%' ");
					sql.append("\n  and S.salesrep_code not like 'SN%' ");
				}else if( Utils.isNull(custCatNo).equalsIgnoreCase("VAN SALES")){
					sql.append("\n  and S.salesrep_code like 'V%' ");
				}else{
					sql.append("\n  and S.salesrep_code not like 'SN%' ");
					sql.append("\n  and S.salesrep_code not like 'C%' ");
				}
			}else{
				sql.append("\n  and S.salesrep_code not like 'SN%' ");
				sql.append("\n  and S.salesrep_code not like 'C%' ");
			}
			if( !Utils.isNull(salesChannelNo).equals("")){
				sql.append("\n  and substr(salesrep_code,2,1)='"+Utils.isNull(salesChannelNo)+"'");
			}
			if( !Utils.isNull(salesZone).equals("")){
				sql.append("\n  and salesrep_code in(");
				sql.append("\n    select salesrep_code from pensbi.XXPENS_BI_MST_SALES_ZONE ");
				sql.append("\n    where zone = "+Utils.isNull(salesZone) );
				sql.append("\n  )");
			}
			if( !salesrepCode.equals("")){
				sql.append("\n  and S.salesrep_code  = '"+salesrepCode+"' ");
			}
			sql.append("\n  ORDER BY S.salesrep_code asc ");
			
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				 
				 //salesrepCode +=Utils.isNull(rst.getString("salesrep_code"))+",";
				 //salesrepId +=Utils.isNull(rst.getString("salesrep_id"))+",";
				 bean = new PopupBean();
				 bean.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				 bean.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				 
				 pos.add(bean);
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	 return pos;
	}
}
