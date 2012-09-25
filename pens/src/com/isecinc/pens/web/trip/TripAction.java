package com.isecinc.pens.web.trip;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MTrip;
import com.isecinc.pens.model.MUser;

/**
 * Trip Action Class
 * 
 * @author Witty.B
 * @version $Id: TripAction.java,v 1.0 19/10/2010 00:00:00
 * 
 * Modifier : A-neak.t	12/10/2010
 * 			 add method save.
 * 
 */

public class TripAction extends I_Action {

	/**
	 * Prepare
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Trip Prepare Form");
		TripForm tripForm = (TripForm) form;
		Trip trip = null;
		String returnText = "";
		
		try {
			if (request.getParameter("type").equalsIgnoreCase(SystemElements.USER)){
				returnText = "prepare_1";
			}else{
				returnText = "prepare_2";
			}
			
			trip = new MTrip().find(id);
			tripForm.setTrip(trip);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.getMessage());
			throw e;
		}
		
		return returnText;
	}

	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		logger.debug("Trip Prepare Form without ID");
		TripForm tripForm = (TripForm) form;
		String returnText = "";
		
		try {
			if (request.getParameter("type").equalsIgnoreCase(SystemElements.USER)){
				returnText = "prepare_1";
			}else{
				returnText = "prepare_2";
			}
			Trip trip = new Trip();
			tripForm.setTrip(trip);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.getMessage());
		}
		return returnText;
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Trip Search");
		TripForm tripForm = (TripForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String type = (String)request.getParameter("type") != null ? (String)request.getParameter("type") : (String)request.getAttribute("type");
		String returnText = "";
		
		try {
			if(SystemElements.USER.equalsIgnoreCase(type)){
				returnText = "search_1";
			}else{
				returnText = "search_2";
			}
			
			if (type == null || type.equals("")) {
				type = (String) request.getAttribute("type");
			}
			
			TripCriteria criteria = getSearchCriteria(request, tripForm.getCriteria(), this.getClass().toString());
			if(request.getAttribute("searchKey") != null){
				criteria.setSearchKey((String)request.getAttribute("searchKey"));
			}else{
				request.getSession(true).setAttribute("searchKey", criteria.getSearchKey());
			}
			
			tripForm.setCriteria(criteria);
			String whereCause = "";
			String tripDateFrom = ConvertNullUtil.convertToString(tripForm.getCriteria().getTrip().getTripDateFrom());
			String tripDateTo = ConvertNullUtil.convertToString(tripForm.getCriteria().getTrip().getTripDateTo());
			// tripDate Format dd/MM/YY
			if (!tripDateFrom.equals("") && !tripDateTo.equals("")) {
				whereCause += " AND CONCAT(YEAR,MONTH,DAY) >= '" + tripDateFrom.split("/")[2] + tripDateFrom.split("/")[1] + tripDateFrom.split("/")[0] + "'";
				whereCause += " AND CONCAT(YEAR,MONTH,DAY) <= '" + tripDateTo.split("/")[2] + tripDateTo.split("/")[1] + tripDateTo.split("/")[0] + "'";
			}

			if(!type.equalsIgnoreCase(SystemElements.ADMIN)){
				whereCause += " AND USER_ID = " + user.getId();	
			}else{
				whereCause += " AND USER_ID = " + tripForm.getTrip().getUser().getId();
			}
			whereCause += " ORDER BY YEAR, MONTH, DAY, LINE_NO ";
			Trip[] results = new MTrip().search(whereCause);
			tripForm.setResults(results);

			if (results != null) {
				tripForm.getCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
			
			if(request.getParameter("rf") == null){
				request.setAttribute("rf", "Y");
			}
			if(request.getParameter("sort") != null){
				request.setAttribute("sort", request.getParameter("sort"));
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() 
					+ e.getMessage());
		}
		
		return returnText;
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		TripForm tripForm = (TripForm) form;
		Trip trip = null;
		
		try {

			String trip_date = request.getParameter("trip_date") != null ? (String) request.getParameter("trip_date") : "";
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin Transaction
			conn.setAutoCommit(false);

			// do Something
			trip = new Trip();
			trip.setDay(trip_date.split("/")[0]);
			trip.setMonth(trip_date.split("/")[1]);
			trip.setYear(trip_date.split("/")[2]);
			trip.setUser(new MUser().find(String.valueOf(tripForm.getTrip().getUser().getId())));
			trip.setCustomer(new MCustomer().find(String.valueOf(tripForm.getTrip().getCustomer().getId())));
			
			new MTrip().save(trip, userActive.getId(), conn);
			
			// Commit Transaction
			conn.commit();
			//
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", tripForm.getCriteria().getSearchKey());
			
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return "re-search";
	}

	/**
	 * Save new sorting trip.
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward saveNewSort(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		Connection conn = null;
		TripForm tripForm = (TripForm) form;
		Trip trip = null;
		int userId = 0;
		
		try {
			userId = tripForm.getTrip().getUser().getId();
			User userActive = (User) request.getSession(true).getAttribute("user");
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin transaction
			conn.setAutoCommit(false);
			
			// Transaction
			int i = 0;
			for(String id : tripForm.getIds()){
				trip = new MTrip().find(id);
				trip.setLineNo(String.valueOf(i + 1));
				trip.setCustomer(new MCustomer().find(String.valueOf(tripForm.getCustomerId()[i])));
				trip.setUser(new MUser().find(String.valueOf(userId)));
				new MTrip().save(trip, userActive.getId(), conn);
				i++;
			}
			
			// Commit transaction
			conn.commit();
			
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {}
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
		}finally{
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return mapping.findForward("re-search");
	}
	
	@Override
	protected void setNewCriteria(ActionForm form) {
		TripForm tripForm = (TripForm) form;
		tripForm.setCriteria(new TripCriteria());
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Delete Method 
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward delete(ActionMapping mapping, ActionForm form,
						HttpServletRequest request, HttpServletResponse response){
		TripForm tripForm = (TripForm)form;
		Connection conn = null;
		Statement stmt = null;
		
		try {
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			// Begin transaction
			conn.setAutoCommit(false);
			
			String tripId = (String)request.getParameter("tripId");
			
			tripId = tripId.substring(0, tripId.length() - 1);
			String sql = "DELETE FROM " + MTrip.TABLE_NAME + " WHERE " + MTrip.COLUMN_ID + " IN (" + tripId + ")";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			
			// Commit Transaction
			conn.commit();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_FAIL).getDesc()
					+ e.getMessage());
			try {
				conn.rollback();
			} catch (Exception e2) {}
		} finally {
			request.setAttribute("type", SystemElements.ADMIN);
			request.setAttribute("searchKey", tripForm.getCriteria().getSearchKey());
			
			try {
				conn.setAutoCommit(true);
				stmt.close();
				conn.close();
			} catch (Exception e2) {}
		}
		
		return mapping.findForward("re-search");
	}
	
}
