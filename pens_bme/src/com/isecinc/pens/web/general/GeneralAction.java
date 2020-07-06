package com.isecinc.pens.web.general;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.order.OrderForm;
import com.isecinc.pens.web.summary.SummaryExport;
import com.isecinc.pens.web.summary.SummaryForm;
import com.pens.util.EnvProperties;
import com.pens.util.EnvQuartzProperties;
import com.pens.util.Utils;

/**
 * GeneralAction
 * 
 * @author Witty
 * @version $Id: 07/05/2020 $
 * 
 */
public class GeneralAction extends I_Action {

	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare have id");
		request.getSession().setAttribute("SESSION_DATA", null);
		return "prepare";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare no id");
		String pageName = Utils.isNull(request.getParameter("pageName"));
		if("GenBarcode".equalsIgnoreCase(pageName)){
			return new GenBarcodeProcessAction().prepare(form, request, response);
		}
		return "prepare";
	}

    protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return "prepare";
	}
    
    public ActionForward submitExecute(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("submitExecute");
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			if("GenBarcode".equalsIgnoreCase(pageName)){
				new GenBarcodeProcessAction().submitExecute(mapping, form, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("prepare");
	}
    
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		String htmlTable = "";
		String fileName ="data.xls";
		try {
			/** Export before search **/
			if(request.getAttribute("DATA_JOJO") ==null){
			
			    String result = GeneralDAO.searchReport(request);
				
				request.setAttribute("DATA_JOJO", result);
			}
			htmlTable = (String)request.getAttribute("DATA_JOJO");
			
		    java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+fileName);
			response.setContentType("application/vnd.ms-excel");
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable);
		    w.flush();
		    w.close();
		
		    out.flush();
		    out.close();
		
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
	   return mapping.findForward("prepare");
	}
    
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "";
	}

	@Override
	protected void setNewCriteria(ActionForm form) {
		
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
