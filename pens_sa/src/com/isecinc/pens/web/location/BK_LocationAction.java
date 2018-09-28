package com.isecinc.pens.web.location;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.stock.StockBean;
import com.isecinc.pens.web.stock.StockConstants;
import com.isecinc.pens.web.stock.StockForm;
import com.isecinc.pens.web.stock.StockReport;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class BK_LocationAction extends I_Action {

	public static int pageSize = 90;
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		String action = Utils.isNull(request.getParameter("action")); 
		try {
			logger.debug("search Head :pageName["+pageName+"]");
	
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			   aForm.getBean().setItemsList(null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		LocationForm aForm = (LocationForm) form;
		try{
			if(Utils.isNull(request.getParameter("action")).equalsIgnoreCase("new")){
				/** Init Data ***/
			    LocationInitial.getInstance().initSession(request);
			    aForm.setBean(new LocationBean());
			    request.getSession().removeAttribute("CUST_LOC_LIST");
			    request.getSession().removeAttribute("RESULTS");
			 }
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return "search";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		LocationForm aForm = (LocationForm) form;
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
		LocationForm aForm = (LocationForm) form;
		User user = (User) request.getSession().getAttribute("user");
		List<LocationBean> allList = new ArrayList<LocationBean>();
		try {
			request.getSession().removeAttribute("CUST_LOC_LIST");
			request.getSession().removeAttribute("RESULTS");
			
			LocationBean bean = aForm.getBean();
			
			if("MAP".equalsIgnoreCase(bean.getDispType())){
				//search Customer Location
				if( !Utils.isNull(bean.getDispAllStore()).equals("") ){
					allList.addAll(LocationDAO.searchCustomerLocationList(bean));
				}
				//search Check in Location
				if(   !"".equalsIgnoreCase(Utils.isNull(bean.getDispAllOrder())) 
				   || !"".equalsIgnoreCase(Utils.isNull(bean.getDispAllVisit()))){
					
				   allList.addAll(LocationDAO.searchCustomerCheckInMapList(bean));
				}
				if(allList != null && allList.size() >0){
				    request.getSession().setAttribute("CUST_LOC_LIST", allList);
				}else {
					request.setAttribute("Message", "ไม่พบข้อมูล");
				}

			}else if("DATA".equalsIgnoreCase(bean.getDispType())){
				boolean excel = false;
				StringBuffer html = LocationReport.searchCustomerCheckInDataList(bean,excel);
				if(html.length() >0){
			       request.getSession().setAttribute("RESULTS", html);
				}else{
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				}
			}// oracle
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}finally{}
		return "search";
	}
	
	public ActionForward exportReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug("exportReport : ");
		User user = (User) request.getSession().getAttribute("user");
		LocationForm aForm = (LocationForm) form;
		StringBuffer resultHtmlTable = null;
		String pageName = aForm.getPageName();
		try {
			boolean excel = true;
			resultHtmlTable = LocationReport.searchCustomerCheckInDataList(aForm.getBean(),excel);
		    			
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
			e.printStackTrace();
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return null;
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
