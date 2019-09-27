package com.isecinc.pens.web.stockmc;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockMCAction extends I_Action {

	public static int pageSize = 60;
	public static String pageSTockMC = "STOCKMC";
	public static String pageMasItemStockMC = "MasterItemStockMC";
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockMCForm aForm = (StockMCForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String forward ="";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				aForm.setPageName(pageName);
				return new StockMCProcess().prepareSearch(mapping, aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				aForm.setPageName(pageName);
				return new StockMCMasterItemProcess().prepareSearch(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward(forward);
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		StockMCForm aForm = (StockMCForm) form;
		String msg = "";
		try {
			String action = Utils.isNull(request.getParameter("action"));
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			logger.debug("pageName:"+pageName);
			logger.debug("action:"+action);
			
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().searchHead(mapping, form, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().searchHead(mapping, form, request, response);
			}
		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		
		return "detail";
	}

	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("viewDetail : ");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			logger.debug("pageName:"+pageName);

			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().viewDetail(mapping, form, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().viewDetail(mapping, form, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	public ActionForward loadItem(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("loadItem : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().loadItem(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("clearForm  ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().clearForm(mapping, form, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				
			}	
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
		}
		return mapping.findForward("detail");
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String msg = "";
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			logger.debug("save-->");
           
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().save(aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().save(aForm, request, response);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return "new";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockMCForm aForm = (StockMCForm) form;
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			if(pageSTockMC.equalsIgnoreCase(pageName)){
				return new StockMCProcess().exportToExcel(mapping, aForm, request, response);
			}else if(pageMasItemStockMC.equalsIgnoreCase(pageName)){
				return new StockMCMasterItemProcess().exportToExcel(mapping, form, request, response);
			}	
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
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
