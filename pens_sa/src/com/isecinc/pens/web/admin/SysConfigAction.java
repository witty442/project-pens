package com.isecinc.pens.web.admin;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DBConnection;

import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.SystemConfig;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.init.InitialSystemConfig;
import com.isecinc.pens.model.MSysConfig;

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
			String alertPeriod = InitialSystemConfig.getConfigs().get(InitialReferences.ALERT_PERIOD) != null ? InitialSystemConfig
					.getConfigs().get(InitialReferences.ALERT_PERIOD).getValue()
					: "0";

			String qtyDel = InitialSystemConfig.getConfigs().get(InitialReferences.QTY_DELIVER) != null ? InitialSystemConfig
					.getConfigs().get(InitialReferences.QTY_DELIVER).getValue()
					: "";

			String roundDel = InitialSystemConfig.getConfigs().get(InitialReferences.ROUND_DELIVER) != null ? InitialSystemConfig
					.getConfigs().get(InitialReferences.ROUND_DELIVER).getValue()
					: "";

			String memberType = InitialSystemConfig.getConfigs().get(InitialReferences.MEMBER_TYPE) != null ? InitialSystemConfig
					.getConfigs().get(InitialReferences.MEMBER_TYPE).getValue()
					: "";

			configForm.setAlertPeriod(new Integer(alertPeriod));
			configForm.setQtyDeliver(qtyDel);
			configForm.setRoundDeliver(roundDel);
			configForm.setMemberType(memberType);
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

			String alertPeriod = String.valueOf(configForm.getAlertPeriod());
			String qtyDel = configForm.getQtyDeliver();
			String roundDel = configForm.getRoundDeliver();
			String memberType = configForm.getMemberType();

			SystemConfig config1 = InitialSystemConfig.getConfigs().get(InitialReferences.ALERT_PERIOD);
			SystemConfig config2 = InitialSystemConfig.getConfigs().get(InitialReferences.QTY_DELIVER);
			SystemConfig config3 = InitialSystemConfig.getConfigs().get(InitialReferences.ROUND_DELIVER);
			SystemConfig config4 = InitialSystemConfig.getConfigs().get(InitialReferences.MEMBER_TYPE);

			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			// Begin Trans
			new MSysConfig().save(new SystemConfig(config1.getId(), config1.getName(), alertPeriod),
					userActive.getId(), conn);
			new MSysConfig().save(new SystemConfig(config2.getId(), config2.getName(), qtyDel), userActive.getId(),
					conn);
			new MSysConfig().save(new SystemConfig(config3.getId(), config3.getName(), roundDel), userActive.getId(),
					conn);
			new MSysConfig().save(new SystemConfig(config4.getId(), config4.getName(), memberType), userActive.getId(),
					conn);
			// Commit
			conn.commit();

			config1.setValue(alertPeriod);
			config2.setValue(qtyDel);
			config3.setValue(roundDel);
			config4.setValue(memberType);

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
