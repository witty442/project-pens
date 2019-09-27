package com.isecinc.pens.web.van;

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
public class VanAction extends I_Action {

	public static String PAGE_ORDER_VAN_VO ="OrderVanVO";
	
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		VanForm aForm = (VanForm) form;
		Connection conn = null;
		try { 
			String pageName = Utils.isNull(request.getParameter("pageName")); 
			String action = Utils.isNull(request.getParameter("action")); 
			
			if("new".equalsIgnoreCase(action)){
				if(PAGE_ORDER_VAN_VO.equalsIgnoreCase(pageName)){
					return new OrderVanVOProcess().prepareSearch(mapping, aForm, request, response);
				}
			}else{
				aForm.setBean(aForm.getBeanCriteria());//get prev criteria
				if(PAGE_ORDER_VAN_VO.equalsIgnoreCase(pageName)){
					return new OrderVanVOProcess().searchHead(mapping, aForm, request, response);
				}
			}
		} catch (Exception e) {
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
		VanForm aForm = (VanForm) form;
		Connection conn = null;
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			
			if(PAGE_ORDER_VAN_VO.equalsIgnoreCase(pageName)){
				return new OrderVanVOProcess().searchHead(mapping, aForm, request, response);
			}
		} catch (Exception e) {
		    e.printStackTrace();
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward viewDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("viewDetail");
		VanForm aForm = (VanForm) form;
		Connection conn = null;
		try {
			String pageName = Utils.isNull(request.getParameter("pageName"));
			if(PAGE_ORDER_VAN_VO.equalsIgnoreCase(pageName)){
				return new OrderVanVOProcess().searchDetail(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		User user = (User) request.getSession().getAttribute("user");
		VanForm aForm = (VanForm) form;
		String pageName = aForm.getPageName();
		
		return "detail";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		VanForm aForm = (VanForm) form;
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
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		VanForm stockVanForm = (VanForm) form;
		String pageName = stockVanForm.getPageName();
		Connection conn = null;
		try {
			if(PAGE_ORDER_VAN_VO.equalsIgnoreCase(pageName)){
				return new OrderVanVOProcess().exportToExcel(mapping, stockVanForm, request, response);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;//
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
		VanForm orderForm = (VanForm) form;
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
