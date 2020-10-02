package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.CustomerSequence;
import com.pens.util.seq.SequenceProcessAll;

/**
 * Customer Sequence Process
 * 
 * @author Atiz.b
 * @version $Id: MCustomerSequence.java,v 1.0 22/12/2010 00:00:00 atiz.b Exp $
 * 
 * 
 */
public class MCustomerSequence extends I_Model<CustomerSequence> {

	private static final long serialVersionUID = 1296968014962453055L;
	public static String TABLE_NAME = "c_customer_sequence";
	public static String COLUMN_ID = "CUSTOMER_SEQUENCE_ID";

	// Column Sales Online Side active
	private String[] columnsSave = { COLUMN_ID, "TERRITORY", "PROVINCE", "DISTRICT", "CURRENT_NEXT", "UPDATED_BY" };

	// Column Sales Online Side active
	private String[] columnsUpdate = { COLUMN_ID, "CURRENT_NEXT", "UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public CustomerSequence find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, CustomerSequence.class);
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public CustomerSequence[] search(String whereCause) throws Exception {
		List<CustomerSequence> pos = search(TABLE_NAME, COLUMN_ID, whereCause, CustomerSequence.class);
		if (pos.size() == 0) return null;
		CustomerSequence[] array = new CustomerSequence[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param customerSequence
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(CustomerSequence customerSequence, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (customerSequence.getId() == 0) {
			id = SequenceProcessAll.getIns().getNextValueInt(TABLE_NAME);
		} else {
			id = customerSequence.getId();
		}
		Object[] values = { id, customerSequence.getTerritory(), customerSequence.getProvince(),
				customerSequence.getDistrict(), customerSequence.getCurrentNext(), activeUserID };
		if (super.save(TABLE_NAME, columnsSave, values, customerSequence.getId(), conn)) {
			customerSequence.setId(id);
		}
		return true;
	}

	/**
	 * Update Next Value
	 * 
	 * @param customerSequence
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean update(CustomerSequence customerSequence, int activeUserID, Connection conn) throws Exception {
		Object[] values = { customerSequence.getId(), customerSequence.getCurrentNext(), activeUserID };
		return super.save(TABLE_NAME, columnsUpdate, values, customerSequence.getId(), conn);
	}
}
