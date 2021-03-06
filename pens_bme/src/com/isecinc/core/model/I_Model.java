package com.isecinc.core.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.Database;
import com.pens.util.DBConnection;

/**
 * I_Model Class
 * 
 * @author Atiz.b
 * @version $Id: I_Model.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public abstract class I_Model<T> implements Serializable {

	private static final long serialVersionUID = -5758544910967488311L;

	/** Logger */
	protected static Logger logger = Logger.getLogger("PENS");

	/**
	 * Find
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	protected T find(String id, String tableName, String columnID, Class<T> classes) throws Exception {
		// logger.debug("Find " + this.getClass());
		Connection conn = null;
		T t = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			String sql = "SELECT * FROM " + tableName + " WHERE " + columnID + " = ? ";
			List<T> ts = Database.query(sql, new Object[] { id }, classes, conn);
			if (ts.size() > 0) {
				t = ts.get(0);
			}
			return t;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @param whereCause
	 * @param classes
	 * @return
	 * @throws Exception
	 */
	protected List<T> search(String tableName, String columnID, String whereCause, Class<T> classes) throws Exception {
		// logger.debug("Search " + this.getClass());
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			String sql = "SELECT * FROM " + tableName + " WHERE 1 = 1 " + (whereCause.length() > 0 ? whereCause : "");
			logger.debug("sql:"+sql);
			List<T> ts = Database.query(sql, null, classes, conn);
			return ts;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}
	
	protected List<T> search(String tableName, String columnID, String whereCause,String orderBy, Class<T> classes) throws Exception {
		// logger.debug("Search " + this.getClass());
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			String sql = "SELECT * FROM " + tableName + " WHERE 1 = 1 " + (whereCause.length() > 0 ? whereCause : ""+" order by "+orderBy);
			logger.debug("sql:"+sql);
			List<T> ts = Database.query(sql, null, classes, conn);
			return ts;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
	}

	/**
	 * Save
	 * 
	 * @param tableName
	 * @param columnID
	 * @param columns
	 * @param values
	 * @param valueID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected boolean save(String tableName, String[] columns, Object[] values, int valueID, Connection conn)
			throws Exception {
		logger.debug("Save " + this.getClass());
		if (valueID == 0) return saveNew(tableName, columns, values, valueID, conn);
		else return saveUpdate(tableName, columns, values, valueID, conn);
	}

	/**
	 * Save New
	 * 
	 * @param tableName
	 * @param columnID
	 * @param columns
	 * @param values
	 * @param valueID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected boolean saveNew(String tableName, String[] columns, Object[] values, int valueID, Connection conn)
			throws Exception {
		PreparedStatement pstmt = null;
		try {
			logger.debug("Save New " + this.getClass());
			String col = "";
			String val = "";
			for (String s : columns) {
				col += ", " + s;
				val += ", ?";
			}
			col += ", UPDATED";
			val += ", CURRENT_TIMESTAMP";
			col = col.substring(1).trim();
			val = val.substring(1).trim();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO " + tableName);
			sql.append("(" + col + ") ");
			sql.append("VALUES(" + val + ") ");
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			int i = 1;
			for (Object b : values) {
				pstmt.setObject(i++, b);
			}
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e2) {}
		}
		return true;
	}

	/**
	 * Save Update
	 * 
	 * @param tableName
	 * @param columnID
	 * @param columns
	 * @param values
	 * @param valueID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private boolean saveUpdate(String tableName, String[] columns, Object[] values, int valueID, Connection conn)
			throws Exception {
		logger.debug("Save Update " + this.getClass());
		PreparedStatement pstmt = null;
		try {
			StringBuilder sql = new StringBuilder();
			String col = "";
			int skipColumn = 0;
			// skip ID column
			for (int i = 1; i < columns.length; i++) {
				if (!columns[i].equalsIgnoreCase("CREATED_BY")) {
					col += ", " + columns[i] + " = ? ";
				} else {
					skipColumn = i;
				}
			}
			col = col.substring(1).trim();
			sql.append("UPDATE " + tableName);
			sql.append(" SET " + col);
			sql.append(",UPDATED = CURRENT_TIMESTAMP ");
			sql.append(" WHERE " + columns[0] + " = ?");
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			int j = 1;
			for (int i = 1; i < values.length; i++) {
				if (i != skipColumn) pstmt.setObject(j++, values[i]);
			}
			// add id value
			pstmt.setObject(j++, values[0]);
			int u = pstmt.executeUpdate();
			
			logger.debug("update:"+u);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				pstmt.close();
			} catch (Exception e2) {}
		}
		return true;
	}

	/**
	 * Change Active
	 * 
	 * @param tableName
	 * @param columnID
	 * @param active
	 * @param ids
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	protected boolean changeActive(String tableName, String columnID, String active, String[] ids, int activeUserID,
			Connection conn) throws Exception {
		logger.debug("Change Active " + this.getClass());
		PreparedStatement pstmt = null;
		String id = "";
		try {
			for (String s : ids)
				id += "," + s;
			id = id.substring(1);
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE " + tableName +" SET \n");
			if(active.equals("Y")){
				 sql.append(" END_DATE = NULL \n");
			}else{
				 sql.append(" END_DATE = to_date(to_char(SYSDATE-1,'dd/mm/yyyy'),'dd/mm/yyyy') \n");	
			}
			sql.append(", UPDATED = CURRENT_TIMESTAMP \n");
			sql.append(", UPDATED_BY = " + activeUserID +"\n");
			sql.append(" WHERE " + columnID + " IN (" + id + ") \n");
			logger.debug(sql.toString());
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	/**
	 * Delete
	 * 
	 * @param tableName
	 * @param columnId
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String tableName, String columnId, String deleteId, Connection conn) throws Exception {
		logger.debug("Delete " + this.getClass());
		Statement stmt = null;
		try {
			String sql = "DELETE FROM " + tableName + " WHERE " + columnId + " IN (" + deleteId + ")";
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				stmt.close();
			} catch (Exception e) {}
		}
		return true;
	}

	/**
	 * Check Duplicate Document
	 * 
	 * @param tableName
	 * @param columnId
	 * @param columnDoc
	 * @param documentNo
	 * @param id
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean checkDocumentDuplicate(String tableName, String columnId, String columnDoc, String documentNo,
			int id, Connection conn) throws Exception {
		logger.debug(String.format("Check Duplicate %s[%s] - %s[%s]", tableName, columnId, columnDoc, documentNo));
		Statement stmt = null;
		ResultSet rst = null;
		try {
			int tot = 0;
			stmt = conn.createStatement();
			String sql = "SELECT COUNT(*) as TOT FROM " + tableName;
			sql += " WHERE " + columnDoc + "='" + documentNo + "' ";
			if (id != 0) sql += "  AND " + columnId + "<> '" + id + "' ";
			logger.debug(sql);
			rst = stmt.executeQuery(sql);
			if (rst.next()) {
				tot = rst.getInt("TOT");
			}
			if (tot > 0) return false;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e) {}
		}
		return true;
	}

	/**
	 * Look Up
	 * 
	 * @return
	 */
	public List<T> lookUp() {
		return null;
	}

}
