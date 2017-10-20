package com.isecinc.pens.web.clearinvoice;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Summary;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MSummary;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ClearInvoiceAction extends I_Action {

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ClearInvoiceForm summaryForm = (ClearInvoiceForm) form;
		try {
			 logger.debug("prepare");
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 summaryForm.setResults(null);
				 ClearInvoice s = new ClearInvoice();
				 s.setCondition("remain_amount <= 100 and remain_amount >=0.9");
				 summaryForm.setBean(s);
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
		ClearInvoiceForm summaryForm = (ClearInvoiceForm) form;
		try {
			logger.debug("prepare 2");
			if("new".equalsIgnoreCase(request.getParameter("action"))){
				request.getSession().setAttribute("results", null);
				summaryForm.setResults(null);
			 }
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
		ClearInvoiceForm summaryForm = (ClearInvoiceForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			List<ClearInvoice> results = new ClearInvoiceDAO().search(summaryForm.getBean(),user);
			if (results != null) {
				//request.getSession().setAttribute("results", results);
				summaryForm.setResults(results);
				
				logger.debug("results:"+summaryForm.getResults());
				
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ClearInvoiceForm summaryForm = (ClearInvoiceForm) form;
		int r = 0;
		String orderIdSqlIn = "";
		String re ="";
		User user = (User) request.getSession().getAttribute("user");
		try {
			if( summaryForm.getResults() !=null && summaryForm.getResults().size()>0){
				
				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				for(int i=0;i<summaryForm.getResults().size();i++){
					ClearInvoice item = summaryForm.getResults() .get(i);
					
					r += ClearInvoiceDAO.updatePaidAmountCheck(conn, item);
					
					orderIdSqlIn += "'"+item.getOrderId()+"',";
				}
				conn.commit();
				
				logger.debug("Total Record Update:"+r);
				if(orderIdSqlIn.length()>0){
					orderIdSqlIn = orderIdSqlIn.substring(0,orderIdSqlIn.length()-1);
				}
				
				//search again
				ClearInvoice c = summaryForm.getBean();
				c.setCondition("");
				c.setOrderIdSqlIn(orderIdSqlIn);
				
				List<ClearInvoice> results = new ClearInvoiceDAO().search(c,user);
				if (results != null) {
					//request.getSession().setAttribute("results", results);
					summaryForm.setResults(results);
					logger.debug("results:"+summaryForm.getResults());
				} else {
					request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
				}
				re ="search";
				c.setCondition("remain_amount <= 100 and remain_amount >=0.9");
			}
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
        return re;
	}

	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ClearInvoiceForm summaryForm = (ClearInvoiceForm) form;
		try {
			 summaryForm.setBean(new ClearInvoice());
			 summaryForm.setResults(null);
			 request.getSession().setAttribute("results", null);
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
