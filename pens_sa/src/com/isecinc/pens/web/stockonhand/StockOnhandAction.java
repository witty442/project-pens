package com.isecinc.pens.web.stockonhand;

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
import com.pens.util.PageVisit;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class StockOnhandAction extends I_Action {

	public static int pageSize = 99999;
	public static String PAGE_STOCK_VAN ="StockVan";
	public static String PAGE_STOCK_OH ="StockOnhand";
	public static String PAGE_STOCK_CV ="StockCoverage";
	public static String PAGE_PREODER_NISSIN ="PreOrderNissin";
	
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		StockOnhandForm aForm = (StockOnhandForm) form;
		Connection conn = null;
		try { 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			
			if(PAGE_STOCK_VAN.equalsIgnoreCase(pageName)){
				return new StockVanProcess().prepareSearch(mapping, aForm, request, response);
			}else if(PAGE_STOCK_OH.equalsIgnoreCase(pageName)){
				return new StockOnhandProcess().prepareSearch(mapping, aForm, request, response);
			}else if(PAGE_STOCK_CV.equalsIgnoreCase(pageName)){
				return new StockCVProcess().prepareSearch(mapping, aForm, request, response);
			}else if(PAGE_PREODER_NISSIN.equalsIgnoreCase(pageName)){
				return new PreOrderNissinProcess().prepareSearch(mapping, aForm, request, response);
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
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			if(PAGE_STOCK_VAN.equalsIgnoreCase(pageName)){
				return new StockVanProcess().searchHead(mapping, aForm, request, response);
			}else if(PAGE_STOCK_OH.equalsIgnoreCase(pageName)){
			   return new StockOnhandProcess().searchHead(mapping, aForm, request, response);
			}else if(PAGE_STOCK_CV.equalsIgnoreCase(pageName)){
			   return new StockCVProcess().searchHead(mapping, aForm, request, response);
			}else if(PAGE_PREODER_NISSIN.equalsIgnoreCase(pageName)){
			   return new PreOrderNissinProcess().searchHead(mapping, aForm, request, response);
			} 
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
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		StockOnhandForm aForm = (StockOnhandForm) form;
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			if(true){
				Thread.sleep(10000);
			}
		    if(PAGE_PREODER_NISSIN.equalsIgnoreCase(pageName)){
			   return new PreOrderNissinProcess().save(aForm, request, response);
			} 
		} catch (Exception e) {
			
            e.printStackTrace();
			request.setAttribute("Message","�������ö�ѹ�֡�������� \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "search";
		} finally {
			try {
			} catch (Exception e2) {}
		}
		return "search";
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		StockOnhandForm stockVanForm = (StockOnhandForm) form;
		String pageName = stockVanForm.getPageName();
		try {
			if(PAGE_STOCK_VAN.equalsIgnoreCase(pageName)){
				return new StockVanProcess().exportToExcel(mapping, stockVanForm, request, response);
			}else if(PAGE_STOCK_OH.equalsIgnoreCase(pageName)){
			    return new StockOnhandProcess().exportToExcel(mapping, stockVanForm, request, response);
			}else if(PAGE_STOCK_CV.equalsIgnoreCase(pageName)){
			    return new StockCVProcess().exportToExcel(mapping, stockVanForm, request, response);
			}else if(PAGE_PREODER_NISSIN.equalsIgnoreCase(pageName)){
			    return new PreOrderNissinProcess().exportToExcel(mapping, stockVanForm, request, response);
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
