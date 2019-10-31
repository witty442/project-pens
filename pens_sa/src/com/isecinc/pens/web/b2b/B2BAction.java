package com.isecinc.pens.web.b2b;

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
import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class B2BAction extends I_Action {

	public static int pageSize = 90;
	public static final String PAGE_B2B_MAKRO = "B2BMakro";
	public static final String PAGE_Q_B2B_MAKRO_SALESBYITEM = "QueryB2BMakroSalesByItem";
	 
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		B2BForm aForm = (B2BForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		String forward ="search";
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			String popup = Utils.isNull(request.getParameter("popup")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			
			if("new".equals(action)){
				pageName = Utils.isNull(request.getParameter("pageName"));
				request.getSession().removeAttribute("BATCH_TASK_RESULT");
				
				B2BBean sales = new B2BBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
					return new B2BProcess().prepareSearch(mapping, aForm, request, response);
				}else if (PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){
					return new QueryB2BMakroSalesByItemProcess().prepareSearch(mapping, aForm, request, response);
				}
				aForm.setBean(sales);
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]");
				
			}
			logger.debug("forward:"+forward);
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
	
	public ActionForward importExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("importExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("importExcel :pageName["+pageName+"]");
	
			 //ImportExcel
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				return new B2BProcess().importExcel(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchHead");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("importExcel :pageName["+pageName+"]");
	
			 //Search Report
			if (PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){
				return new QueryB2BMakroSalesByItemProcess().searchHead(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("exportExcel :pageName["+pageName+"]");
	
			 //Search Report
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				return new B2BProcess().exportExcel(mapping, aForm, request, response);
			}else if (PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){
				return new QueryB2BMakroSalesByItemProcess().exportToExcel(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward loadExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("loadExcel");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = Utils.isNull(request.getParameter("pageName")); 
		try {
			logger.debug("loadExcel :pageName["+pageName+"]");
	
			//Load Excel
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 return new B2BProcess().loadExcel(mapping, aForm, request, response);
			}else if (PAGE_Q_B2B_MAKRO_SALESBYITEM.equalsIgnoreCase(pageName)){
				return new QueryB2BMakroSalesByItemProcess().loadExcel(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("searchBatch");
		B2BForm aForm = (B2BForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName(); 
		try {
			logger.debug("searchBatch :pageName["+pageName+"]");
	
			 //searchBatch
			if (PAGE_B2B_MAKRO.equalsIgnoreCase(pageName)){
				 return new B2BProcess().searchBatch(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		B2BForm aForm = (B2BForm) form;
		String pageName = aForm.getPageName();
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		B2BForm aForm = (B2BForm) form;
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
		B2BForm orderForm = (B2BForm) form;
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
