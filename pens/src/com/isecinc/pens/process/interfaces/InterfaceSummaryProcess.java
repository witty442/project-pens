package com.isecinc.pens.process.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.DBCPConnectionProvider;

import com.isecinc.pens.bean.InterfaceSummary;

/**
 * Interfaces Process Class
 * 
 * @author Atiz.b
 * @version $Id: InterfaceStatusProcess.java,v 1.0 7/12/2010 00:00:00 atiz.b Exp $
 * 
 */
public abstract class InterfaceSummaryProcess {

	Logger log = Logger.getLogger("PENS");

	/**
	 * Get Summary Interfaces
	 * 
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public List<InterfaceSummary> getSummaryInterfaces(InterfaceSummary criteria) throws Exception {
		List<InterfaceSummary> pos = new ArrayList<InterfaceSummary>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String sql = createSQL(criteria);
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			pos = getResult(rst);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return pos;
	}

	/**
	 * Create SQL
	 * 
	 * @param criteria
	 * @return
	 */
	protected abstract String createSQL(InterfaceSummary criteria) throws Exception;

	/**
	 * Get Result
	 * 
	 * @param rst
	 * @return
	 * @throws Exception
	 */
	protected abstract List<InterfaceSummary> getResult(ResultSet rst) throws Exception;

}
