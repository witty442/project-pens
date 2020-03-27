package com.isecinc.pens.web.jojo;

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
 * Member Receipt Action
 * 
 * @author atiz.b
 * @version $Id: MemberReceiptAction.java,v 1.0 07/02/2011 00:00:00 atiz.b Exp $
 * 
 */
public class JojoAction extends I_Action {

	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare1");
		request.getSession().setAttribute("DATA_JOJO", null);
		return "process";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("prepare2");
		request.getSession().setAttribute("DATA_JOJO", null);
		return "search";
	}

    protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	JojoForm jojoForm = (JojoForm) form;
    	try {
			logger.debug("search");
			
		    String result = JojoDAO.searchReport(request);
			
			request.setAttribute("DATA_JOJO", result);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return "search";
	}
    
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("export");
		String htmlTable = "";
		String fileName ="data.xls";
		try {
			/** Export before search **/
			if(request.getAttribute("DATA_JOJO") ==null){
			
			    String result = JojoDAO.searchReport(request);
				
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
	return mapping.findForward("export");
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
