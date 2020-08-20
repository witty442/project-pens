<%@ page language="java" contentType="text/html; charset=TIS-620" pageEncoding="TIS-620"%>
<%@page import="com.isecinc.pens.bean.User"%>
<%@page import="com.isecinc.pens.model.MUser"%>
<%@page import="com.isecinc.pens.model.MCustomer"%>
<%@page import="com.isecinc.pens.model.MTrip"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="com.pens.util.*"%>
<%@page import="com.isecinc.pens.bean.Trip"%>
<%@page import="java.sql.Connection"%>
<%
	String startDate = (String)request.getParameter("sDate");
	String endDate = (String)request.getParameter("eDate");
	String[] sDate = null;
	String[] eDate = null;
	Connection conn = null;
	Trip trip = null;
	Trip[] results = null;
	Trip[] results2 = null;
	StringBuilder whereCause = new StringBuilder();
	int count = 0;
	String msg = "";
	
	try{
		
		User userActive = (User) request.getSession().getAttribute("user");
		
		sDate = startDate.split("/");
		conn = new DBCPConnectionProvider().getConnection(conn);
		
		// Begin transaction
		conn.setAutoCommit(false);
		
		// Query from source data.
		whereCause.delete(0, whereCause.length());
		whereCause.append(" AND DAY = '" + sDate[0] + "'");
		whereCause.append(" AND MONTH = '" + sDate[1] + "'");
		whereCause.append(" AND YEAR = '" + sDate[2] + "'");

		results = new MTrip().search(whereCause.toString());
		if(results != null){
			for(Trip t : results){
				eDate = endDate.split("/");
				// Check duplicate data in destination.
				whereCause.delete(0, whereCause.length());
				whereCause.append(" AND DAY = '" + eDate[0] + "'");
				whereCause.append(" AND MONTH = '" + eDate[1] + "'");
				whereCause.append(" AND YEAR = '" + eDate[2] + "'");
				
				results2 = new MTrip().search(whereCause.toString());
				if(results2 != null){
					String id = "";
					for(Trip t2 : results2){
						// Delete data if source is duplicate in destination
						id = "," + t2.getId();
					}	
					id = id.substring(1, id.length());
					new MTrip().delete(id, conn);
				}
				// Insert data from source to desctination.
				trip = new Trip();
				trip.setYear(eDate[2]);
				trip.setMonth(eDate[1]);
				trip.setDay(eDate[0]);
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