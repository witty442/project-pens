package com.isecinc.pens.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Contact;
import com.pens.util.seq.SequenceProcess;

/**
 * MContact Class
 * 
 * @author atiz.b
 * @version $Id: MContact.java,v 1.0 13/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MContact extends I_Model<Contact> {

	private static final long serialVersionUID = -4119649113903274964L;

	public static String TABLE_NAME = "m_contact";
	public static String COLUMN_ID = "CONTACT_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "CONTACT_TO", "RELATION", "PHONE", "FAX", "CUSTOMER_ID", "ISACTIVE",
			"CREATED_BY", "UPDATED_BY", "PHONE2", "MOBILE", "MOBILE2", "PHONE_SUB1", "PHONE_SUB2" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Contact find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Contact.class);
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
	public Contact[] search(String whereCause) throws Exception {
		List<Contact> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Contact.class);
		if (pos.size() == 0) return null;
		Contact[] array = new Contact[pos.size()];
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
	public boolean save(Contact contact, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (contact.getId() == 0) {
			id = SequenceProcess.getNextValueInt(TABLE_NAME);
		} else {
			id = contact.getId();
		}
		Object[] values = { id, ConvertNullUtil.convertToString(contact.getContactTo()).trim(),
				ConvertNullUtil.convertToString(contact.getRelation()).trim(),
				ConvertNullUtil.convertToString(contact.getPhone()).trim(),
				ConvertNullUtil.convertToString(contact.getFax()).trim(), contact.getCustomerId(),
				contact.getIsActive() != null ? contact.getIsActive() : "N", activeUserID, activeUserID,
				ConvertNullUtil.convertToString(contact.getPhone2()).trim(),
				ConvertNullUtil.convertToString(contact.getMobile()).trim(),
				ConvertNullUtil.convertToString(contact.getMobile2()).trim(),
				ConvertNullUtil.convertToString(contact.getPhoneSub1()).trim(),
				ConvertNullUtil.convertToString(contact.getPhoneSub2()).trim() };
		if (super.save(TABLE_NAME, columns, values, contact.getId(), conn)) {
			contact.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<Contact> lookUp(long customerId) {
		List<Contact> pos = new ArrayList<Contact>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + customerId + " ORDER BY CONTACT_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Contact.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
