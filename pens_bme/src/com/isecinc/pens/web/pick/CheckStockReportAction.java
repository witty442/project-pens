package com.isecinc.pens.web.pick;

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
import com.isecinc.pens.bean.CheckStockReportBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.CheckStockReportDAO;
import com.isecinc.pens.dao.constants.ControlConstantsDB;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class CheckStockReportAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "";
		CheckStockReportForm aForm = (CheckStockReportForm) form;
		try {
			//Clear OLD Session
			request.getSession().removeAttribute("RESULT_SUMMARY");
			request.getSession().removeAttribute("RESULT_DETAIL");
			
			String action = Utils.isNull(request.getParameter("action"));
			String pageName = Utils.isNull(request.getParameter("pageName"));
			String warehouse = Utils.isNull(request.getParameter("warehouse"));
			
			if("new".equalsIgnoreCase(action) && pageName.equalsIgnoreCase("summary")){
				CheckStockReportBean bean = new CheckStockReportBean();
				//Get Config W2_CK_STK_INITDATE
				String initDate = ControlConstantsDB.getValueByConCode(ControlConstantsDB.W2_CK_STK_INITDATE_CODE, ControlConstantsDB.W2_CK_STK_INITDATE_CODE);
				bean.setInitDate(initDate);
				bean.setWarehouse(warehouse);
				bean.setDispDiffQtyNoZero("true");
				aForm.setBean(bean);
				
				forward = "summary";
			}else if("new".equalsIgnoreCase(action) && pageName.equalsIgnoreCase("detail")){
				CheckStockReportBean bean = new CheckStockReportBean();
				//Get Config W2_CK_STK_INITDATE
				String initDate = ControlConstantsDB.getValueByConCode(ControlConstantsDB.W2_CK_STK_INITDATE_CODE, ControlConstantsDB.W2_CK_STK_INITDATE_CODE);
				bean.setInitDate(initDate);
				bean.setWarehouse(warehouse);
				bean.setDispDiffQtyNoZero("true");
				aForm.setBean(bean);
				
				forward = "detail";
			}
			//set PageName
			aForm.setPageName(pageName);
			aForm.setWarehouse(warehouse);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		CheckStockReportForm aForm = (CheckStockReportForm) form;
		String forward ="";
		StringBuffer resultHTML = null;
		boolean excel = false;
		try {
			//Clear session Result search
			request.getSession().removeAttribute("RESULT_SUMMARY");
			
			if(aForm.getPageName().equalsIgnoreCase("summary")){
			   resultHTML = CheckStockReportDAO.searchCheckStockSummary(aForm.getBean(),excel);
			   forward = "summary";
			}else if(aForm.getPageName().equalsIgnoreCase("detail")){
				resultHTML = CheckStockReportDAO.searchCheckStockDetail(aForm.getBean(),excel);
				forward = "detail";
			}
			if(resultHTML ==null){
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}else{
				request.getSession().setAttribute("RESULT_SUMMARY",resultHTML);
			}
		} catch (Exception e) {
			aForm.setResults(null);
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return forward;
	}
	/**
	 * Print Report
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		logger.debug("exportExcel : ");
		CheckStockReportForm aForm = (CheckStockReportForm) form;
		StringBuffer resultHTML = null;
		boolean excel = true;
		try {
			//Clear session Result search
			request.getSession().removeAttribute("RESULT_SUMMARY");
			
			if(aForm.getPageName().equalsIgnoreCase("summary")){
			    resultHTML = CheckStockReportDAO.searchCheckStockSummary(aForm.getBean(),excel);
			}else if(aForm.getPageName().equalsIgnoreCase("detail")){
				resultHTML = CheckStockReportDAO.searchCheckStockDetail(aForm.getBean(),excel);
			}
			if(resultHTML ==null){
				request.setAttribute("Message", "ไม่พบข้อมูล");
			}else{
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				//Writer w = new BufferedWriter(new OutputStreamWriter(out)); 
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				
				w.write(resultHTML.toString());
			    w.flush();
			    w.close();
	
			    out.flush();
			    out.close();
			    
			    logger.debug("\n"+resultHTML.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("summary");
	}
	

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		CheckStockReportForm aForm = (CheckStockReportForm) form;
		try {
			request.getSession().removeAttribute("RESULT_SUMMARY");
			CheckStockReportBean ad = aForm.getBean();
			ad.setStyle("");
			ad.setMaterialMaster("");
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("summary");
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		CheckStockReportForm aForm = (CheckStockReportForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
            e.printStackTrace();
			return "prepare";
		} finally {
		}
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
