<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.model.MTrip"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="util.DBCPConnectionProvider"%>
<%@page import="com.isecinc.pens.bean.Trip"%>
<%@page import="java.sql.Connection"%>
<%
	String monthFrom = (String)request.getParameter("monthFrom");
	String yearFrom = (String)request.getParameter("yearFrom");
	String monthTo = (String)request.getParameter("monthTo");
	String yearTo = (String)request.getParameter("yearTo");
	String[] sDate = null;
	String[] eDate = null;
	Connection conn = null;
	Trip trip = null;
	Trip[] results = null;
	Trip[] results2 = null;
	StringBuilder whereCause = new StringBuilder();
	int count = 0;
	String msg = "";
	int deleteCount = 0;
	try{
		
		User userActive = (User) request.getSession().getAttribute("user");
		
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		
		// Begin transaction
		conn.setAutoCommit(false);
		
		// Query from source data.
		whereCause.delete(0, whereCause.length());
		
		whereCause.append(" AND MONTH = '" + monthFrom + "'");
		whereCause.append(" AND YEAR = '" + yearFrom + "'");

		results = new MTrip().search(whereCause.toString());
		
		//delete Trip by monthTo,yearTo
		deleteCount = new MTrip().delete(conn,monthTo,yearTo);
		System.out.println("delete count:"+deleteCount);
		
		if(results != null){
			for(Trip t : results){
				// Insert data from source to desctination.
				trip = new Trip();
				trip.setYear(yearTo);
				trip.setMonth(monthTo);
				trip.setDay(t.getDay());
				trip.setCustomer(t.getCustomer());
				trip.setUser(t.getUser());
				trip.setCreatedBy(userActive);
				trip.setUpdatedBy(userActive);
				
				// Save new record.
				new MTrip().save(trip, userActive.getId(), conn);
				count++;
			}
		}
		
		// Commit transaction
		conn.commit();
		msg = "Copy success : " + count + " Records.";
		
	}catch(Exception e){
		//System.out.println(e.toString());
		conn.rollback();
		msg = "Copy fail : " + e.toString();
		e.printStackTrace();	
	}finally{
		try{
			conn.setAutoCommit(true);
			conn.close();
		}catch(Exception e2){}
	}
%>
<%=msg%>