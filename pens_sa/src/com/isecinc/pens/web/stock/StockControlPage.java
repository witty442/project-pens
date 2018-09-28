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

import util.DBConnection;
import util.Utils;

import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.salestarget.SalesTargetBean;

public class StockControlPage {
	protected static Logger logger = Logger.getLogger("PENS");
	

	public static void prepareSearchCreditReport(HttpServletRequest request,Connection conn,User user,String pageName){
		PopupBean item = null;
		List<PopupBean> dataList = null;
		try{
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
			request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
			
			//Cust Cat No List
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("");
			item.setCustCatDesc("");
			
			List<PopupBean> dataTempList = searchCustCatNoListModel(conn,StockConstants.PAGE_CREDIT, "");
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
			
			List<PopupBean> salesChannelList_s =searchSalesChannelListModel(conn);
			dataList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",dataList);
			
			//SALESREP_LIST
			//add Blank Row
			dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = searchSalesrepListAll(conn,"","");
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
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
				requestDate = Utils.parse("01/"+rst.getString("r"), Utils.DD_MM_YYYY_WITH_SLASH);
				cal.setTime(requestDate);
				logger.debug("Cal:"+cal.getTime());
				periodName =  Utils.stringValue(cal.getTime(),"MMM-yy").toUpperCase();
				logger.debug("period:"+periodName);
		        startDate  =  "01-"+Utils.stringValue(cal.getTime(),"MMM-yyyy");
		        endDate    =   cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"-"+Utils.stringValue(cal.getTime(),"MMM-yyyy");
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
			if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
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
	
	public static List<PopupBean> searchSalesChannelListModel(Connection conn){
		List<PopupBean> pos = new ArrayList<PopupBean>();
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n  SELECT distinct S.sales_channel_no ,S.sales_channel_desc from XXPENS_BI_MST_SALES_CHANNEL S ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  and sales_channel_no in('0','1','2','3','4') ");
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
	
	public static List<PopupBean> searchSalesrepListAll(String salesChannelNo,String custCatNo) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchSalesrepListAll(conn, salesChannelNo, custCatNo);
		}catch(Exception e){
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static List<PopupBean> searchSalesrepListAll(Connection conn,String salesChannelNo,String custCatNo){
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
