package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.DBCPConnectionProvider;

import com.isecinc.core.Database;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.DocSequence;
import com.isecinc.pens.bean.DocType;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.process.SequenceProcess;

/**
 * DocSequence Model
 * 
 * @author Atiz.b
 * @version $Id: MDocSequence.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 *          atiz.b : change new doctype created way.. seperate by user type
 * 
 */
public class MDocSequence extends I_Model<DocSequence> {

	private static final long serialVersionUID = 1640780528117076239L;

	public static String TABLE_NAME = "c_doctype_sequence";
	public static String COLUMN_ID = "DOCTYPE_SEQUENCE_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "DOCTYPE_ID", "SALES_CODE", "ORDER_TYPE", "START_NO", "CURRENT_NEXT",
			"CURRENT_YEAR", "CURRENT_MONTH", "ISACTIVE" };
	
	private String[] columnsInsert = { COLUMN_ID, "DOCTYPE_ID", "SALES_CODE", "ORDER_TYPE", "START_NO", "CURRENT_NEXT",
			"CURRENT_YEAR", "CURRENT_MONTH", "ISACTIVE" ,"UPDATED_BY"};

	// Column Sales Online Side active
	private String[] columnsSave = { COLUMN_ID, "START_NO", "CURRENT_NEXT", "UPDATED_BY" };

	// Column Sales Online Side active
	private String[] columnsUpdate = { COLUMN_ID, "CURRENT_NEXT", "CURRENT_YEAR", "CURRENT_MONTH", "UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DocSequence find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, DocSequence.class);
	}

	/**
	 * Generate SEQ
	 * 
	 * @param activeUser
	 * @return
	 * @throws Exception
	 */
	public boolean genSeq(User activeUser) throws Exception {
		Connection conn = null;
		try {
			String salesCode = activeUser.getCode();

			// get Current Run of Sales
			String whereCause = "AND SALES_CODE = '" + salesCode + "' ";
			List<DocSequence> docSeqs = search(TABLE_NAME, COLUMN_ID, whereCause, DocSequence.class);

			// get all document active
			List<DocType> docTypes = new MDocType().getActiveDocument();

			// get only lacked seq
			for (DocSequence d : docSeqs) {
				for (DocType dt : docTypes) {
					if (d.getDoctypeID() == dt.getId()) {
						docTypes.remove(dt);
						break;
					}
				}
			}
			if (docTypes.size() == 0) return true;

			conn = new DBCPConnectionProvider().getConnection(conn);
			// begin trans
			conn.setAutoCommit(false);
			// create new DocSeq
			int id = 0;
			Object[] values = null;
			for (DocType dt : docTypes) {
				// TT Doc Sequence
				if (activeUser.getType().equalsIgnoreCase(User.TT)) {
					if (dt.getName().equalsIgnoreCase(DocSequence.ORDER_NUMNER)
							|| dt.getName().equalsIgnoreCase(DocSequence.RECEIPT_NUMNER)
							|| dt.getName().equalsIgnoreCase(DocSequence.VISIT_NUMBER)) {
						// get id
						id = SequenceProcess.getNextValue(TABLE_NAME);
						values = new Object[] { id, dt.getId(), salesCode, "MM", 1, 1, Database.getCurrentYear(),
								Database.getCurrentMonth(), "Y" };
						// set id = 0 for create new record
						super.save(TABLE_NAME, columns, values, 0, conn);
					}
				}
				// VAN Doc Sequence
				if (activeUser.getType().equalsIgnoreCase(User.VAN)) {
					if (dt.getName().equalsIgnoreCase(DocSequence.CUSTOMER_NUMBER)) {
						// get id
						id = SequenceProcess.getNextValue(TABLE_NAME);
						values = new Object[] { id, dt.getId(), salesCode, "", 1, 1, Database.getCurrentYear(),
								Database.getCurrentMonth(), "Y" };
						// set id = 0 for create new record
						super.save(TABLE_NAME, columns, values, 0, conn);
					}
					if (dt.getName().equalsIgnoreCase(DocSequence.ORDER_NUMNER)
							|| dt.getName().equalsIgnoreCase(DocSequence.RECEIPT_NUMNER)
							|| dt.getName().equalsIgnoreCase(DocSequence.VISIT_NUMBER)) {
						// get id
						id = SequenceProcess.getNextValue(TABLE_NAME);
						values = new Object[] { id, dt.getId(), salesCode, "MM", 1, 1, Database.getCurrentYear(),
								Database.getCurrentMonth(), "Y" };
						// set id = 0 for create new record
						super.save(TABLE_NAME, columns, values, 0, conn);
					}
				}
				// DD Doc Sequence
				if (activeUser.getType().equalsIgnoreCase(User.DD)) {
					if (dt.getName().equalsIgnoreCase(DocSequence.MEMBER_NUMBER)) {
						// get id
						id = SequenceProcess.getNextValue(TABLE_NAME);
						values = new Object[] { id, dt.getId(), "", "", 1, 1, Database.getCurrentYear(),
								Database.getCurrentMonth(), "Y" };
						// set id = 0 for create new record
						super.save(TABLE_NAME, columns, values, 0, conn);
					}
					if (dt.getName().equalsIgnoreCase(DocSequence.ORDER_NUMNER)
							|| dt.getName().equalsIgnoreCase(DocSequence.RECEIPT_NUMNER)) {
						// get id
						id = SequenceProcess.getNextValue(TABLE_NAME);
						values = new Object[] { id, dt.getId(), "", "MM", 1, 1, Database.getCurrentYear(),
								Database.getCurrentMonth(), "Y" };
						// set id = 0 for create new record
						super.save(TABLE_NAME, columns, values, 0, conn);
					}
				}

			}
			// commit
			conn.commit();
			//
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e2) {}
			throw e;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (Exception e2) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return true;
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public DocSequence[] search(String whereCause) throws Exception {
		List<DocSequence> pos = search(TABLE_NAME, COLUMN_ID, whereCause, DocSequence.class);
		if (pos.size() == 0) return null;
		DocSequence[] array = new DocSequence[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save Update
	 * 
	 * @param values
	 * @param valueID
	 * @param conn
	 * @return
	 * @throws Exception
	 */

	public boolean save(DocSequence docSequence, int activeUserID, Connection conn) throws Exception {
		Object[] values = { docSequence.getId(), docSequence.getStartNo(), docSequence.getCurrentNext(), activeUserID };
		return super.save(TABLE_NAME, columnsSave, values, docSequence.getId(), conn);
	}

	
	public boolean saveNew(DocSequence docSequence, int activeUserID, Connection conn) throws Exception {
		docSequence.setId(SequenceProcess.getNextValue(TABLE_NAME));
		Object[] values = { docSequence.getId(),docSequence.getDoctypeID(),docSequence.getSalesCode(),docSequence.getOrderType(),
				            docSequence.getStartNo(),docSequence.getCurrentNext(),
				            docSequence.getCurrentYear(),docSequence.getCurrentMonth(), docSequence.getActive() ,activeUserID };
		return super.save(TABLE_NAME, columnsInsert, values, 0, conn);
	}
	/**
	 * Update Next Seq
	 * 
	 * @param docSequence
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean update(DocSequence docSequence, int activeUserID, Connection conn) throws Exception {
		Object[] values = { docSequence.getId(), docSequence.getCurrentNext(), docSequence.getCurrentYear(),
				docSequence.getCurrentMonth(), activeUserID };
		return super.save(TABLE_NAME, columnsUpdate, values, docSequence.getId(), conn);
	}
}
