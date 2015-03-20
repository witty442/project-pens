package com.isecinc.pens.web.memberrenew;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberRenew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.init.InitialSystemConfig;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberRenew;

/**
 * Member Renew Action Class
 * 
 * @author Aneak.t
 * @version $Id: MemberRenewAction.java,v 1.0 01/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberRenewAction extends I_Action {

	/**
	 * Prepare with id.
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Member Renew Prepare Form");
		MemberRenewForm memberRenewForm = (MemberRenewForm) form;
		Member member = null;

		try {

			member = new MMember().find(id);
			if (member == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			memberRenewForm.getMemberRenew().setMember(member);
			memberRenewForm.getMemberRenew().setExpiredDate(member.getExpiredDate());
			memberRenewForm.getMemberRenew().setRenewedDate(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));

			List<MemberRenew> renews = new MMemberRenew().lookUp(Integer.parseInt(id));
			

			if (renews.size() > 0) {
				memberRenewForm.getMemberRenew().setAppliedDate(renews.get(0).getRenewedDate());
			} else {
				memberRenewForm.getMemberRenew().setAppliedDate(member.getRegisterDate());
			}
			
			//Case no Expire get last renew_id for update
			String currentDateStr = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String lastExpireDateStr = renews.get(0).getExpiredDate();
			
			Date currentDate = Utils.parse(currentDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			Date lastExpireDate = Utils.parse(lastExpireDateStr, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			logger.debug("currentDate:"+currentDate);
			logger.debug("lastExpireDate:"+lastExpireDate);
			
			if( currentDate.before(lastExpireDate)){
				memberRenewForm.getMemberRenew().setId(renews.get(0).getId());
			}
			
			logger.debug("renew date:"+renews.get(0).getRenewedDate());

			// get back search key
			if (memberRenewForm.getCriteria().getSearchKey() == null) {
				if (request.getSession(true).getAttribute("CMSearchKey") != null) {
					memberRenewForm.getCriteria().setSearchKey(
							(String) request.getSession(true).getAttribute("CMSearchKey"));
				}
			} else {
				request.getSession(true).removeAttribute("CMSearchKey");
			}

			request.getSession(true).setAttribute("isPrepare", "Y");
			request.getSession().removeAttribute("save_success");
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}

		return "add";
	}

	/**
	 * Save.
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MemberRenewForm memberRenewForm = (MemberRenewForm) form;
		Connection conn = null;
		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);

			// Begin Transaction
			conn.setAutoCommit(false);

			// Save Member Renew
			new MMemberRenew().save(memberRenewForm.getMemberRenew(), userActive.getCode(), userActive.getId(), conn);

			Calendar c1 = Calendar.getInstance();
			c1.setTime(DateToolsUtil.convertStringToDate(memberRenewForm.getMemberRenew().getRenewedDate()));
			c1.add(Calendar.MONTH, Integer.parseInt(memberRenewForm.getMemberRenew().getMemberType()));

			// Update to Member
			MMember mMember = new MMember();

			Member member = mMember.find(String.valueOf(memberRenewForm.getMemberRenew().getMember().getId()));
			member.setMemberType(memberRenewForm.getMemberRenew().getMemberType());
			member.setExpiredDate(DateToolsUtil.convertToString(c1.getTime()));

			// Calculate new Level
			mMember.setMemberAgeLevel(member);

			// Save
			mMember.save(member, userActive.getCode(), userActive.getId(), conn);

			// Commit Transaction
			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			request.getSession(true).setAttribute("isPrepare", "Y");
			request.getSession(true).setAttribute("save_success", "Y");
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return "add";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MemberRenewForm memberRenewForm = (MemberRenewForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String type = request.getParameter("type") != null ? (String) request.getParameter("type") : "";
		String whereCause = "";
		Member[] members = null;
		try {
			logger.info("type:"+type);
			MemberRenewCriteria criteria = getSearchCriteria(request, memberRenewForm.getCriteria(), this.getClass().toString());
			memberRenewForm.setCriteria(criteria);
			String toDay = DateToolsUtil.getCurrentDateTime("dd/MM/yyyy");
			String expireDate = "";
			String alertPeriod = "";
			Calendar cal = Calendar.getInstance();

			if (type.equals("show")) {
				alertPeriod = InitialSystemConfig.getConfigs().get(InitialReferences.ALERT_PERIOD) != null ? InitialSystemConfig
						.getConfigs().get(InitialReferences.ALERT_PERIOD).getValue()
						: "0";
				cal.add(Calendar.DATE, Integer.parseInt(alertPeriod));
				expireDate = DateToolsUtil.convertToString(cal.getTime());
				whereCause += " AND EXPIRED_DATE <= '" + DateToolsUtil.convertToTimeStamp(expireDate) + "'";

			} else if (type.equals("search")) {
				if (memberRenewForm.getMemberRenew().getAlertPeriod() != null
						&& !memberRenewForm.getMemberRenew().getAlertPeriod().equals("")) {
					alertPeriod = memberRenewForm.getMemberRenew().getAlertPeriod();
					cal.add(Calendar.DATE, Integer.parseInt(alertPeriod));
					expireDate = DateToolsUtil.convertToString(cal.getTime());
					whereCause += " AND EXPIRED_DATE >= '" + DateToolsUtil.convertToTimeStamp(toDay) + "'";
					whereCause += " AND EXPIRED_DATE <= '" + DateToolsUtil.convertToTimeStamp(expireDate) + "'";
				}

				if ( !Utils.isNull(memberRenewForm.getMemberRenew().getMember().getCode()).equals("") )  {
					whereCause += " AND CODE LIKE '%"+ Utils.isNull(memberRenewForm.getMemberRenew().getMember().getCode()) + "%'";
				}
				if ( !Utils.isNull(memberRenewForm.getMemberRenew().getMember().getName()).equals("")) {
					whereCause += " AND NAME LIKE '%"+ Utils.isNull(memberRenewForm.getMemberRenew().getMember().getName()) + "%'";
				}
				if (!Utils.isNull(memberRenewForm.getMemberRenew().getMember().getName2()).equals("")) {
					whereCause += " AND NAME2 LIKE '%"+ Utils.isNull(memberRenewForm.getMemberRenew().getMember().getName2()) + "%'";
				}
				if (!Utils.isNull(memberRenewForm.getMemberRenew().getExpiredDateFrom()).equals("") ) {
					whereCause += " AND EXPIRED_DATE >= '"
							   + DateToolsUtil.convertToTimeStamp(Utils.isNull(memberRenewForm.getMemberRenew().getExpiredDateFrom())) + "'";
				}
				if (!Utils.isNull(memberRenewForm.getMemberRenew().getExpiredDateTo()).equals("")) {
					whereCause += " AND EXPIRED_DATE <= '"
							   + DateToolsUtil.convertToTimeStamp(Utils.isNull(memberRenewForm.getMemberRenew().getExpiredDateTo())) + "'";
				}
			}

			whereCause += " AND CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			whereCause += " AND ISACTIVE = 'Y' ";
			whereCause += " ORDER BY CUSTOMER_ID, EXPIRED_DATE ";

			logger.info("whereSql:"+whereCause);
			
			members = new MMember().search(whereCause);
			memberRenewForm.setMembers(members);

			if (members != null) {
				memberRenewForm.getCriteria().setSearchResult(members.length);
			} else {
				if (!type.equals("show")) {
					request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND)
							.getDesc());
				}
			}
			request.getSession().removeAttribute("save_success");
			request.getSession().setAttribute("first_search", "Y");
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}

		if (type.equals("show")) return "show";
		else return "search";
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
		MemberRenewForm memberRenewForm = (MemberRenewForm) form;
		memberRenewForm.setCriteria(new MemberRenewCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
