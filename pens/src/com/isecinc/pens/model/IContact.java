package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.dataimports.bean.IContactBean;

/**
 * ICustomer Class
 * 
 * @author Danai.K
 * @version $Id: ICustomer.java,v 1.0 20/12/2010 00:00:00 Danai.K Exp $
 */

public class IContact extends I_Model<IContactBean> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "i_contact";
	public static String COLUMN_ID = "ICONTACT_ID";

	// Column Sales Online Side active
	private String[] columns = {
		COLUMN_ID, "ICUSTOMER_ID", "CONTACT_TO", "RELATION", "PHONE", "PHONE2", 
		"MOBILE", "MOBILE2", "FAX", "IMPORTED", "IMPORTED_DETAIL", 
		/*"ISACTIVE",*/ /*"CREATED",*/ "CREATED_BY", /*"UPDATED",*/ "UPDATED_BY"
	};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public IContactBean find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, IContactBean.class);
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
	public IContactBean[] search(String whereCause) throws Exception {
		List<IContactBean> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, IContactBean.class);
		if (pos.size() == 0) return null;
		IContactBean[] array = new IContactBean[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param contact
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(IContactBean contact, int activeUserID, Connection conn) throws Exception {
		/*int id = 0;
		if (contact.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = contact.getId();
		}*/
		int id = contact.getId();

		Object[] values = {
			id, contact.getCustomerId(), ConvertNullUtil.convertToString(contact.getContactTo()), ConvertNullUtil.convertToString(contact.getRelation()), ConvertNullUtil.convertToString(contact.getPhone()), ConvertNullUtil.convertToString(contact.getPhone2()), 
			ConvertNullUtil.convertToString(contact.getMobile()), ConvertNullUtil.convertToString(contact.getMobile2()), ConvertNullUtil.convertToString(contact.getFax()), ConvertNullUtil.convertToString(contact.getImported()), ConvertNullUtil.convertToString(contact.getImportedDetail()), 
			/*contact.getIsActive(),*/ /*contact.getCreated(),*/ activeUserID, /*contact.getUpdated(),*/ activeUserID
		};

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "ICUSTOMER_ID", contact.getCustomerId()+"", 0, conn)) {
			//update
			super.save(TABLE_NAME, columns, values, contact.getId(), conn);
		} else {
			//insert
			super.save(TABLE_NAME, columns, values, 0, conn);
		}

		return true;
	}
}
