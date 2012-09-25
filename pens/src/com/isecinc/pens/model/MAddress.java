package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.process.SequenceProcess;

/**
 * MAddress Class
 * 
 * @author atiz.b
 * @version $Id: MAddress.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MAddress extends I_Model<Address> {

	private static final long serialVersionUID = -4119649113903274964L;

	public static String TABLE_NAME = "m_address";
	public static String COLUMN_ID = "ADDRESS_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "LINE1", "LINE2", "LINE3", "LINE4", "PROVINCE_ID", "DISTRICT_ID",
			"POSTAL_CODE", "PURPOSE", "CUSTOMER_ID", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "PROVINCE_NAME" , "REFERENCE_ID" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Address find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Address.class);
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
	public Address[] search(String whereCause) throws Exception {
		List<Address> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Address.class);
		if (pos.size() == 0) return null;
		Address[] array = new Address[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param address
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(Address address, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (address.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = address.getId();
		}
		Object[] values = { id, ConvertNullUtil.convertToString(address.getLine1()).trim(),
				ConvertNullUtil.convertToString(address.getLine2()).trim(),
				ConvertNullUtil.convertToString(address.getLine3()).trim(),
				ConvertNullUtil.convertToString(address.getLine4()).trim(), address.getProvince().getId(),
				address.getDistrict().getId(), ConvertNullUtil.convertToString(address.getPostalCode()).trim(),
				ConvertNullUtil.convertToString(address.getPurpose()).trim(), address.getCustomerId(),
				address.getIsActive() != null ? address.getIsActive() : "N", activeUserID, activeUserID,
				ConvertNullUtil.convertToString(address.getProvince().getName()) ,address.getReferenceId()==0?null:address.getReferenceId() };
		if (super.save(TABLE_NAME, columns, values, address.getId(), conn)) {
			address.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<Address> lookUp(int customerId) {
		List<Address> pos = new ArrayList<Address>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + customerId + " ORDER BY ADDRESS_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Address.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
