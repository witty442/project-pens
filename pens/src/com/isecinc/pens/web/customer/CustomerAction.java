package com.isecinc.pens.web.customer;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;
import util.DateToolsUtil;
import util.ExcelHeader;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Customer;
import com.isecinc.pens.bean.District;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.TrxHistory;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.EnvProperties;
import com.isecinc.pens.inf.helper.FileUtil;
import com.isecinc.pens.inf.helper.Utils;
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

	private int MAX_ROW_PAGE = 50;
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
			
			if(Utils.isNull(customer.getPrintType()).equals("")){
				customer.setPrintType("H");//Printype HEAD BRANCH
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
		if (request.getParameter("action").equalsIgnoreCase("edit2")) return "viewEdit";
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
			//default Print Type
			customer.setPrintType("H");
			
			// Sales Rep.
			customer.setSalesRepresent(user);

			// Default Vat Code .. need
			customer.setVatCode("7");

			// default to active
			customer.setIsActive("Y");

			// default to trip
			customer.setTrip(DateToolsUtil.getCurrentDateTime("dd/MM/yyyy"));
			customer.setPaymentTerm("IM");

			customer.setDistrict("-1");
			customerForm.setCustomer(customer);

			//default TripDay1 = day of currentDate
			customer.setTripDay(DateToolsUtil.getDayOfDate(new Date()));
			
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
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        int start = 0;
        int end = 50;

		try {
			//clear session
			request.getSession().removeAttribute("tripPageMap");
			
			CustomerCriteria criteria = getSearchCriteria(request, customerForm.getCriteria(), this.getClass().toString());
			customerForm.setCriteria(criteria);
			String whereCause = "";
			if (customerForm.getCustomer().getTerritory() != null
					&& !customerForm.getCustomer().getTerritory().trim().equals("")) {
				whereCause += "\n AND m_customer.TERRITORY = '" + customerForm.getCustomer().getTerritory().trim() + "'";
			}
			if (customerForm.getCustomer().getCode() != null && !customerForm.getCustomer().getCode().trim().equals("")) {
				whereCause += "\n AND m_customer.CODE LIKE '%"
						+ customerForm.getCustomer().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getName() != null && !customerForm.getCustomer().getName().trim().equals("")) {
				whereCause += "\n AND m_customer.NAME LIKE '%"
						+ customerForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getIsActive() != null
					&& !customerForm.getCustomer().getIsActive().equals("")) {
				whereCause += "\n AND m_customer.ISACTIVE = '" + customerForm.getCustomer().getIsActive() + "'";
			}
			// WIT EDIT :04/08/2554 
			if(!User.ADMIN.equals(user.getType())){
			   whereCause += "\n AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			   whereCause += "\n AND m_customer.USER_ID = " + user.getId();
			}
			
			if ( !"".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) && !"0".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) ){
				whereCause += "\n AND m_address.district_id = " + customerForm.getCustomer().getDistrict() + "";
			}
			
			if (customerForm.getCustomer().getSearchProvince() != 0) {
				whereCause += "\n AND m_customer.CUSTOMER_ID IN (select customer_id ";
				whereCause += "\n from m_address where province_id = " + customerForm.getCustomer().getSearchProvince()
						 + ")";
			}
			
			//Check Disp have Trip Or no
			request.getSession().setAttribute("dispHaveTrip", "N");
			if (  !Utils.isNull(customerForm.getCustomer().getDispHaveTrip()).equals("") ) {
				whereCause +="\n AND ( m_customer.trip_day <> 0 or m_customer.trip_day2 <>0 or m_customer.trip_day3 <> 0) ";
			    request.getSession().setAttribute("dispHaveTrip", "Y");
			}
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//** get From Session **/
			//currPage = customerForm.getCurPage();
			//totalRow = customerForm.getTotalRow();
			
			/** Get Case Trip **/
			if ( Utils.isNull(request.getSession().getAttribute("dispHaveTrip")).equalsIgnoreCase("Y")) {
				
				//Trip Day 1 - 23 +98
				List<Customer> tripPageList = new MCustomer().getTripPage(conn, whereCause, user);
				request.getSession().setAttribute("tripPageList", tripPageList);
			    totalPage = tripPageList.size();
			    
			    // Get Trip Case Search By Customer
			    if(tripPageList != null && tripPageList.size() >0){
			    	for(int i=0;i<tripPageList.size();i++){
			    		//tripBySearchSqlIn += "'"+tripPageList.get(i).getTripDay()+"',";
			    		// set default CurPage
			    		if(i==0){
			    			currPage =Integer.parseInt(tripPageList.get(i).getTripDay());
			    			break;
			    		}
			    	}
			    	/*if(tripBySearchSqlIn.length() >0){
			    		tripBySearchSqlIn = tripBySearchSqlIn.substring(0,tripBySearchSqlIn.length()-1);
			    	}*/
			    }
			    
			}else{
				totalRow = new MCustomer().getTotalRowCustomer(conn, whereCause, user);
			    if(totalRow > 0){
			    	double t = new Double(totalRow)/new Double(MAX_ROW_PAGE);
			    	logger.debug("t:"+t);
			    	BigDecimal totalPageB = new BigDecimal(t);
			    	totalPageB = totalPageB.setScale(0,BigDecimal.ROUND_UP);
			    	
			    	logger.debug("totalPageB:"+totalPageB);
			    	
				    totalPage = totalPageB.intValue();
			    }
			}
			logger.debug("showTrip:"+request.getAttribute("dispHaveTrip"));
			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			 Customer[] results = null;
			 if ( Utils.isNull(request.getSession().getAttribute("dispHaveTrip")).equalsIgnoreCase("Y")) {
			
				String currPageSqlIn = "'"+currPage+"'";
				results = new MCustomer().searchOptByTrip(conn,whereCause,user,currPageSqlIn,customerForm.getCustomer().getDispTotalInvoice());//new method optimize
			}else{
				end = 50;
				whereCause +="\n limit "+start+","+end;
				
				results = new MCustomer().searchOpt(conn,whereCause,user,start,customerForm.getCustomer().getDispTotalInvoice());//new method optimize
			}
			customerForm.setResults(results);
			customerForm.setTotalPage(totalPage);
			customerForm.setTotalRow(totalRow);
			customerForm.setCurPage(currPage);
			
			Customer customer = customerForm.getCustomer();
			logger.debug("customer getDistrict:"+customer.getDistrict());
			
			//customer.setDistrict(district)

			if (results != null) {
				customerForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return "search";
	}
	
	public ActionForward searchPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("Customer Search Page");
		CustomerForm customerForm = (CustomerForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        int start = 0;
        int end = 50;
		try {
			CustomerCriteria criteria = getSearchCriteria(request, customerForm.getCriteria(), this.getClass().toString());
			customerForm.setCriteria(criteria);
			String whereCause = "";
			if (customerForm.getCustomer().getTerritory() != null
					&& !customerForm.getCustomer().getTerritory().trim().equals("")) {
				whereCause += "\n AND m_customer.TERRITORY = '" + customerForm.getCustomer().getTerritory().trim() + "'";
			}
			if (customerForm.getCustomer().getCode() != null && !customerForm.getCustomer().getCode().trim().equals("")) {
				whereCause += "\n AND m_customer.CODE LIKE '%"
						+ customerForm.getCustomer().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getName() != null && !customerForm.getCustomer().getName().trim().equals("")) {
				whereCause += "\n AND m_customer.NAME LIKE '%"
						+ customerForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getIsActive() != null
					&& !customerForm.getCustomer().getIsActive().equals("")) {
				whereCause += "\n AND m_customer.ISACTIVE = '" + customerForm.getCustomer().getIsActive() + "'";
			}
			// WIT EDIT :04/08/2554 
			if(!User.ADMIN.equals(user.getType())){
			   whereCause += "\n AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			   whereCause += "\n AND m_customer.USER_ID = " + user.getId();
			}
			
			if ( !"".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) && !"0".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) ){
				whereCause += "\n AND m_address.district_id = " + customerForm.getCustomer().getDistrict() + "";
			}
			
			if (customerForm.getCustomer().getSearchProvince() != 0) {
				whereCause += "\n AND m_customer.CUSTOMER_ID IN (select customer_id ";
				whereCause += "\n from m_address where province_id = " + customerForm.getCustomer().getSearchProvince()
						 + ")";
			}
			
			if ( Utils.isNull(request.getSession().getAttribute("dispHaveTrip")).equals("Y") ) {
			    whereCause +="\n AND ( m_customer.trip_day <> 0 or m_customer.trip_day2 <>0 or m_customer.trip_day3 <> 0) ";
			}
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//** get From Session **/
			currPage = customerForm.getCurPage();
			totalPage = customerForm.getTotalPage();
			totalRow = customerForm.getTotalRow();

			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			start = (currPage-1)*MAX_ROW_PAGE;
			end =  MAX_ROW_PAGE;
			
			logger.debug("start["+start+"]end["+end+"]");

			Customer[] results = null;
			if (Utils.isNull(request.getSession().getAttribute("dispHaveTrip")).equalsIgnoreCase("Y")) {
				String startTrip = "'"+currPage+"'";
				results = new MCustomer().searchOptByTrip(conn,whereCause,user,startTrip,customerForm.getCustomer().getDispTotalInvoice());//new method optimize
			}else{
				whereCause +="\n limit "+start+","+end;
				results = new MCustomer().searchOpt(conn,whereCause,user,start,customerForm.getCustomer().getDispTotalInvoice());//new method optimize
			}
			
			//logger.debug("results.length:"+results.length);
			
			customerForm.setResults(results);
			customerForm.setTotalPage(totalPage);
			customerForm.setTotalRow(totalRow);
			customerForm.setCurPage(currPage);
			
			if (results != null) {
				customerForm.getCriteria().setSearchResult(results.length);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception e){
				
			}
		}
		return mapping.findForward("search"); 
	}
	
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		CustomerForm customerForm = (CustomerForm) form;
		try {
			CustomerCriteria criteria = getSearchCriteria(request, customerForm.getCriteria(), this.getClass().toString());
			customerForm.setCriteria(criteria);
			String whereCause = "";
			if (customerForm.getCustomer().getTerritory() != null
					&& !customerForm.getCustomer().getTerritory().trim().equals("")) {
				whereCause += "\n AND m_customer.TERRITORY = '" + customerForm.getCustomer().getTerritory().trim() + "'";
			}
			if (customerForm.getCustomer().getCode() != null && !customerForm.getCustomer().getCode().trim().equals("")) {
				whereCause += "\n AND m_customer.CODE LIKE '%"
						+ customerForm.getCustomer().getCode().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getName() != null && !customerForm.getCustomer().getName().trim().equals("")) {
				whereCause += "\n AND m_customer.NAME LIKE '%"
						+ customerForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}
			if (customerForm.getCustomer().getIsActive() != null
					&& !customerForm.getCustomer().getIsActive().equals("")) {
				whereCause += "\n AND m_customer.ISACTIVE = '" + customerForm.getCustomer().getIsActive() + "'";
			}
			// WIT EDIT :04/08/2554 
			if(!User.ADMIN.equals(user.getType())){
			   whereCause += "\n AND m_customer.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "'";
			   whereCause += "\n AND m_customer.USER_ID = " + user.getId();
			}
			
			if ( !"".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) && !"0".equals(Utils.isNull(customerForm.getCustomer().getDistrict())) ){
				whereCause += "\n AND m_address.district_id = " + customerForm.getCustomer().getDistrict() + "";
			}
			
			if (customerForm.getCustomer().getSearchProvince() != 0) {
				whereCause += "\n AND m_customer.CUSTOMER_ID IN (select customer_id ";
				whereCause += "\n from m_address where province_id = " + customerForm.getCustomer().getSearchProvince()
						 + ")";
			}
			
			if ( Utils.isNull(request.getSession().getAttribute("dispHaveTrip")).equals("Y") ) {
			    whereCause +="\n AND ( m_customer.trip_day <> 0 or m_customer.trip_day2 <>0 or m_customer.trip_day3 <> 0) ";
			}
			whereCause +=" order by m_customer.code asc";
			//logger.debug("whereCause:"+whereCause);
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			Customer[] results  = new MCustomer().searchOpt(conn,whereCause,user,0,"dispTotalInvoice");
			if(results != null){
				//get Excel
				StringBuffer h = new StringBuffer("");
				h.append(ExcelHeader.EXCEL_HEADER);
				h.append("<table border='1'> \n");
				h.append("<tr><td colspan='5'>รายงานยอดหนี้คงค้าง</td> </tr> \n");
				h.append("</table> \n");
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append(" <td>No</td> \n");
				h.append(" <td>หมายเลขลูกค้า</td> \n");
				h.append(" <td>ชื่อ</td> \n");
				h.append(" <td>วงเงินสินเชื่อ</td> \n");
				h.append(" <td>ยอดบิลค้างชำระ</td> \n");
				h.append("</tr> \n");
				for(int i=0;i<results.length;i++){
					Customer c = results[i];
					h.append("<tr> \n");
					h.append(" <td class='num'>"+c.getNo()+"</td> \n");
					h.append(" <td class='text'>"+c.getCode()+"</td> \n");
					h.append(" <td class='text'>"+c.getName()+"</td> \n");
					h.append(" <td class='currency'>"+c.getCreditLimit()+"</td> \n");
					h.append(" <td class='currency'>"+c.getTotalInvoice()+"</td> \n");
					h.append("</tr> \n");
				}
				h.append("</table> \n");
				
				//export to excel
				java.io.OutputStream out = response.getOutputStream();
				response.setHeader("Content-Disposition", "attachment; filename=data.xls");
				response.setContentType("application/vnd.ms-excel");
				
				Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
				w.write(h.toString());
			    w.flush();
			    w.close();

			    out.flush();
			    out.close();
			    
			}else{
				request.setAttribute("Message","Data not found");
			}
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
			e.printStackTrace();
			return mapping.findForward("search"); 
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("search"); 
	}
	
	
	public ActionForward toCreateNewReqpromotion(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("toCreateNewReqpromotion");
		CustomerForm customerForm = (CustomerForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
      
		try {
			if (request.getParameter("shotcut_customerId") != null) {
				int customerId = Integer.parseInt(request.getParameter("shotcut_customerId"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}finally{
			try{
				if(conn != null){
					conn.close();conn=null;
				}
			}catch(Exception e){
				
			}
		}
		return mapping.findForward("toCreateNewReqpromotion"); 
	}
	
	protected String search_V1(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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

			//Customer[] results = new MCustomer().search(whereCause);
			Customer[] results = new MCustomer().searchOpt(whereCause,user,0,customerForm.getCustomer().getDispTotalInvoice());//new method optimize
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
		EnvProperties env = EnvProperties.getInstance();
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

			//Set Parameter printType
			String printType = Utils.isNull(customer.getPrintType());
			String printTax = Utils.isNull(customer.getPrintTax());
			String printHeadBranchDesc = Utils.isNull(customer.getPrintHeadBranchDesc());
			
			logger.debug("printType["+printType+"]");
			logger.debug("printTax["+printTax+"]");
			logger.debug("printHeadBranchDesc["+printHeadBranchDesc+"]");
			
			if(printType.equals("H")){
				customer.setPrintBranchDesc("");
			}
			if(printTax.equals("")){
				customer.setPrintTax("N");
			}
			if(printHeadBranchDesc.equals("")){
				customer.setPrintHeadBranchDesc("N");
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
			
			//Write Image File To Local
			if(customerForm.getImageFile() != null && !Utils.isNull(customerForm.getImageFile().getFileName()).equals("")){
				//	Customer c = new MCustomer().find(String.valueOf(customerId));
				
				//rename file name to customerCode
				String imageFileNametemp = Utils.isNull(customerForm.getImageFile().getFileName());
				String fileName = customer.getCode() +imageFileNametemp.substring(imageFileNametemp.lastIndexOf("."),imageFileNametemp.length());
				
				logger.debug("imageFileNametemp:"+imageFileNametemp);
				logger.debug("fileName:"+fileName);
				
				String imagePath = env.getProperty("path.image.local");//"D:/SalesApp/Images/";
				//Delete file older
				if( !Utils.isNull(customer.getImageFileName()).equals("") && !Utils.isNull(customer.getImageFileName()).equals(imagePath)){
					FileUtil.deleteFile(Utils.isNull(customer.getImageFileName()));
				}
				
			   FileUtil.createDir(imagePath);//Create HomePath
			   String pathLocalImage = imagePath+fileName;
			   FileUtil.writeImageFile(pathLocalImage, customerForm.getImageFile().getInputStream());
			   
			   customer.setImageFileName(pathLocalImage);
			   
			   // Save updateImageFile
			   new MCustomer().updateImageFile(customer, userActive.getId(),userActive.getUserName(), conn);
			}
			
			// Commit Transaction
			conn.commit();
            //Add message Success
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
	
	public ActionForward saveEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		CustomerForm customerForm = (CustomerForm) form;
		int customerId = 0;
		EnvProperties env = EnvProperties.getInstance();
		try {
			customerId = customerForm.getCustomer().getId();

			// check Token
			if (!isTokenValid(request)) {
				customerForm.setCustomer(new Customer());
				customerForm.getAddresses().clear();
				customerForm.getContacts().clear();
				return mapping.findForward("prepare"); 
			}
			User userActive = (User) request.getSession(true).getAttribute("user");
			Customer customer = customerForm.getCustomer();

			
			//Set Parameter printType
			String printType = Utils.isNull(customer.getPrintType());
			String printTax = Utils.isNull(customer.getPrintTax());
			String printHeadBranchDesc = Utils.isNull(customer.getPrintHeadBranchDesc());
			
			String airpayFlag = Utils.isNull(customer.getAirpayFlag());
			
			logger.debug("printType["+printType+"]");
			logger.debug("printTax["+printTax+"]");
			logger.debug("printHeadBranchDesc["+printHeadBranchDesc+"]");
			logger.debug("airpayFlag["+airpayFlag+"]");
			
			if(printType.equals("H")){
				customer.setPrintBranchDesc("");
			}
			if(printTax.equals("")){
				customer.setPrintTax("N");
			}
			if(printHeadBranchDesc.equals("")){
				customer.setPrintHeadBranchDesc("N");
			}
			if(airpayFlag.equals("")){
				customer.setAirpayFlag("N");
			}
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
            
			//Write Image File To Local
			if(customerForm.getImageFile() != null && !Utils.isNull(customerForm.getImageFile().getFileName()).equals("")){
				//rename file name to customerCode
				String imageFileNametemp = Utils.isNull(customerForm.getImageFile().getFileName());
				String fileName = customer.getCode() +imageFileNametemp.substring(imageFileNametemp.lastIndexOf("."),imageFileNametemp.length());
				
				String imagePath = env.getProperty("path.image.local");//"D:/SalesApp/Images/";
				//Delete file older
				if( !Utils.isNull(customer.getImageFileName()).equals("") && !Utils.isNull(customer.getImageFileName()).equals(imagePath)){
					FileUtil.deleteFile(Utils.isNull(customer.getImageFileName()));
				}
				
			   FileUtil.createDir(imagePath);//Create HomePath
			   String pathLocalImage = imagePath+fileName;
			   FileUtil.writeImageFile(pathLocalImage, customerForm.getImageFile().getInputStream());
			   customer.setImageFileName(pathLocalImage);
			}
			
			// Save Customer tax_no ,print only
			new MCustomer().update(customer, userActive.getId(),userActive.getUserName(), conn);
			
			// Save Address
			logger.debug("AddressArr:"+customerForm.getAddresses().size());
			for (Address address : customerForm.getAddresses()) {
				address.setCustomerId(customer.getId());
				new MAddress().save(address, userActive.getId(), conn);
			}

			// Save Contact
			for (Contact contact : customerForm.getContacts()) {
				contact.setCustomerId(customer.getId());
				new MContact().save(contact, userActive.getId(), conn);
			}
						
			// Commit Transaction
			conn.commit();
			
			//searh refresh data
			customer = new MCustomer().find(String.valueOf(customerId));
			customerForm.setCustomer(customer);
			customerForm.setAddresses(new MAddress().lookUp(customer.getId()));
			customerForm.setContacts(new MContact().lookUp(customer.getId()));
			
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
			return mapping.findForward("prepare"); 
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("view"); 
	}
	
	public ActionForward saveEditCredit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		CustomerForm customerForm = (CustomerForm) form;
		int customerId = 0;
		EnvProperties env = EnvProperties.getInstance();
		try {
			customerId = customerForm.getCustomer().getId();

			// check Token
			if (!isTokenValid(request)) {
				customerForm.setCustomer(new Customer());
				customerForm.getAddresses().clear();
				customerForm.getContacts().clear();
				return mapping.findForward("prepare"); 
			}
			User userActive = (User) request.getSession(true).getAttribute("user");
			Customer customer = customerForm.getCustomer();

			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
            
			//Write Image File To Local
			if(customerForm.getImageFile() != null && !Utils.isNull(customerForm.getImageFile().getFileName()).equals("")){
				//rename file name to customerCode
				String imageFileNametemp = Utils.isNull(customerForm.getImageFile().getFileName());
				String fileName = customer.getCode() +imageFileNametemp.substring(imageFileNametemp.lastIndexOf("."),imageFileNametemp.length());
				
				String imagePath = env.getProperty("path.image.local");//"D:/SalesApp/Images/";
				//Delete file older
				if( !Utils.isNull(customer.getImageFileName()).equals("") && !Utils.isNull(customer.getImageFileName()).equals(imagePath)){
					FileUtil.deleteFile(Utils.isNull(customer.getImageFileName()));
				}
				
			   FileUtil.createDir(imagePath);//Create HomePath
			   String pathLocalImage = imagePath+fileName;
			   /** Write File Image To Local **/
			   FileUtil.writeImageFile(pathLocalImage, customerForm.getImageFile().getInputStream());
			   customer.setImageFileName(pathLocalImage);
			}
			
			// Save Customer tax_no ,print only
			new MCustomer().updateCredit(customer, userActive.getId(),userActive.getUserName(), conn);
						
			// Commit Transaction
			conn.commit();
			
			//searh refresh data
			customer = new MCustomer().find(String.valueOf(customerId));
			customerForm.setCustomer(customer);
			customerForm.setAddresses(new MAddress().lookUp(customer.getId()));
			customerForm.setContacts(new MContact().lookUp(customer.getId()));
			
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
			return mapping.findForward("prepare"); 
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("view"); 
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
