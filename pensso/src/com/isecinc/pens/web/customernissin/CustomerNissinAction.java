package com.isecinc.pens.web.customernissin;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.CustomerNissin;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomerNissin;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.EnvProperties;
import com.pens.util.UserUtils;

/**
 * Customer Action Class
 * 
 * @author Witty
 * @version 
 * 
 *     
 */

public class CustomerNissinAction extends I_Action {

	//private int MAX_ROW_PAGE = 50;
	public static int pageSize = 50;
	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Customer Prepare Form");
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		CustomerNissin customer = null;
		//User user = (User) request.getSession(true).getAttribute("user");
		try {
			customer = new MCustomerNissin().findOpt(Utils.convertStrToLong(id));
			if (customer == null) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			logger.debug("canEdit:"+customer.isCanEdit());
			customerNissinForm.setCustomer(customer);
			
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
		logger.debug("CustomerNissin Prepare Form without ID");
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		User user = (User) request.getSession(true).getAttribute("user");
		try {
			CustomerNissin customer = new CustomerNissin();
			//customer.setDistrictId("-1");
			customer.setCanEdit(true);
			customerNissinForm.setCustomer(customer);
			
			//save criteria
			CustomerNissinCriteria criteria = getSearchCriteria(request, customerNissinForm.getCriteria(), this.getClass().toString());
			request.getSession().setAttribute("_cust_criteria", criteria);
			
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
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        CustomerNissinCriteria criteria = null;
        String whereCause = "";
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			//init connection
			conn = DBConnectionApps.getInstance().getConnection();
			
			if("newsearch".equalsIgnoreCase(action)){
				criteria = getSearchCriteria(request, customerNissinForm.getCriteria(), this.getClass().toString());
				
				//save criteria
				request.getSession().setAttribute("_cust_criteria", criteria);
				customerNissinForm.setCriteria(criteria);
			}else if("back".equalsIgnoreCase(action)){
				criteria = (CustomerNissinCriteria)request.getSession().getAttribute("_cust_criteria");
				
				action = "newsearch";
				
				customerNissinForm.setCriteria(criteria);
				logger.debug("cri Customer:"+customerNissinForm.getCustomer());
			}
			
			if(customerNissinForm.getCustomer() != null){
				if (customerNissinForm.getCustomer().getName() != null && !customerNissinForm.getCustomer().getName().trim().equals("")) {
					whereCause += "\n AND c.NAME LIKE '%"
							+ customerNissinForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
							+ "%' ";
				}
				if ( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getDistrictId()))){
					whereCause += "\n AND c.district_id = " + customerNissinForm.getCustomer().getDistrictId() + "";
				}
		
				if ( !Utils.isNull(customerNissinForm.getCustomer().getProvinceId()).equals("")) {
					whereCause += "\n AND c.province_id = " + customerNissinForm.getCustomer().getProvinceId();
				}
			}
			//For Sales Nissin 
			if( !user.getUserName().equalsIgnoreCase("admin") && UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){
				whereCause += "\n and c.created_by ='"+user.getUserName()+"'";
				whereCause += "\n and c.province_id in (select province_id from pensso.m_province_sales_nis where salesrep_code ='"+user.getUserName()+"')" ;
			}
			
			if("newsearch".equalsIgnoreCase(action)){
				//default currPage = 1
				customerNissinForm.setCurrPage(currPage);
				
				//get Total Record
				customerNissinForm.setTotalRecord(new MCustomerNissin().getTotalRowCustomer(conn, whereCause, user));
				//calc TotalPage
				customerNissinForm.setTotalPage(Utils.calcTotalPage(customerNissinForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > customerNissinForm.getTotalRecord()){
				   endRec = customerNissinForm.getTotalRecord();
			    }
			    customerNissinForm.setStartRec(startRec);
			    customerNissinForm.setEndRec(endRec);
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > customerNissinForm.getTotalRecord()){
				   endRec = customerNissinForm.getTotalRecord();
			    }
			    customerNissinForm.setStartRec(startRec);
			    customerNissinForm.setEndRec(endRec);
			}

			logger.debug("showTrip:"+request.getAttribute("dispHaveTrip"));
			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			CustomerNissin[] results = new MCustomerNissin().searchOpt(conn,whereCause,user,currPage);//new method optimize
			customerNissinForm.setResults(results);
			
			CustomerNissin customer = customerNissinForm.getCustomer();
			//logger.debug("customer getDistrict:"+customer.getDistrict());

