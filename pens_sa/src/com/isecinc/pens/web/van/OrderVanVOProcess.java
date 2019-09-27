package com.isecinc.pens.web.van;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.prodshow.ProdShowBean;
import com.isecinc.pens.web.prodshow.ProdShowDAO;
import com.isecinc.pens.web.prodshow.ProdShowForm;
import com.isecinc.pens.web.prodshow.ProdShowUtils;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockReport;
import com.isecinc.pens.web.stockonhand.StockOnhandBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class OrderVanVOProcess  {
	/** Logger */
	protected Logger logger = Logger.getLogger("PENS");
	public static int pageSize = 60;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		VanForm aForm = (VanForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			logger.debug("action:"+action);
			if("new".equals(action)){
				request.setAttribute("vanForm_RESULT",null);
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				VanBean bean = new VanBean();
				bean.setPeriodType("month");
				bean.setDispType("VO");
				bean.setReportType("summary");
				aForm.setBean(bean);
				
				//prepare Session List
				prepareSearchData(request, conn, user);
				
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("vanForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				VanBean bean = new VanBean();
				bean.setPeriodType("month");
				//bean.setTransDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		VanForm aForm = (VanForm) form;
		try {
			if(aForm.getBean().getReportType().equalsIgnoreCase("summary")){
				return  searchHeadBySummary(mapping, aForm, request, response);
			}else{
				return  searchHeadByDetail(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
		}
	}
	
	public ActionForward searchHeadBySummary(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHeadBySummary");
		VanForm aForm = (VanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		boolean excel = false,foundData=false;
		try {
			//save criteria case Back from detail
			aForm.setBeanCriteria(aForm.getBean());
			
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//clear session from result
			request.setAttribute("vanForm_RESULT",null);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(searchReportTotalRec(conn,aForm.getBean(),user));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    VanBean stockResult = searchReportBySummary(conn,user,aForm,excel,allRec,currPage,pageSize);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.setAttribute("vanForm_RESULT",resultHtmlTable);
					 foundData = true;
				}
				if(foundData==false){
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 request.setAttribute("vanForm_RESULT",null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    VanBean stockResult = searchReportBySummary(conn,user,aForm,excel,allRec,currPage,pageSize);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.setAttribute("vanForm_RESULT",resultHtmlTable);
					 foundData = true;
				}
				if(foundData==false){
					 request.setAttribute("Message", "ไม่พบข้อมูล");
					 request.setAttribute("vanForm_RESULT",null);
					// aForm.getBean().setItemsList(null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	public ActionForward searchHeadByDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHeadByDetail");
		VanForm aForm = (VanForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		boolean excel = false,foundData=false;
		try {
			//save criteria case Back from detail
			aForm.setBeanCriteria(aForm.getBean());
			
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//clear session from result
			request.setAttribute("vanForm_RESULT",null);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			//get Items Show by Page Size
		    VanBean stockResult = searchReportByDetail(conn,user,aForm,excel);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
			if(resultHtmlTable != null){
				 request.setAttribute("vanForm_RESULT",resultHtmlTable);
				 foundData = true;
			}
			if(foundData==false){
				 request.setAttribute("Message", "ไม่พบข้อมูล");
				 request.setAttribute("vanForm_RESULT",null);
				// aForm.getBean().setItemsList(null);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchDetail");
		VanForm aForm = (VanForm) form;
		boolean foundData = false;
		try {
			String orderNo = Utils.isNull(request.getParameter("orderNo"));
			logger.debug("orderNo:"+orderNo);
	
			request.setAttribute("vanForm_RESULT_DETAIL",null);
			
			VanBean stockResult = searchReportDetail(request.getContextPath(),orderNo,false);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
			if(resultHtmlTable != null){
				 request.setAttribute("vanForm_RESULT_DETAIL",resultHtmlTable);
				 foundData = true;
			}
			if(foundData==false){
				 request.setAttribute("Message", "ไม่พบข้อมูล");
				 request.setAttribute("vanForm_RESULT_DETAIL",null);
				// aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			throw e;
		}finally{
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportToExcel : ");
		VanForm aForm = (VanForm) form;
		boolean excel = true;
		Connection conn = null;
		User user = (User)request.getSession().getAttribute("user");
		VanBean stockResult = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			if(aForm.getBean().getReportType().equalsIgnoreCase("summary")){
				stockResult = searchReportBySummary(conn,user,aForm,excel,true,0,pageSize);
			}else{
				stockResult = searchReportByDetail(conn,user,aForm,excel);
			}
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
		    if(resultHtmlTable != null && resultHtmlTable.length() >0){
				
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(resultHtmlTable.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
		   }else{
			  request.setAttribute("Message","ไม่พบข้อมูล");
			  return mapping.findForward("search");
		   }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try{
				if(conn != null){
					conn.close();
				}
			}catch(Exception ee){}
		}
		return null;
	}
	
	public VanBean searchReportBySummary(Connection conn,User user,VanForm vanForm,boolean excel,boolean allRec,int currPage,int pageSize) {
		VanBean item = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		VanBean o = vanForm.getBean();
		try{
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n 	 select  ");
			sql.append("\n   H.order_number,H.order_date,H.customer_number, H.customer_name ");
			sql.append("\n  ,H.doc_status ,H.net_amount,H.creation_date ");
			sql.append("\n 	 from apps.xxpens_om_order_headers_temp H ");
			sql.append("\n 	 ,pensbi.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n  where H.salesrep_id= Z.salesrep_id");
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, o,user));
			
			sql.append("\n   ORDER BY H.order_number,H.order_date,H.customer_number asc");
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
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new VanBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTableBySummary(conn,vanForm,o,excel));
			  }
			  
			  item.setOrderNo(Utils.isNull(rst.getString("ORDER_NUMBER")));	
			  item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  item.setDocStatus("VO".equals(Utils.isNull(rst.getString("doc_status")))?"ยกเลิก":"ใช้งาน");
			  item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"), Utils.format_current_2_disgit));
			  item.setOrderCreateDate(DateUtil.stringValue(rst.getTimestamp("CREATION_DATE"), DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th));
			 
			  //get row table
			  html.append(genRowTable(o,excel,item));
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // gen end Table
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
			} catch (Exception e) {}
		}
	  return o;
	}
	
	public VanBean searchReportByDetail(Connection conn,User user,VanForm vanForm,boolean excel) {
		VanBean item = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		int r = 0;
		VanBean o = vanForm.getBean();
		try{
			sql.append("\n 	select  ");
			sql.append("\n  H.order_number,H.order_date,H.customer_number, H.customer_name ");
			sql.append("\n ,H.doc_status ,H.net_amount,H.creation_date ");
			sql.append("\n from apps.xxpens_om_order_headers_temp H ");
			sql.append("\n ,pensbi.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n where H.salesrep_id= Z.salesrep_id");
			
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, o,user));
			
			sql.append("\n ORDER BY H.order_number,H.order_date,H.customer_number asc");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new VanBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTableByDetail(conn,vanForm,o,excel));
			  }
			  
			  item.setOrderNo(Utils.isNull(rst.getString("ORDER_NUMBER")));	
			  item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  item.setDocStatus("VO".equals(Utils.isNull(rst.getString("doc_status")))?"ยกเลิก":"ใช้งาน");
			  item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"), Utils.format_current_2_disgit));
			  item.setOrderCreateDate(DateUtil.stringValue(rst.getTimestamp("CREATION_DATE"), DateUtil.DD_MM_YYYY_HH_MM_WITH_SLASH,DateUtil.local_th));
			  
			  html.append(genRowTableByDetail(excel,item,true));
			  //Get and gen detail by order
			  html.append(genDetailOrder(conn,item.getOrderNo(),excel));
			 
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // gen end Table
			  html.append("</table>");
			}
			o.setDataStrBuffer(html);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	public StringBuffer genDetailOrder(Connection conn,String orderNo,boolean excel){
		VanBean item = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = new StringBuffer("");
		int r = 0;
		try{
			sql.append("\n select  ");
			sql.append("\n  P.segment1 ,P.description ");
			sql.append("\n ,D.UOM_CODE,D.ORDERED_QUANTITY ,D.TOTAL_AMOUNT ");
			sql.append("\n from apps.xxpens_om_order_headers_temp H");
			sql.append("\n ,apps.xxpens_om_order_lines_temp D");
			sql.append("\n ,apps.xxpens_om_item_mst_v P");
			sql.append("\n where H.order_number = D.order_number ");
			sql.append("\n and D.product_id = P.inventory_item_id ");
			sql.append("\n and H.order_number ='"+orderNo+"'");
			sql.append("\n ORDER BY P.segment1  asc");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new VanBean();
			  r++;
			  item.setLineNumber(r);
			  item.setProductCode(Utils.isNull(rst.getString("segment1")));
			  item.setProductName(Utils.isNull(rst.getString("description")));
			  item.setUomCode(Utils.isNull(rst.getString("uom_code")));
			  item.setQty(Utils.decimalFormat(rst.getDouble("ORDERED_QUANTITY"), Utils.format_current_2_disgit));
			  item.setTotalAmount(Utils.decimalFormat(rst.getDouble("TOTAL_AMOUNT"), Utils.format_current_2_disgit));
			
			  html.append(genRowTableByDetail(excel,item,false));
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return html;
	}
	
	public int searchReportTotalRec(Connection conn,VanBean o,User user) {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try{
			sql.append("\n 	select count(*) as c "); 
			sql.append("\n 	from apps.xxpens_om_order_headers_temp H ,pensbi.XXPENS_BI_MST_SALES_ZONE Z ");
			sql.append("\n  where H.salesrep_id= Z.salesrep_id");
			//Gen Where Condition
			sql.append(genWhereCondSql(conn, o,user));
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
			 totalRec = rst.getInt("c");
			}//while
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return totalRec;
	}
	
	public VanBean searchReportDetail(String contextPath,String orderNo,boolean excel){
		VanBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StringBuffer html = null;
		VanBean bean = null;
		int r = 0;
		try{
			//create connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			sql.append("\n select  ");
			sql.append("\n  P.segment1 ,P.description ");
			sql.append("\n ,D.UOM_CODE,D.ORDERED_QUANTITY ,D.TOTAL_AMOUNT ");
			sql.append("\n from apps.xxpens_om_order_headers_temp H");
			sql.append("\n ,apps.xxpens_om_order_lines_temp D");
			sql.append("\n ,apps.xxpens_om_item_mst_v P");
			sql.append("\n where H.order_number = D.order_number ");
			sql.append("\n and D.product_id = P.inventory_item_id ");
			sql.append("\n and H.order_number ='"+orderNo+"'");
			sql.append("\n ORDER BY P.segment1  asc");
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new VanBean();
			  r++;
			  if(r==1){
				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTableDetail(contextPath,excel));
			  }
			  item.setLineNumber(r);
			  item.setProductCode(Utils.isNull(rst.getString("segment1")));
			  item.setProductName(Utils.isNull(rst.getString("description")));
			  item.setUomCode(Utils.isNull(rst.getString("uom_code")));
			  item.setQty(Utils.decimalFormat(rst.getDouble("ORDERED_QUANTITY"), Utils.format_current_2_disgit));
			  item.setNetAmount(Utils.decimalFormat(rst.getDouble("TOTAL_AMOUNT"), Utils.format_current_2_disgit));
			  
			  //get row table
			  html.append(genRowTableDetail(excel,item));
			  
			  //add to List
			  //itemList.add(item);
			}//while
			
			//Check Execute Found data
			if(r>0){
			  // gen end Table
			  html.append("</table>");
			  
			  bean = new VanBean();
			  bean.setDataStrBuffer(html);
			}
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
	  return bean;
	}
	
	public static StringBuffer genWhereCondSql(Connection conn,VanBean o,User user) throws Exception{
		StringBuffer sql = new StringBuffer("");
		//filter by user Login
		//check user login is map cust sales TT
		boolean isUserMapCustSalesTT = GeneralDAO.isUserMapCustSalesTT(user);
		if(isUserMapCustSalesTT){
			sql.append("\n  and H.salesrep_id in(");
			sql.append("\n    select Z.salesrep_id from PENSBI.XXPENS_BI_MST_CUST_CAT_MAP_TT M");
			sql.append("\n    ,pensbi.XXPENS_BI_MST_SALES_ZONE Z");
			sql.append("\n    where M.zone =Z.zone ");
			sql.append("\n    and M.user_name = '"+Utils.isNull(user.getUserName()) +"'");
			sql.append("\n  )");
		}
		
		if( Utils.isNull(o.getDispType()).equalsIgnoreCase("VO")){
			sql.append("\n and H.doc_status ='VO'");
		}
		if( !Utils.isNull(o.getCustCatNo()).equals("")){
			sql.append("\n and H.order_number Like '"+Utils.isNull(o.getCustCatNo())+"%'");
		}
		if( !Utils.isNull(o.getSalesChannelNo()).equals("")){
			sql.append("\n and substr(H.order_number,2,1) = '"+Utils.isNull(o.getSalesChannelNo())+"'");
		}
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and Z.salesrep_code = '"+Utils.isNull(o.getSalesrepCode())+"'");
		}
		if( !Utils.isNull(o.getBrand()).equals("")){
			sql.append("\n and D.brand = '"+Utils.isNull(o.getBrand())+"'");
		}
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and H.customer_number = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getSalesZone()).equals("")){
			sql.append("\n and Z.zone = "+Utils.isNull(o.getSalesZone()) );
		}
		
		if( !Utils.isNull(o.getStartDate()).equalsIgnoreCase("") && !Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			
			sql.append("\n and H.order_date >= to_date('"+startDateStr+"','dd/mm/yyyy')");
			sql.append("\n and H.order_date <= to_date('"+endDateStr+"','dd/mm/yyyy')");
		
		}else if(!Utils.isNull(o.getStartDate()).equalsIgnoreCase("")){
			Date startDate = DateUtil.parse(o.getStartDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String startDateStr = DateUtil.stringValue(startDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.order_date = to_date('"+startDateStr+"','dd/mm/yyyy')");
			
		}else if(!Utils.isNull(o.getEndDate()).equalsIgnoreCase("")){
			Date endDate = DateUtil.parse(o.getEndDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.order_date = to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
		return sql;
	}
	/**
	 * 
	 * @param columnNameArr
	 * @return
	 * @throws Exception
	 */
	private  StringBuffer genHeadTableBySummary(Connection conn,VanForm vanForm,VanBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		if(excel){
			int colspan=7;
			h.append(ExcelHeader.EXCEL_HEADER);
			
			h.append("<table id='tblProduct' align='center' border='1'> \n");
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
			h.append("</table> \n");
		}else{
			//Gen Paging
		   int totalPage = vanForm.getTotalPage();
		   int totalRecord = vanForm.getTotalRecord();
		   int currPage =  vanForm.getCurrPage();
		   int startRec = vanForm.getStartRec();
		   int endRec = vanForm.getEndRec();
		   int no = startRec;
			
		   h.append("<div align='left'> \n");
		   h.append("<span class='pagebanner'>รายการทั้งหมด  "+totalRecord+" รายการ, แสดงรายการที่  "+startRec+" ถึง  "+endRec+".</span>\n");
		   h.append("<span class='pagelinks'>\n");
		   h.append("หน้าที่ \n");
		   for(int r=0;r<totalPage;r++){
			 if(currPage ==(r+1)){
			    h.append("<strong>"+(r+1) +"</strong>\n");
			 }else{ 
				h.append("<a href='javascript:gotoPage("+(r+1)+")'" );
				h.append(" title='Go to page "+(r+1)+"'> "+(r+1)+"</a>\n");
		     }
		  }//for 
		   
		  h.append("</span>\n");
		  h.append("</div>\n");
		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append(" <th >เลขที่บิล</th> \n");
		h.append(" <th >วันที่</th>");
		h.append(" <th >รหัสร้านค้า</th>");
		h.append(" <th >ชื่อร้านค้า</th>");
		h.append(" <th >สถานะ</th>");
		h.append(" <th >Net Amount</th>");
		h.append(" <th >วันเวลาที่บันทึกบิลที่ Sales App</th>");
		if( !excel){
		h.append(" <th >ดูรายละเอียด</th>");
		}
		h.append("</tr> \n");
	
		return h;
	}
	private  StringBuffer genHeadTableByDetail(Connection conn,VanForm vanForm,VanBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		int colspan=7;
		if(excel){
			h.append(ExcelHeader.EXCEL_HEADER);
			
			h.append("<table id='tblProduct' align='center' border='1'> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+"><b> รายการบิลยกเลิก ที่ Sales appl.(แบบแสดงรายละเอียด)</b> </td>\n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("<td colspan="+colspan+">ประเภทขาย:Van Sales   | ภาคการขาย :"+GeneralDAO.getSalesChannelName(conn,head.getSalesChannelNo())+" </td>\n");
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
			h.append("</table> \n");
		}
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append(" <th >เลขที่บิล</th> \n");
		h.append(" <th >วันที่</th>");
		h.append(" <th >รหัสร้านค้า</th>");
		h.append(" <th >ชื่อร้านค้า</th>");
		h.append(" <th >สถานะ</th>");
		h.append(" <th >Net Amount</th>");
		h.append(" <th >วันเวลาที่บันทึกบิลที่ Sales App</th>");
		h.append("</tr> \n");
	
		return h;
	}
	
	private  StringBuffer genHeadTableDetail(String contextPath,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		String width="100%";
		h.append("<table id='tblProduct' align='center' border='1' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr> \n");
		h.append(" <th >Line </th> \n");
		h.append(" <th >Item</th>");
		h.append(" <th >Item Name</th>");
		h.append(" <th >UOM</th>");
		h.append(" <th >Qty</th>");
		h.append(" <th >Net Amount</th>");
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
	private  StringBuffer genRowTable(VanBean head,boolean excel,VanBean item) throws Exception{
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
		h.append("<td class='"+classText+"' width='5%'>"+item.getOrderNo()+"</td> \n");
		h.append("<td class='"+classText+"' width='5%'>"+item.getOrderDate()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='6%'>"+item.getCustomerCode()+"</td> \n");
		h.append("<td class='"+classText+"' width='15%'>"+item.getCustomerName()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getDocStatus()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='8%'>"+item.getNetAmount()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getOrderCreateDate()+"</td> \n");
		if( !excel){
		   h.append("<td class='"+classTextCenter+"' width='10%'><a href=javascript:viewDetail('"+item.getOrderNo()+"')>ดูรายละเอียด</a></td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	private  StringBuffer genRowTableByDetail(boolean excel,VanBean item,boolean genHeadOrder) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number";
		if(excel){
			classText = "text";
			classTextCenter = "text";
			classCurrency = "currency";
		}
		h.append("<tr> \n");
		if( genHeadOrder){
			classText = "td_text_bold";
			classTextCenter = "td_text_center_bold";
			classCurrency = "td_number_bold";
			if(excel){
				classText = "text_bold";
				classTextCenter = "text_bold";
				classCurrency = "currency_bold";
			}
			//Header order
			h.append("<td class='"+classText+"' width='5%'>"+item.getOrderNo()+"</td> \n");
			h.append("<td class='"+classText+"' width='5%'>"+item.getOrderDate()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='6%'>"+item.getCustomerCode()+"</td> \n");
			h.append("<td class='"+classText+"' width='15%'>"+item.getCustomerName()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getDocStatus()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='8%'>"+item.getNetAmount()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='8%'>"+item.getOrderCreateDate()+"</td> \n");
		}else{
			//item of Order
			h.append("<td class='"+classText+"' width='5%'></td> \n");
			h.append("<td class='"+classText+"' width='5%'></td> \n");
			h.append("<td class='"+classTextCenter+"' width='6%'>"+item.getProductCode()+"</td> \n");
			h.append("<td class='"+classText+"' width='15%'>"+item.getProductName()+"</td> \n");
			h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getUomCode()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='8%'>"+item.getQty()+"</td> \n");
			h.append("<td class='"+classCurrency+"' width='8%'>"+item.getTotalAmount()+"</td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	
	private  StringBuffer genRowTableDetail(boolean excel,VanBean item) throws Exception{
		StringBuffer h = new StringBuffer("");
		String classText = "td_text";
		String classTextCenter = "td_text_center";
		String classCurrency = "td_number";
		h.append("<tr> \n");
		if(excel){
			classText = "text";
			classCurrency = "currency";
		}
	
		h.append("<td class='"+classTextCenter+"' width='3%'>"+item.getLineNumber()+"</td> \n");
		h.append("<td class='"+classText+"' width='10%'>"+item.getProductCode()+"</td> \n");
		h.append("<td class='"+classText+"' width='15%'>"+item.getProductName()+"</td> \n");
		h.append("<td class='"+classTextCenter+"' width='5%'>"+item.getUomCode()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='8%'>"+item.getQty()+"</td> \n");
		h.append("<td class='"+classCurrency+"' width='8%'>"+item.getNetAmount()+"</td> \n");
		h.append("</tr> \n");
		return h;
	}
	public  void prepareSearchData(HttpServletRequest request,Connection conn,User user){
		try{
			//init monthYearList
			request.getSession().setAttribute("PERIOD_LIST", VanUtils.initPeriod(conn));
			
			//SALES_CHANNEL_LIST
			//add Blank Row
			List<PopupBean> salesChannelList = new ArrayList<PopupBean>();
			PopupBean item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			salesChannelList.add(item);
			
			List<PopupBean> salesChannelList_s = VanUtils.searchSalesChannelListModel(conn, user);
			salesChannelList.addAll(salesChannelList_s);
			request.getSession().setAttribute("SALES_CHANNEL_LIST",salesChannelList);
			
			//Cust Cat No List
			List<PopupBean> custCatNoList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setCustCatNo("V");
			item.setCustCatDesc("VAN SALES");
			custCatNoList.add(item);
			request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",custCatNoList);
			
			
			//SALESREP_LIST
			//add Blank Row
			List<PopupBean> dataList = new ArrayList<PopupBean>();
			item = new PopupBean();
			item.setSalesChannelNo("");
			item.setSalesChannelDesc("");
			dataList.add(item);
			
			List<PopupBean> salesrepList_s = VanUtils.searchSalesrepListAll(conn,"","C","",user);
			dataList.addAll(salesrepList_s);
			request.getSession().setAttribute("SALESREP_LIST",dataList);
			
			//SALES_ZONE_LIST
			//add Blank Row
			List<PopupBean> salesZoneList = new ArrayList<PopupBean>();
			item = new PopupBean();item.setSalesZone("");item.setSalesZoneDesc("");
			salesZoneList.add(item);
			
			List<PopupBean> salesZoneList_s = VanUtils.searchSalesZoneListModel(conn,user);
			salesZoneList.addAll(salesZoneList_s);
			request.getSession().setAttribute("SALES_ZONE_LIST",salesZoneList);
			
			//REPORT_TYPE_LIST
			List<PopupBean> itemList = new ArrayList<PopupBean>();
			item = new PopupBean();item.setKeyName("แบบสรุป");item.setValue("summary");
			itemList.add(item);
			item = new PopupBean();item.setKeyName("แบบแสดงรายละเอียด");item.setValue("detail");
			itemList.add(item);
			request.getSession().setAttribute("REPORT_TYPE_LIST",itemList);
			
			//DISP_TYPE_LIST
			itemList = new ArrayList<PopupBean>();
			item = new PopupBean();item.setKeyName("เฉพาะรายการยกเลิก");;item.setValue("VO");
			itemList.add(item);
			item = new PopupBean();item.setKeyName("แสดงทั้งหมด");item.setValue("ALL");
			itemList.add(item);
			request.getSession().setAttribute("DISP_TYPE_LIST",itemList);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
}
