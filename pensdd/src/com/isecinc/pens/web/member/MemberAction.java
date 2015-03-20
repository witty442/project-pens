package com.isecinc.pens.web.member;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberProduct;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ResultBean;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.init.InitialSystemConfig;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MContact;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MMemberProduct;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MTrxHistory;

/**
 * Member Action Class
 * 
 * @author Aneak.t
 * @version $Id: MemberAction.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberAction extends I_Action {
	
	
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Member Prepare Form");
		MemberForm memberForm = (MemberForm) form;
		Member member = null;

		try {
			User userActive = (User) request.getSession(true).getAttribute("user");
			member = new MMember().find(id);
			if (member == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}

			member.setCustomerType(userActive.getCustomerType().getKey());
			memberForm.setAddresses(new MAddress().lookUp(member.getId()));
			memberForm.setContacts(new MContact().lookUp(member.getId()));
			memberForm.setMemberProducts(new MMemberProduct().lookUp(member.getId()));
			if (member.getBirthDay() != null && !member.getBirthDay().equals("")) {
				int curYear = Integer.parseInt(new SimpleDateFormat("yyyy", new Locale("th", "TH")).format(new Date()));
				int birthYear = Integer.parseInt(member.getBirthDay().split("/")[2]);
				member.setAge(String.valueOf(curYear - birthYear));
			}
			// check expire date.
			String expireDate = this.getExpireDate(member.getId());
			if (!"".equals(expireDate)) member.setExpiredDate(expireDate);

			memberForm.setMember(member);
			// get back search key
			if (memberForm.getCriteria().getSearchKey() == null) {
				if (request.getSession(true).getAttribute("CMSearchKey") != null) {
					memberForm.getCriteria()
							.setSearchKey((String) request.getSession(true).getAttribute("CMSearchKey"));
				}
			} else {
				request.getSession(true).removeAttribute("CMSearchKey");
			}

			// save token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		if (request.getParameter("action").equalsIgnoreCase("edit")) return "prepare";
		return "view";
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Member Prepare Form without ID");
		MemberForm memberForm = (MemberForm) form;
		try {
			Member member = new Member();
			// Default Vat Code .. need
			member.setVatCode("7");

			// default to active
			member.setIsActive("Y");

			// Set default value.
			member.setMemberType(InitialSystemConfig.getConfigs().get(InitialReferences.MEMBER_TYPE).getValue());
			member.setOrderAmountPeriod(Integer.parseInt(InitialSystemConfig.getConfigs().get(
					InitialReferences.QTY_DELIVER).getValue()));
			member.setRoundTrip(InitialSystemConfig.getConfigs().get(InitialReferences.ROUND_DELIVER).getValue());

			member.setRecommendedCode("");
			member.setRecommendedBy("");

			memberForm.setMember(member);

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}

		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MemberForm memberForm = (MemberForm) form;
		try {
			MemberCriteria criteria = getSearchCriteria(request, memberForm.getCriteria(), this.getClass().toString());
			memberForm.setCriteria(criteria);
			
			ResultBean resultBean = new MMember().searchNew(memberForm);
			
			List<Member> results = resultBean.getResults();
			memberForm.setResults(results);

			if (results != null && results.size() > 0) {
				if(resultBean.isMoreMaxRecord()){
				     memberForm.getCriteria().setSearchResult(results.size());
				     request.setAttribute("Message_more_max_disp", "มากกว่า");
				     request.setAttribute("Message_more_max_disp2", "โปรดระบุ Criteria ให้มากขึ้น");
				}else{
				     memberForm.getCriteria().setSearchResult(results.size());	
				}
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}
		return "search";
	}

	/*
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		MemberForm memberForm = (MemberForm) form;
		String deleteID = (String) request.getParameter("ids");
		String deleteIDContacts = (String) request.getParameter("ids_contact");
		int memberId = 0;
		try {
			memberId = memberForm.getMember().getId();
			// check Token
			if (!isTokenValid(request)) {
				memberForm.setMember(new Member());
				memberForm.getAddresses().clear();
				memberForm.getContacts().clear();
				memberForm.getMemberProducts().clear();
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");
			Member member = memberForm.getMember();

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			// Save Member
			member.setCustomerType(userActive.getCustomerType().getKey());

			// Calculate expired date.
			Calendar cal = Calendar.getInstance();
			int month = 0;
			month = Integer.parseInt(memberForm.getMember().getMemberType());

			// Calculate Expire Date at 1st Save
			if (memberForm.getMember().getId() == 0 || memberForm.getMember().getExpiredDate().length() == 0) {
				cal.setTime(DateToolsUtil.convertStringToDate(memberForm.getMember().getRegisterDate()));
				cal.add(Calendar.MONTH, month);
				member.setExpiredDate(DateToolsUtil.convertToString(cal.getTime()));
				new MMember().setMemberAgeLevel(member);
			}

			logger.debug("creditName:"+member.getCardName());
			// Save Member
			if (!new MMember().save(member, userActive.getCode(), userActive.getId(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}

			// Save Address
			for (Address address : memberForm.getAddresses()) {
				address.setCustomerId(member.getId());
				new MAddress().save(address, userActive.getId(), conn);
			}

			// Save Contact
			for (Contact contact : memberForm.getContacts()) {
				contact.setCustomerId(member.getId());
				new MContact().save(contact, userActive.getId(), conn);
			}
			
			//Delete contact
			if (deleteIDContacts != null && !deleteIDContacts.equals("")) {
				// ',1,2,3' --> '1,2,3'
				deleteIDContacts = deleteIDContacts.substring(1, deleteIDContacts.length());
				new MContact().delete(deleteIDContacts, conn);
			}
			
			// Save Product
			for (MemberProduct memberProduct : memberForm.getMemberProducts()) {
				memberProduct.setCustomerId(member.getId());
				new MMemberProduct().save(memberProduct, userActive.getId(), conn);
			}

			// Delete
			if (deleteID != null && !deleteID.equals("")) {
				// ',1,2,3' --> '1,2,3'
				deleteID = deleteID.substring(1, deleteID.length());
				new MMemberProduct().delete(deleteID, conn);
			}

			// Trx History
			TrxHistory trx = new TrxHistory();
			trx.setTrxModule(TrxHistory.MOD_MEMBER);
			if (memberId == 0) trx.setTrxType(TrxHistory.TYPE_INSERT);
			else trx.setTrxType(TrxHistory.TYPE_UPDATE);
			trx.setRecordId(member.getId());
			trx.setUser(userActive);
			new MTrxHistory().save(trx, userActive.getId(), conn);
			// Trx History --end--

			// Commit Transaction
			conn.commit();

			memberForm.setMember(member);

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			// save token
			saveToken(request);
		} catch (Exception e) {
			memberForm.getMember().setId(memberId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}

		return "view";
	}

	/**
	 * Cancel Member
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward cancelMember(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		MemberForm memberForm = (MemberForm) form;
		Connection conn = null;
		Member member = null;

		try {
			User user = (User) request.getSession().getAttribute("user");

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			// Transaction
			member = new MMember().find(String.valueOf(memberForm.getMember().getId()));
			member.setCancelReason(memberForm.getMember().getCancelReason());
			member.setIsActive("C");

			new MMember().save(member, user.getCode(), user.getId(), conn);

			// Commit Transaction
			conn.commit();

			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
		} finally {
			try {
				conn.setAutoCommit(true);
				conn.close();
			} catch (Exception e2) {}
		}

		return mapping.findForward("cancel-member");
	}

	/**
	 * New Criteria
	 */
	protected void setNewCriteria(ActionForm form) {
		MemberForm memberForm = (MemberForm) form;
		memberForm.setCriteria(new MemberCriteria());
	}

	public String getExpireDate(int memberId) throws Exception {
		String expireDate = "";
		String whereCause = "";
		try {
			whereCause = " AND DOC_STATUS = 'SV' ";
			whereCause += " AND CUSTOMER_ID = " + memberId;
			whereCause += " ORDER BY ORDER_DATE ";
			Order[] orders = new MOrder().search(whereCause);
			if (orders == null || orders.length == 0) return expireDate;

			whereCause = " AND ISCANCEL = 'N' ";
			whereCause += " AND ORDER_ID = " + orders[orders.length - 1].getId();
			whereCause += " ORDER BY SHIPPING_DATE ";
			OrderLine[] lines = new MOrderLine().search(whereCause);
			if (lines == null || lines.length == 0) return expireDate;
			else expireDate = lines[lines.length - 1].getShippingDate();
		} catch (Exception e) {
			throw e;
		}
		return expireDate;
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}