			if (results != null) {
				customerNissinForm.getCriteria().setSearchResult(results.length);
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
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
        int currPage = 1;
        int totalRow = 0;
        int totalPage = 0;
        int start = 0;
        int end = 50;
		try {
			CustomerNissinCriteria criteria = getSearchCriteria(request, customerNissinForm.getCriteria(), this.getClass().toString());
			customerNissinForm.setCriteria(criteria);
			String whereCause = "";
			
			if (customerNissinForm.getCustomer().getName() != null && !customerNissinForm.getCustomer().getName().trim().equals("")) {
				whereCause += "\n AND c.NAME LIKE '%"
						+ customerNissinForm.getCustomer().getName().trim().replace("\'", "\\\'").replace("\"", "\\\"")
						+ "%' ";
			}

			if ( !"".equals(Utils.isNull(customerNissinForm.getCustomer().getDistrictId()))  ){
				whereCause += "\n AND c.district_id = " + customerNissinForm.getCustomer().getDistrictId() + "";
			}
			
			if ( !Utils.isNull(customerNissinForm.getCustomer().getProvinceId()).equals("")) {
				whereCause += "\n AND c.province_id = " + customerNissinForm.getCustomer().getProvinceId();
			}

		    //For Sales Nissin 
			if(UserUtils.userInRole("ROLE_ALL",user,new String[]{User.NIS})){
				whereCause += "\n AND c.created_by ='"+user.getCode()+"'";
			}
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			//** get From Session **/
			currPage = customerNissinForm.getCurPage();
			totalPage = customerNissinForm.getTotalPage();
			totalRow = customerNissinForm.getTotalRow();

			logger.debug("totalRow:"+totalRow);
			logger.debug("totalPage:"+totalPage);
			logger.debug("currPage:"+currPage);
			
			start = (currPage-1)*pageSize;
			end =  pageSize;
			
			logger.debug("start["+start+"]end["+end+"]");

			CustomerNissin[] results = null;
			whereCause +="\n limit "+start+","+end;
			results = new MCustomerNissin().searchOpt(conn,whereCause,user,start);//new method optimize
			
			//logger.debug("results.length:"+results.length);
			
			customerNissinForm.setResults(results);
			customerNissinForm.setTotalPage(totalPage);
			customerNissinForm.setTotalRow(totalRow);
			customerNissinForm.setCurPage(currPage);
			
			if (results != null) {
				customerNissinForm.getCriteria().setSearchResult(results.length);
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
	
	public ActionForward clearFormSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("clearFormSearch");
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		try {
			customerNissinForm.setResults(null);
			customerNissinForm.setCustomer(new CustomerNissin());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		}finally{
			try{
			
			}catch(Exception e){
				
			}
		}
		return mapping.findForward("search"); 
	}
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		long customerId = 0;
		try {
			customerId = customerNissinForm.getCustomer().getId();

			// check Token
			if (!isTokenValid(request)) {
				customerNissinForm.setCustomer(new CustomerNissin());
				return "prepare";
			}

			User userActive = (User) request.getSession(true).getAttribute("user");

			CustomerNissin customer = customerNissinForm.getCustomer();
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			// Save Customer
			if (!new MCustomerNissin().save(customer, userActive.getId(),userActive.getUserName(), conn)) {
				// return with duplicate Document no
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.DUPLICATE).getDesc());
				conn.rollback();
				return "prepare";
			}

			// Commit Transaction
			conn.commit();
			
            //Add message Success
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			
			//search again
			customerNissinForm.setCustomer(new MCustomerNissin().findOpt(customer.getId()));
			
			// Save Token
			saveToken(request);
			
		} catch (Exception e) {
			customerNissinForm.getCustomer().setId(customerId);
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
		return "prepare";
	}
	
	public ActionForward saveEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Connection conn = null;
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		long customerId = 0;
		EnvProperties env = EnvProperties.getInstance();
		try {
			customerId = customerNissinForm.getCustomer().getId();

			// check Token
			if (!isTokenValid(request)) {
				customerNissinForm.setCustomer(new CustomerNissin());
				return mapping.findForward("prepare"); 
			}
			User userActive = (User) request.getSession(true).getAttribute("user");
			CustomerNissin customer = customerNissinForm.getCustomer();

		
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);
            
			// Save Customer tax_no ,print only
			new MCustomerNissin().update(customer, userActive.getId(),userActive.getUserName(), conn);

			// Commit Transaction
			conn.commit();
			
			//searh refresh data
			customer = new MCustomerNissin().find(String.valueOf(customerId));
			customerNissinForm.setCustomer(customer);
		
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			// Save Token
			saveToken(request);
		} catch (Exception e) {
			customerNissinForm.getCustomer().setId(customerId);
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
		CustomerNissinForm customerNissinForm = (CustomerNissinForm) form;
		customerNissinForm.setCriteria(new CustomerNissinCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
