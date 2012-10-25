package com.isecinc.pens.web.customer;

import java.sql.Connection;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.District;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MContact;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MDistrict;
import com.isecinc.pens.model.MTrip;
import com.isecinc.pens.model.MTrxHistory;

/**
 * Customer Action Class
 * 
 * @author Aneak.t
 * @version $Id: ProductAction.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 *          atiz.b : edit for customer prefix code
 */

public class CustomerAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Customer Prepare Form");
		CustomerForm customerForm = (CustomerForm) form;
		Customer customer = null;
		User user = (User) request.getSession(true).getAttribute("user");

		try {
			customer = new MCustomer().find(id);
			if (customer == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			customerForm.setAddresses(new MAddress().lookUp(customer.getId()));
			customerForm.setContacts(new MContact().lookUp(customer.getId()));
			customerForm.setCustomer(customer);
			String whereCause = " AND CUSTOMER_ID = " + customer.getId() + " AND USER_ID = " + user.getId();
			whereCause += " ORDER BY TRIP_ID DESC ";
			Trip[] trips = new MTrip().search(whereCause);
			if (trips != null) customerForm.getCustomer().setTrip(trips[0].getTripDateFrom());

			// get back search key
			if (customerForm.getCriteria().getSearchKey() == null) {
				if (request.getSession(true).getAttribute("CMSearchKey") != null) {
					customerForm.getCriteria().setSearchKey(
							(String) request.getSession(true).getAttribute("CMSearchKey"));
				} else {
					customerForm.getCriteria()
							.setSearchKey((String) request.getSession(true).getAttribute("searchKey"));
				}
			} else {
				request.getSession(true).removeAttribute("CMSearchKey");
			}

			// Save Token
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
		logger.debug("Customer Prepare Form without ID");
		CustomerForm customerForm = (CustomerForm) form;
		try {
			User user = (User) request.getSession(true).getAttribute("user");
			Customer customer = new Customer();

			// Sales Rep.
			customer.setSalesRepresent(user);

			// Default Vat Code .. need
			customer.setVatCode("7");

			// default to active
			customer.setIsActive("Y");

			// default to trip
			customer.setTrip(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
			customer.setPaymentTerm("IM");

			customerForm.setCustomer(customer);

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
		logger.debug("Customer Search");
		CustomerForm customerForm = (CustomerForm) form;
		User user = (User) request.getSession().getAttribute("user");

		try {
			CustomerCriteria criteria = getSearchCriteria(request, customerForm.getCriteria(), this.getClass().toString());
			customerForm.setCriteria(criteria);
			String whereCause = "";
			if (customerForm.getCustomer().getTerritory() != null
					&& !customerForm.getCustomer().getTerritory().trim().equals("")) {
				whereCause += " AND m_customer.TERRITORY = '" + customerForm.getCustomer().getTerritory().trim() + "'";
			}
			if (customerForm.getCustomer().getCode() != null && !customerForm.getCustomer().getCode().trim().equals("")) {
				whereCause += " AND m_customer.CODE LIKE '%"
						+ customerForm.getCustomer().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getName() != null && !customerForm.getCustomer().getName().trim().equals("")) {
				whereCause += " AND m_customer.NAME LIKE '%"
						+ customerForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getIsActive() != null
					&& !customerForm.getCustomer().getIsActive().equals("")) {
				whereCause += " AND m_customer.ISACTIVE = '" + customerForm.getCustomer().getIsActive() + "'";
			}
			// WIT EDIT :04/08/2554 
			if(User.ADMIN.equals(user.getType())){
				
			}else{
			   whereCause += " AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			   whereCause += " AND m_customer.USER_ID = " + user.getId();
			}
			
			if (customerForm.getCustomer().getSearchProvince() != 0) {
				whereCause += " AND m_customer.CUSTOMER_ID IN (select customer_id ";
				whereCause += "from m_address where province_id = " + customerForm.getCustomer().getSearchProvince()
						+ ")";
			}

			Customer[] results = new MCustomer().search(whereCause);
			//Customer[] results = new MCustomer().searchOpt(whereCause);//new method optimize
			customerForm.setResults(results);

			if (results != null) {
				customerForm.getCriteria().setSearchResult(results.length);
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
		CustomerForm customerForm = (CustomerForm) form;
		int customerId = 0;
		try {
			customerId = customerForm.getCustomer().getId();

			// check Token
			if (!isTokenValid(request)) {
				customerForm.setCustomer(new Customer());
				customerForm.getAddresses().clear();
				customerForm.getContacts().clear();
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");

			Customer customer = customerForm.getCustomer();
			
			if(!User.ADMIN.equals(userActive.getRole().getKey()))
				customer.setCustomerType(userActive.getCustomerType().getKey());

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			District d;
			if (customerId == 0) {
				// Prepare Customer Code Prefix
				String codePrefix = "";
				codePrefix += Integer.parseInt(customer.getTerritory());
				boolean baddr = false;
				for (Address a : customerForm.getAddresses()) {
					if (a.getPurpose().equalsIgnoreCase("S")) {
						d = new MDistrict().find(String.valueOf(a.getDistrict().getId()));
						if (d.getCode().length() == 0) d.setCode("99");
						codePrefix += new DecimalFormat("00").format(a.getProvince().getId() - 100);
						customer.setProvince(new DecimalFormat("00").format(a.getProvince().getId() - 100));
						customer.setDistrict(d.getCode());
						baddr = true;
						break;
					}
				}
				if (!baddr) {
					for (Address a : customerForm.getAddresses()) {
						if (a.getPurpose().equalsIgnoreCase("B")) {
							d = new MDistrict().find(String.valueOf(a.getDistrict().getId()));
							if (d.getCode().length() == 0) d.setCode("99");
							codePrefix += new DecimalFormat("00").format(a.getProvince().getId() - 100);
							customer.setProvince(new DecimalFormat("00").format(a.getProvince().getId() - 100));
							customer.setDistrict(d.getCode());
							break;
						}
					}
				}
				customer.setCodePrefix(codePrefix);
			}

			// Save Customer
			if (!new MCustomer().save(customer, userActive.getId(),userActive.getUserName(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}

			// Save customer to current user's trip.
			if (customerId == 0) {
				Trip trip = new Trip();
				// A-neak.t 28/12/2010
				String[] date = null;
				if (ConvertNullUtil.convertToString(customerForm.getCustomer().getTrip()).trim().length() == 0) {
					date = DateToolsUtil.getCurrentDateTime("dd/MM/yyyy").split("/");
					customerForm.getCustomer().setTrip(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
				} else {
					date = customerForm.getCustomer().getTrip().split("/");
				}

				trip.setDay(date[0]);
				trip.setMonth(date[1]);
				trip.setYear(date[2]);
				trip.setCustomer(customer);
				trip.setCreatedBy(userActive);
				trip.setUpdatedBy(userActive);
				trip.setUser(userActive);
				new MTrip().save(trip, userActive.getId(), conn);
			}

			// Save Address
			for (Address address : customerForm.getAddresses()) {
				address.setCustomerId(customer.getId());
				new MAddress().save(address, userActive.getId(), conn);
			}

			// Save Contact
			for (Contact contact : customerForm.getContacts()) {
				contact.setCustomerId(customer.getId());
				new MContact().save(contact, userActive.getId(), conn);
			}

			// Trx History
			TrxHistory trx = new TrxHistory();
			trx.setTrxModule(TrxHistory.MOD_CUSTOMER);
			if (customerId == 0) trx.setTrxType(TrxHistory.TYPE_INSERT);
			else trx.setTrxType(TrxHistory.TYPE_UPDATE);
			trx.setRecordId(customer.getId());
			trx.setUser(userActive);
			new MTrxHistory().save(trx, userActive.getId(), conn);
			// Trx History --end--

			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());

			// Save Token
			saveToken(request);
		} catch (Exception e) {
			customerForm.getCustomer().setId(customerId);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return "prepare";
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "view";
	}

	@Override
	protected void setNewCriteria(ActionForm form) {
		CustomerForm customerForm = (CustomerForm) form;
		customerForm.setCriteria(new CustomerCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
