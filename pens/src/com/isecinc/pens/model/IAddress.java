package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.dataimports.bean.IAddressBean;

/**
 * ICustomer Class
 * 
 * @author Danai.K
 * @version $Id: ICustomer.java,v 1.0 20/12/2010 00:00:00 Danai.K Exp $
 */

public class IAddress extends I_Model<IAddressBean> {

	private static final long serialVersionUID = -1170650417151328865L;

	public static String TABLE_NAME = "i_address";
	public static String COLUMN_ID = "IADDRESS_ID";

	// Column Sales Online Side active
	private String[] columns = {
		COLUMN_ID, "LINE1", "LINE2", "LINE3", "LINE4", "DISTRICT_ID", 
		"PROVINCE_ID", "PROVINCE_NAME", "POSTAL_CODE", "COUNTRY", 
		"PURPOSE", "ICUSTOMER_ID", "IMPORTED", "IMPORTED_DETAIL", 
		/*"ISACTIVE",*/ /*"CREATED",*/ "CREATED_BY", /*"UPDATED",*/ "UPDATED_BY"
	};

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public IAddressBean find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, IAddressBean.class);
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
	public IAddressBean[] search(String whereCause) throws Exception {
		List<IAddressBean> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, IAddressBean.class);
		if (pos.size() == 0) return null;
		IAddressBean[] array = new IAddressBean[pos.size()];
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
	public boolean save(IAddressBean address, int activeUserID, Connection conn) throws Exception {
		/*int id = 0;
		if (address.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = address.getId();
		}*/
		int id = address.getId();

		Object[] values = {
			id, ConvertNullUtil.convertToString(address.getLine1()), ConvertNullUtil.convertToString(address.getLine2()), ConvertNullUtil.convertToString(address.getLine3()), ConvertNullUtil.convertToString(address.getLine4()), address.getDistrict().getId(), 
			address.getProvince().getId(), ConvertNullUtil.convertToString(address.getProvinceName()), ConvertNullUtil.convertToString(address.getPostalCode()), ConvertNullUtil.convertToString(address.getCountry()), 
			ConvertNullUtil.convertToString(address.getPurpose()), address.getCustomerId(), ConvertNullUtil.convertToString(address.getImported()), ConvertNullUtil.convertToString(address.getImportedDetail()), 
			/*address.getIsActive(),*/ /*address.getCreated(),*/ activeUserID, /*address.getUpdated(),*/ activeUserID
		};

		// check duplicate
		//if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "ICUSTOMER_ID", address.getCustomerId()+"", 0, conn)) {
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, COLUMN_ID, address.getId()+"", 0, conn)) {
			//update
			super.save(TABLE_NAME, columns, values, address.getId(), conn);
		} else {
			//insert
			super.save(TABLE_NAME, columns, values, 0, conn);
		}

		return true;
	}
}
