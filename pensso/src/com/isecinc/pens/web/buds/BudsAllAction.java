package com.isecinc.pens.web.buds;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.buds.page.ConfPickingAction;
import com.isecinc.pens.web.buds.page.ControlPickingAction;
import com.isecinc.pens.web.buds.page.InvoiceReportAction;
import com.isecinc.pens.web.buds.page.OrderEDIAction;
import com.isecinc.pens.web.buds.page.StockOnhandAction;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class BudsAllAction extends I_Action {

	public ActionForward prepareSearchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearchHead");
		BudsAllForm budsAllForm = (BudsAllForm) form;
		try {
			 if("ConfPickingSearch".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ConfPickingAction().prepareSearchHead(mapping, budsAllForm, request, response);
			 }else if("ControlPickingSearch".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ControlPickingAction().prepareSearchHead(mapping, budsAllForm, request, response);
			 }else if("StockOnhandSearch".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new StockOnhandAction().prepareSearchHead(mapping, budsAllForm, request, response);
			 }else if("OrderEDISearch".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new OrderEDIAction().prepareSearchHead(mapping, budsAllForm, request, response);
			 }else if("InvoiceReport".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new InvoiceReportAction().prepareSearchHead(mapping, budsAllForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message",e.toString());
		}
		return mapping.findForward("budsAll");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+request.getParameter("page"));
			 if("ConfPickingSearch".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ConfPickingAction().searchHead(mapping, aForm, request, response);
			 }else if("ControlPickingSearch".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new ControlPickingAction().searchHead(mapping, aForm, request, response);
			 }else if("OrderEDISearch".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				 return new OrderEDIAction().searchHead(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+request.getParameter("pageName"));
			logger.debug("subPageName:"+request.getParameter("subPageName"));
			 if("ConfPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new ConfPickingAction().viewDetail(mapping, aForm, request, response);
			 }else  if("OrderEDIDetail".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new OrderEDIAction().viewDetail(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "reportAll";
		BudsAllForm budsAllForm = (BudsAllForm) form;
		try {
			 logger.debug("prepare pageName["+Utils.isNull(request.getParameter("pageName"))+"] action["+request.getParameter("action")+"]");
			
			 if("new".equalsIgnoreCase(request.getParameter("action"))){
				 request.getSession().setAttribute("results", null);
				 request.getSession().setAttribute("summary",null);
				 budsAllForm.setResults(null);
				 
				 //Default display have qty
				 if("ConfPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new ConfPickingAction().prepare(form, request, response);
				 }else if("OrderEDI".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
					 return new OrderEDIAction().prepare(form, request, response);
				 }
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
		try {
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "reports";
	}
	
	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		logger.debug("page["+aForm.getPageName()+"]");
		try {
			 if("ConfPicking".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				return new ConfPickingAction().search(form, request, response);
			 }else if("OrderEDI".equalsIgnoreCase(Utils.isNull(request.getParameter("pageName"))) ){
				 return new OrderEDIAction().search(form, request, response);
			 }else if("StockOnhandSearch".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				return new StockOnhandAction().search(aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw e;
		}
		return "budsAll";
	}
	
	public ActionForward export(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("reportName:"+request.getParameter("reportName"));
			 if("PickingList".equalsIgnoreCase(Utils.isNull(aForm.getPageName())) ){
				return new ConfPickingAction().exportPickingList(mapping, aForm, request, response);
			 }else if("PickingReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new ConfPickingAction().exportPickingListReport(mapping, form, request, response);
			 }else if("SalesReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new ConfPickingAction().exportSalesReport(mapping, aForm, request, response);
			 }else if("SalesDetailReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new ConfPickingAction().exportSalesDetailReport(mapping, aForm, request, response);
			 }else if("ControlPickingReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new ControlPickingAction().exportControlPicking(mapping, aForm, request, response);
			 }else if("StockOnhandReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new StockOnhandAction().exportStockOnhandReport(mapping, aForm, request, response);
			 }else if("InvoiceReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new InvoiceReportAction().exportInvoiceReport(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("budsAll");
	}
	
	public ActionForward printReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("reportName:"+request.getParameter("reportName"));
		    if("PickingReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
			   return new ConfPickingAction().printPickingReport(mapping, form, request, response);
		    }else if("LoadingReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
			   return new ConfPickingAction().printPickingReport(mapping, form, request, response);
		    }else if("ControlPickingReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
			   return new ControlPickingAction().printControlPickingReport(mapping, form, request, response);
		    }else if("InvoiceReport".equalsIgnoreCase(Utils.isNull(request.getParameter("reportName"))) ){
				return new InvoiceReportAction().printInvoiceReport(mapping, aForm, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	
	public ActionForward confirmPicking(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("ConfPicking".equalsIgnoreCase(aForm.getPageName()) ){
			   return new ConfPickingAction().confirmPicking(mapping, form, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	public ActionForward addOrderPickingManual(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("ConfPicking".equalsIgnoreCase(aForm.getPageName()) ){
			   return new ConfPickingAction().addOrderPickingManual(mapping, form, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	public ActionForward rejectOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("ConfPicking".equalsIgnoreCase(aForm.getPageName()) ){
			   return new ConfPickingAction().rejectOrder(mapping, form, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	
	public ActionForward genInvoice(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			logger.debug("PageName:"+aForm.getPageName());
		    if("ConfPicking".equalsIgnoreCase(aForm.getPageName()) ){
			   return new ConfPickingAction().genInvoice(mapping, form, request, response);
		    }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	
	public ActionForward saveAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
		try {
			 String method = Utils.isNull(request.getParameter("method"));
			 logger.debug("method:"+method);
			 if("OrderEDIDetail".equalsIgnoreCase(aForm.getPageName()) && "saveOrderEDI".equalsIgnoreCase(method)){
				 return new OrderEDIAction().saveOrderEDI(mapping, aForm, request, response);
			 }else if("OrderEDIDetail".equalsIgnoreCase(aForm.getPageName()) && "confirmOrderEDI".equalsIgnoreCase(method)){
				 return new OrderEDIAction().confirmOrderEDI(mapping, aForm, request, response);
			 }else if("OrderEDIDetail".equalsIgnoreCase(aForm.getPageName()) && "cancelOrderEDI".equalsIgnoreCase(method)){
				 return new OrderEDIAction().cancelOrderEDI(mapping, aForm, request, response);
			 }
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		BudsAllForm aForm = (BudsAllForm) form;
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
