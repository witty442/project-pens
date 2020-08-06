package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Trip;
import com.isecinc.pens.inf.helper.Utils;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.seq.SequenceProcess;

/**
 * MTrip Class
 * 
 * @author Witty.B
 * @version $Id: MTrip.java,v 1.0 07/10/2010 00:00:00 witty.B Exp $
 * 
 *          * Modifier : A-neak.t 21/10/2010 Edit variable name, Add method Save.
 */

public class MTrip extends I_Model<Trip> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "m_trip";
	public static String COLUMN_ID = "TRIP_ID";

	// Column Trip
	private String[] columns = { COLUMN_ID, "YEAR", "MONTH", "DAY", "CUSTOMER_ID", "USER_ID", "LINE_NO", "CREATED_BY",
			"UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Trip find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Trip.class);
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public Trip[] search(String whereCause) throws Exception {
		List<Trip> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Trip.class);
		if (pos.size() == 0) return null;
		Trip[] array = new Trip[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param customer
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 * */
	public boolean save(Trip trip, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (trip.getId() == 0) {
			id = SequenceProcess.getNextValueInt(TABLE_NAME);
		} else {
			id = trip.getId();
		}
		Object[] values = { id, trip.getYear(), trip.getMonth(), trip.getDay(), trip.getCustomer().getId(),
				trip.getUser().getId(), trip.getLineNo(), activeUserID, activeUserID };

		if (super.save(TABLE_NAME, columns, values, trip.getId(), conn)) {
			trip.setId(id);
		}

		return true;
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}

	private static String SELECT_USER = "select distinct(user_id) from m_customer where customer_type <> 'DD' ";

	private static String ADJUST_TRIP = "delete from m_trip \r\n" + "where user_id = ? \r\n"
			+ "  and CUSTOMER_ID not in (\r\n" + "    select customer_id from m_customer where user_id = ? \r\n"
			+ "  )\r\n";

	/**
	 * Adjust Trip
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean adjustTrip() throws Exception {
		Connection conn = null;
		ResultSet rst = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			logger.debug("---------------------------");
			logger.debug("begin!");
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			// GET USER_ID
			logger.debug("---------------------------");
			logger.debug("Get User from m_customer");
			logger.debug("---------------------------");
			String sql = SELECT_USER;
			rst = stmt.executeQuery(sql);
			List<Integer> userIds = new ArrayList<Integer>();
			while (rst.next()) {
				userIds.add(rst.getInt("USER_ID"));
			}
			if (userIds.size() == 0) return true;

			for (Integer id : userIds) {
				logger.debug(String.format("UserID:[%s]", id));
				logger.debug("---------------------------");
				logger.debug("Adjust Trip!!!");
				logger.debug("---------------------------");
				sql = ADJUST_TRIP;
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, id);
				pstmt.setInt(2, id);
				logger.debug(sql);
				pstmt.execute();
			}
			logger.debug("commit!");
			logger.debug("---------------------------");
			conn.commit();
			return true;
		} catch (Exception e) {
			logger.error("fail!");
			logger.error("---------------------------");
			logger.error(e.toString());
			e.printStackTrace();
			throw e;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				pstmt.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	public static List<String> getYearTripList() throws Exception {
		Connection conn = null;
		ResultSet rst = null;
		PreparedStatement pstmt = null;
		List<String> yearTripList  = new ArrayList<String>();
		int  i=0;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			pstmt = conn.prepareStatement("select distinct year  from m_trip where 1=1 order by year desc");
			rst = pstmt.executeQuery();
			
			String currentYear = Utils.stringValue(new Date(), "yyyy",new  Locale("TH","th"));
			System.out.println("year:"+currentYear);
			
			while(rst.next()){
				
				if(i==0 && !currentYear.equals(rst.getString("year"))){
				   yearTripList.add(currentYear);
				   yearTripList.add(rst.getString("year"));
				}else{
				   yearTripList.add(rst.getString("year"));
				}
				i++;
			}
			if(yearTripList != null && yearTripList.size()==0){
				
				yearTripList.add(currentYear);
			}
			return yearTripList;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				pstmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	public static int delete(Connection conn,String month,String year) throws Exception {
        int resultDel = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("delete from m_trip where month ='"+month+"' and year='"+year+"'");
			
			resultDel = pstmt.executeUpdate();
			return resultDel;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e2) {}
			
		}
	}
}
