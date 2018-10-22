package com.isecinc.pens.web.maya;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.maya.sub.MayaSaleOutAction;
import com.isecinc.pens.web.maya.sub.MayaStockOnhandAction;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class MayaAction extends I_Action {

	/** Constants  */
	final public static String P_MAYA_SALEOUT = "MayaSaleOut";
	final public static String P_MAYA_STOCK_ONHAND = "MayaStockOnhand";
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "search";
		MayaForm mayaForm = (MayaForm) form;
		String pageName =Utils.isNull(request.getParameter("pageName"));
		String pageAction = Utils.isNull(request.getParameter("pageAction"));
		MayaBean bean = new MayaBean();
		try {
			 logger.debug("prepare pageAction["+pageAction+"] pagaName:"+pageName);
			 if("new".equalsIgnoreCase(pageAction)){
				 if(P_MAYA_SALEOUT.equalsIgnoreCase(pageName)){
					bean.setStartDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					bean.setEndDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				 }
				 mayaForm.setBean(bean);
				 mayaForm.setResults(null);
				 
				 logger.debug("startDate:"+mayaForm.getBean().getStartDate());
			 }
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		MayaForm mayaForm = (MayaForm) form;
		try {
			logger.debug("prepare 2");
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MayaForm mayaForm = (MayaForm) form;
		User user = (User) request.getSession().getAttribute("user");
		logger.debug("page["+Utils.isNull(request.getParameter("page"))+"]");
		try {
			 String queryStr= request.getQueryString();
			 if(queryStr.indexOf("d-") != -1){
			 	queryStr = queryStr.substring(queryStr.indexOf("d-"),queryStr.indexOf("-p")+2 );
			 	System.out.println("queryStr:"+queryStr);
			 }
			 
			//Case link page in display no search again
			logger.debug("currentPage:"+request.getParameter(queryStr));
			if(request.getParameter(queryStr) != null){
				
			}else{
				 if(P_MAYA_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					 mayaForm = MayaSaleOutAction.search(request, mayaForm,user);
				 }else if(P_MAYA_STOCK_ONHAND.equalsIgnoreCase(Utils.isNull(request.getParameter("page"))) ){
					 mayaForm = MayaStockOnhandAction.search(request, mayaForm,user);
				 }
			}//Case Click Link	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "search";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		MayaForm mayaForm = (MayaForm) form;
		User user = (User) request.getSession().getAttribute("user");
		StringBuffer htmlTable = new StringBuffer("");
		String fileName ="data.xls";
		
		try {
			logger.debug("PageAction:"+request.getParameter("pageName"));
			
			 if(P_MAYA_SALEOUT.equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				fileName="Report Sale MAYA Shop.xls";
				if(mayaForm.getResults() != null && mayaForm.getResults().size() > 0){
					htmlTable = MayaSaleOutAction.exportToExcel(request,mayaForm,user,mayaForm.getResults());	
				}else{
					request.setAttribute("Message", "ไม่พบข้อมูล");
					return mapping.findForward("export");
				}
			}
			
	        //logger.debug("fileName:"+fileName);
	        //fileName = Utils.toUnicodeChar(fileName);
	        //logger.debug("fileName:"+fileName);
	        //"data.xls";
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("search");
	}
		
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		//Connection conn = null;
		//SummaryForm summaryForm = (SummaryForm) form;
		try {
			 
		} catch (Exception e) {

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "view";
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		MayaForm summaryForm = (MayaForm) form;
		try {
			
			 	
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
