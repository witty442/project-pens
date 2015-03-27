package com.isecinc.pens.web.admin;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;

/**
 * Sysconfig Action Class
 * 
 * @author Atiz.b
 * @version $Id: SysConfigAction.java,v 1.0 07/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SysConfigAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		SysConfigForm configForm = (SysConfigForm) form;
		try {
			
		} catch (Exception e) {
			request.setAttribute("Message", e.getMessage());
		}
		return "prepare";
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SysConfigForm configForm = (SysConfigForm) form;
		Connection conn = null;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");


			request.setAttribute("Message", "บันทึกข้อมูลสำเร็จ");
		} catch (Exception e) {
			request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้" + e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			try {
				conn.setAutoCommit(true);
				conn.close();
			} catch (Exception e2) {}
		}
		return "prepare";
	}

	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setNewCriteria(ActionForm form) {
	// TODO Auto-generated method stub

	}

}
