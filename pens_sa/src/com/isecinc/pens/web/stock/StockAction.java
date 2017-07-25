package com.isecinc.pens.web.stock;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.report.salesanalyst.helper.DBConnection;
import com.isecinc.pens.report.salesanalyst.helper.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockAction extends I_Action {

	public static int pageSize = 90;
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockForm aForm = (StockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().setAttribute("RESULTS",null);
				StockBean sales = new StockBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
					StockControlPage.prepareSearchCreditReport(request, conn, user,pageName);
					sales.setDispRequestDate("true");
					sales.setDispLastUpdate("true");
				}
				
				aForm.setBean(sales);
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
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
		logger.debug("search2");
		StockForm aForm = (StockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		String action = Utils.isNull(request.getParameter("action")); 
		try {
			logger.debug("search Head :pageName["+pageName+"]");
	
			  //Search Report
			if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
				if(action.equalsIgnoreCase("sort")){
					aForm.getBean().setColumnNameSort(Utils.isNull(request.getParameter("columnNameSort")));
					aForm.getBean().setOrderSortType(Utils.isNull(request.getParameter("orderSortType")));
				}else{
					//search new
					request.getSession().setAttribute("RESULTS",null);
					 aForm.getBean().setItemsList(null);
				}
				StockBean stockResult = StockReport.searchReport(request.getContextPath(),aForm.getBean(),false);
				StringBuffer resultHtmlTable = stockResult.getDataStrBuffer();
				if(resultHtmlTable != null){
					 request.getSession().setAttribute("RESULTS",resultHtmlTable);
					 foundData = true;
				}
				aForm.getBean().setItemsList(stockResult.getItemsList());
			}
			
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			   aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		StockForm aForm = (StockForm) form;
		StringBuffer resultHtmlTable =null;
		String pageName = aForm.getPageName();
		try {
			if(StockConstants.PAGE_CREDIT.equalsIgnoreCase(pageName)){
				StockBean stockResult = StockReport.searchReport(request.getContextPath(),aForm.getBean(),true);
				resultHtmlTable = stockResult.getDataStrBuffer();
			}
		    			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=data.xls");
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(resultHtmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		StockForm aForm = (StockForm) form;
		String pageName = aForm.getPageName();
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		StockForm aForm = (StockForm) form;
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
		StockForm orderForm = (StockForm) form;
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
