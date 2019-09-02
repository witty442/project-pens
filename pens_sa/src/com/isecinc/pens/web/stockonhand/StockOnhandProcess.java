package com.isecinc.pens.web.stockonhand;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.ExcelHeader;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockReport;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockOnhandProcess extends I_Action {

	public static int pageSize = 99999;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			logger.debug("action:"+action);
			if("new".equals(action)){
				request.setAttribute("stockOnhandForm_RESULT",null);
				
				//init connection
				conn = DBConnection.getInstance().getConnectionApps();
				//clear session 
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setDispHaveQty("true");
				bean.setOrgCode("B00");
				bean.setSubInv("B001");
				//logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				aForm.setBean(bean);
				
				//init reportType List
				List<PopupBean> dataList = new ArrayList<PopupBean>();
				dataList.add(new PopupBean("reportType","SubInv , Brand , SKU","SUBINVENTORY_CODE,BRAND,SEGMENT1"));
				dataList.add(new PopupBean("reportType","SubInv , Brand","SUBINVENTORY_CODE,BRAND"));
				dataList.add(new PopupBean("reportType","Brand","BRAND"));
				dataList.add(new PopupBean("reportType","Brand , SKU","BRAND,SEGMENT1"));
				dataList.add(new PopupBean("reportType","SKU","SEGMENT1"));
				request.getSession().setAttribute("REPORT_TYPE_LIST",dataList);
	
			}else if("back".equals(action)){
				//clear session 
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.setResultsSearch(null);
				//prepare bean
				StockOnhandBean bean = new StockOnhandBean();
				bean.setDispHaveQty("true");
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
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		boolean foundData = false;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnection.getInstance().getConnectionApps();
			
			if(action.equalsIgnoreCase("sort")){
				aForm.getBean().setColumnNameSort(Utils.isNull(request.getParameter("columnNameSort")));
				aForm.getBean().setOrderSortType(Utils.isNull(request.getParameter("orderSortType")));
			}else{
				//search new
				request.setAttribute("stockOnhandForm_RESULT",null);
				aForm.getBean().setItemsList(null);
			}
			StockOnhandBean stockResult = StockOnhandReport.searchOnhandReport(request.getContextPath(),aForm.getBean(),false);
			StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
			if(resultHtmlTable != null){
				 request.setAttribute("stockOnhandForm_RESULT",resultHtmlTable);
				 foundData = true;
			}
			aForm.getBean().setItemsList(stockResult.getItemsList());
			
			if(foundData==false){
				 request.setAttribute("Message", "ไม่พบข้อมูล");
				 request.setAttribute("stockOnhandForm_RESULT",null);
				 aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
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
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		StockOnhandForm aForm = (StockOnhandForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		StockOnhandForm aForm = (StockOnhandForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportToExcel : ");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			StockOnhandBean stockResult = StockOnhandReport.searchOnhandReport(request.getContextPath(),aForm.getBean(),true);
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
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockOnhandForm orderForm = (StockOnhandForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
	}
	
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
